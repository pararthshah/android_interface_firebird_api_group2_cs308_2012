/*
*	The main code which runs on the Firebird
*/

#include<avr/io.h>
#include<avr/interrupt.h>
#include<util/delay.h>
#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#define WHEEL_DIST 15 // in cm
#include <math.h> //included to support power function

#define SUCCESS 1

#include "lcd2.c"
#include "lcd.c"
#include "buzzer.c"
#include "timers.c"
#include "motion.c"
#include "communication.c"
#include "sensors.c"
#include "isr.c"
#include "whiteline.c"
#include "pos_control.c"
#include "acc.c"

#define FCPU 11059200ul //defined here to make sure that program works properly
#define DIST_PER_TICK 0.544
#define TICKS_PER_SEC 11059

//our defines
#define SIZE_OF_FLAG_ARRAY 10

//Function prototypes and global variables
void port_init();
void velocity(unsigned char, unsigned char);
void motors_delay();
int _rollLCDFlag,_LCDpos;
unsigned char ADC_Conversion(unsigned char);
unsigned char ADC_Value;
unsigned char flag1 = 0;
unsigned char flag2 = 0;
unsigned char Left_white_line = 0;
unsigned char Center_white_line = 0;
unsigned char Right_white_line = 0;
unsigned char Front_Sharp_Sensor=0;
unsigned char Front_IR_Sensor=0;
extern unsigned char already_stopped;
extern unsigned char already_modified_stopped;

//our variables
int flags[SIZE_OF_FLAG_ARRAY];
//functions[];

unsigned long long int curLeftCounter,curRightCounter,splLeftCounter,splRightCounter,functionFlag=0;
unsigned char leftVel,rightVel;
float botRatio,botAngle,botDistance;
unsigned char data;
char _rollLCD[100];
unsigned int leftInt=0,rightInt=0;

//Function to configure LDD bargraph display
void LED_bargraph_config (void)
{
 DDRJ = 0xFF;  //PORT J is configured as output
 PORTJ = 0x00; //Output is set to 0
}


//! A basic-initialization function.
    /*!
	  the function to initialize ports for use of basic functionalities on the Bot
    */
//Function to Initialize PORTS
void port_init()
{
	lcd_port_config();
	adc_pin_config();
	motion_pin_config();
	left_encoder_pin_config();
	right_encoder_pin_config();
	buzzer_pin_config();
	//LED_bargraph_config();
}

int c=0;

//! initialize all devices
    /*!
	 initialize all devices. call all other initialization function
    */
void init_devices (void)
{
	cli(); //Clears the global interrupts
	port_init();
	adc_init();
	uart3_init();
	timer5_init();
	timer4_init();
	TCCR4B = 0x00;
	TIMSK4 = 0x01; //timer4 overflow interrupt enable
	TIMSK3 = 0x01;
	sei();   //Enables the global interrupts
}

/*	Command codes for the various commands that the firebird can accept
*/
#define BUZZER_ON 65
#define BUZZER_OFF 66
#define MOVE_FORWARD 67
#define MOVE_BACKWARD 68
#define MOVE_RIGHT 69
#define MOVE_LEFT 70
#define STOP 71

#define WHITELINE_FOLLOW_START 74
#define WHITELINE_FOLLOW_END 75
#define WHITELINE_STOP_INTERSECTION 76
#define ACC_START 77
#define ACC_STOP 78
#define ACC_CHECK 79
#define ACC_MODIFIED 80
#define PRINT_STATE 90
#define DISCONNECT 120

#define LCD_SET_STRING 32
#define SET_PORT 33
#define GET_SENSOR_VALUE 34
#define GET_PORT 35
#define SET_VELOCITY 36
#define MOVE_BY 37
#define TURN_LEFT_BY 38
#define TURN_RIGHT_BY 39
#define MOVE_BACK_BY 40

#define GET_LEFT_WHEEL_INTERRUPT_COUNT 81
#define GET_RIGHT_WHEEL_INTERRUPT_COUNT 82
#define ENABLE_LEFT_WHEEL_INTERRUPT 83
#define ENABLE_RIGHT_WHEEL_INTERRUPT 84

#define SET_TIMER 85

// Global flags denoting the state of the firebird
int command_rcvd = 0;

unsigned char white_line_flag = 0;
unsigned char whiteline_stop_intersection_flag = 0;

unsigned int ReqdShaftCountInt = 0;
extern unsigned long int ShaftCountRight;
int not_reached_goal = 0 ;

unsigned char acc_flag = 0;
unsigned char acc_modified_flag = 0;
extern unsigned char already_modified_stopped;

//Received data buffer
unsigned char rcvd_data[1024];
int rcvd_data_start = 0, rcvd_data_end = 0;

//Received commands buffer
unsigned char command_buf[1024];
int command_buf_start = 0, command_buf_end = 0;

//Return the next byte from the command buffer
int get_char_from_input(unsigned char *c) {
	if (command_buf_start == command_buf_end) return -1;
	*c = command_buf[command_buf_start];
	command_buf_start++;
	if (command_buf_start == 1024) command_buf_start = 0;
	return 0;
}

//Return back N arguments to a command from the command buffer
//For a multibyte command the protocol is : <COMMAND> <NUM_BYTES_TO_FOLLOW> <BYTE_1> <BYTE_2> ..
// So, this function takes the next byte <N>, and then takes the next <N> bytes from the command buffer and returns it back as a string
unsigned char* recieve_args(int* size){
	unsigned char ch;
    int error = get_char_from_input(&ch);
    if(error == -1)
    {
   		lcd_clear();
		lcd_string("error");
    }
    unsigned int i = 0;
	*size = (unsigned int)ch;
	unsigned char* c = (unsigned char*)calloc(1,*size);
    for(;i< *size;i++)
    {
        error = get_char_from_input(c+i);
        if(error == -1)
        {   
   			lcd_clear();
			lcd_string("error");
        }
    }
    return c;
}

//Set a port to some specific value
void setPort(int portnum, unsigned char value){
	if(portnum==22) PORTA=value;
	else if(portnum==1) PORTB=value;
	else if(portnum==2) PORTC=value;
	else if(portnum==3) PORTD=value;
	else if(portnum==4) PORTE=value;
	else if(portnum==5) PORTF=value;
	else if(portnum==6) PORTG=value;
	else if(portnum==7) PORTH=value;
	else if(portnum==8) PORTJ=value;
	else if(portnum==9) PORTK=value;
	else if(portnum==10) PORTL=value;
	else if(portnum==11) DDRA=value;
	else if(portnum==12) DDRB=value;
	else if(portnum==13) DDRC=value;
	else if(portnum==14) DDRD=value;
	else if(portnum==15) DDRE=value;
	else if(portnum==16) DDRF=value;
	else if(portnum==17) DDRG=value;
	else if(portnum==18) DDRG=value;
	else if(portnum==19) DDRJ=value;
	else if(portnum==20) DDRK=value;
	else if(portnum==21) DDRL=value;
}

//Get the value from some port
void getPort(int portnum){
 
	unsigned char value;

	if(portnum==22) value=PORTA;
	else if(portnum==1) value=PORTB;
	else if(portnum==2) value=PORTC;
	else if(portnum==3) value=PORTD;
	else if(portnum==4) value=PORTE;
	else if(portnum==5) value=PORTF;
	else if(portnum==6) value=PORTG;
	else if(portnum==7) value=PORTH;
	else if(portnum==8) value=PORTJ;
	else if(portnum==9) value=PORTK;
	else if(portnum==10) value=PORTL;
	else if(portnum==11) value=DDRA;
	else if(portnum==12) value=DDRB;
	else if(portnum==13) value=DDRC;
	else if(portnum==14) value=DDRD;
	else if(portnum==15) value=DDRE;
	else if(portnum==16) value=DDRF;
	else if(portnum==17) value=DDRG;
	else if(portnum==18) value=DDRG;
	else if(portnum==19) value=DDRJ;
	else if(portnum==20) value=DDRK;
	else if(portnum==21) value=DDRL;
	send_char(value);
	//_delay_ms(1000);
}

//Get the value of some sensor
void getSensorValue(int sensornum){
	unsigned char value = ADC_Conversion(sensornum);
	 send_char(value);
}

void print_inputs ();

int stop_on_timer4_overflow = 0;

//Variables dictating the current state based on the protocol
int accept = 0;

#define START_BYTE 0
#define SIZE_BYTE 1
#define MULT_BYTE 2

int bytes_remaining = 1;


int state = START_BYTE;

int expected_127 = 3;

//Reset the firebird state - clear the buffers
void disconnect () {
	stop_on_timer4_overflow = 0;
	accept = 0;
	bytes_remaining = 1;
	state = START_BYTE;
	rcvd_data_start = 0;
	rcvd_data_end = 0;
	command_buf_start = 0;
	command_buf_end = 0;
	int i = 0;
	for (i = 0; i<1024; i++) {
		command_buf[i] = 0;
		rcvd_data[i] = 0;
	}
	expected_127 = 3;
	//char reboot_cmd[32] = "\r\nAT+DCON=2,E4:B0:21:CE:7D:78\r\n";

}

//The main invoker routine. It takes as argument the next command to execute and does what is necessary
//Self-explanatory code!
void my_invoker (unsigned char command) {
	if(command == BUZZER_ON){
		buzzer_on();
		return;
	}
	else if(command == BUZZER_OFF){
		buzzer_off();
		return;
	}
	else if(command == MOVE_FORWARD) 
    {
        forward();  //forward
        return;
    }

    else if(command == MOVE_BACKWARD)
    {
        back(); //back
        return;
    }

    else if(command == MOVE_LEFT) 
    {
        left();  //left
        return;
    }

    else if(command == MOVE_RIGHT)
    {
        right(); //right
        return;
    }

    else if(command == STOP) 
    {
        stop(); //stop
        return;
    }
	
	else if(command == SET_VELOCITY) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
        
		//assert(numargs == 1);

		int velleft = (int)*(ch);
		int velright = (int)*(ch+1);
		velocity(velleft,velright);

        return;
    }
	
	else if(command == MOVE_BY) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
		int pos_a = (int)*(ch);
		int pos_b = (int)*(ch+1);

		//int pos = 10;
		//while (pos_b--) pos *= 10;
		//pos *= pos_a;
		//forward_mm(pos);
		pos_a += (pos_b << 8);

		forward();
		velocity(120,120);

		while (pos_a--) {
			//delay on 5 ms
			stop_on_timer4_overflow = 1;
			start_timer4();
			while (stop_on_timer4_overflow != 0) {;}
		}
		stop();
		send_char(SUCCESS);		
		leftInt = 0;
		rightInt = 0;
		
		return;
    }

	else if(command == MOVE_BACK_BY) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
		int pos_a = (int)*(ch);
		int pos_b = (int)*(ch+1);

		//int pos = 10;
		//while (pos_b--) pos *= 10;
		//pos *= pos_a;
		//forward_mm(pos);
		pos_a += (pos_b << 8);

		back();
		velocity(120,120);

		while (pos_a--) {
			//delay on 5 ms
			stop_on_timer4_overflow = 1;
			start_timer4();
			while (stop_on_timer4_overflow != 0) {;}
		}
		stop();
		send_char(SUCCESS);		
		leftInt = 0;
		rightInt = 0;
		
		return;
    }
	
	else if(command == TURN_LEFT_BY) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
        already_stopped = 0;
		int pos_a = (int)*(ch);
		int pos_b = (int)*(ch+1);

		pos_a += (pos_b << 8);

		_delay_ms(500);
		left();
		velocity(200,200);

		while (pos_a--) {
			//delay on 5 ms
			stop_on_timer4_overflow = 1;
			start_timer4();
			while (stop_on_timer4_overflow != 0) {;}
		}
		stop();
		send_char(SUCCESS);		
		leftInt = 0;
		rightInt = 0;
		already_modified_stopped = 0;

        return;
    }

	else if(command == TURN_RIGHT_BY) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
        
		//assert(numargs == 2);

		int pos_a = (int)*(ch);
		int pos_b = (int)*(ch+1);

		pos_a += (pos_b << 8);

		_delay_ms(500);
		right();
		velocity(200,200);


		while (pos_a--) {
			//delay on 5 ms
			stop_on_timer4_overflow = 1;
			start_timer4();
			while (stop_on_timer4_overflow != 0) {;}
		}		

		stop();
		send_char(SUCCESS);
		leftInt = 0;
		rightInt = 0;
		already_modified_stopped = 0;
        return;
    }

    else if(command == LCD_SET_STRING) 
    {
        int numargs;
		unsigned char * ch = recieve_args(&numargs);
        
        int i =0;
		lcd_clear();
        for(;i<numargs;i++)
        {
            lcd_wr_char(*(ch+i));
        }
        return;
    }
	
	else if (command == SET_PORT){
    	int numargs;
    	unsigned char * ch = recieve_args(&numargs); ; 
    	if (numargs != 2){
   
	    }
    	int portnum = (int) *(ch);
    	unsigned char value = (unsigned char) *(ch+1); 
    
		setPort(portnum,value);
    }

    else if(command == GET_SENSOR_VALUE)
    {
    	int numargs;
    	unsigned char * ch = recieve_args(&numargs); ; 
    	if (numargs != 1){
   
	    }
    	int sensornum = (int) *(ch);
    
		//setPort(portnum,value);
		getSensorValue(sensornum);
       
    }
    else if(command == GET_PORT)
    {
      	int numargs;
    	unsigned char * ch = recieve_args(&numargs); ; 
    	if (numargs != 1){
   
	    }
    	int portnum = (int) *(ch); 
    
		getPort(portnum);
        
    }
    else if (command == WHITELINE_FOLLOW_START) {
		whiteline_follow_start();
	}
	else if(command == PRINT_STATE){
		buzzer_on();
		lcd_num(state);
		_delay_ms(1000);
		buzzer_off();
	}
	else if (command == WHITELINE_FOLLOW_END) {
		whiteline_follow_end();
	}
	else if (command == WHITELINE_STOP_INTERSECTION) {
		whiteline_stop_intersection_flag = 1;
	}
    else if(command == ACC_START) {
   		acc_flag = 1;
		
   
    }
	else if(command == ACC_STOP) {
		acc_flag = 0;
		acc_modified_flag = 0;
		buzzer_off();
	}
	else if(command == ACC_MODIFIED){
		acc_modified_flag = 1;
		already_modified_stopped = 0;
	}
	else if(command == ACC_CHECK){
		if (acc_modified_flag == 1 && already_modified_stopped == 1){
			send_char((char)1);
		}
		else {
			char value = PORTA;
			if (value == 0) send_char((char)2);
			else send_char((char)0);
		}
	}
	else if (command == ENABLE_LEFT_WHEEL_INTERRUPT) {
		leftInt = 0;
		left_position_encoder_interrupt_init();
	}
	else if (command == ENABLE_RIGHT_WHEEL_INTERRUPT) {
		rightInt = 0;
		right_position_encoder_interrupt_init();
	}
	else if (command == GET_LEFT_WHEEL_INTERRUPT_COUNT) {
		send_int (leftInt);
		leftInt = 0;
	}
	else if (command == GET_RIGHT_WHEEL_INTERRUPT_COUNT) {
		send_int (rightInt);
		rightInt = 0;
	}
	else if (command == SET_TIMER) {
	int numargs;
    	unsigned char * ch = recieve_args(&numargs); ; 
    	if (numargs != 1){
   
	    }
    	int time = (int) *(ch); 
    
		timer4_init2(time);
	}
	else if (command == DISCONNECT) {
		disconnect();
	}
	else { //Error!!! Unrecognized Command
		buzzer_on();
		_delay_ms(1000);
		buzzer_off();
	}
}

//Debugging function to print the received commands on the lcd
void print_inputs () {
	lcd_clear();
	int t = command_buf_end - command_buf_start;
	int i = 0;
	while (t--) {
		lcd_num ((int)command_buf[i]);
		i++;
	}
	lcd_num (command_buf_start);
	lcd_num (command_buf_end);
	lcd_num (state);
	return;
}

/*
*	THE PROTOCOL
*
*	Once the bluetooth is connected, the controlling device is expected to send 3 "127"s.
*	Only after this, the firebird will start accepting commands
*	There are 2 kinds of commands :
*	1. Single byte - 
*		Such commands have a value of greater than 64.
*	2. Multi byte - 
*		Such commands have a value of less than 64. The next byte after the first one will say how many more bytes will come. And then that many number of bytes are expected to be received.
*	For this we have 2 variables. state and bytes_remaining which dictate what the firebird is expecting next
*/

//IMPORTANT POINT : The bluetooth module also sends control data which is of the form "\r\n<something>\r\n". The protocol has been designed such that we do NOT end up accepting such commands and get stuck in some bad state.

//This function is called every time the main loop executes
//It takes input data from the received data buffer
//Applies the protocol  and extracts out the commands
//it stores these in the command buffer
//which is then used by the invoker to execute
void process_rcvd_data () {

	int start = rcvd_data_start;
	int end = rcvd_data_end;
	unsigned char data;
	while (start != end) {
		
		data = rcvd_data[start++];
		if (start == 1024) start = 0;		

		if (expected_127 > 0) {
			if (data == 127) {
				expected_127--;
			}
			continue;
		}

		command_buf[command_buf_end] = data;
		command_buf_end++;
		if (command_buf_end == 1024) command_buf_end = 0;

		/* check if you are done with the current command */
		if (--bytes_remaining) continue;
		
		/* next state */
		if (state == START_BYTE){
				
			if (!(data & 0xC0)){ //multiple bytes expected
				state = SIZE_BYTE;
			}
			else command_rcvd++;
			bytes_remaining = 1;
		}
		else if (state == SIZE_BYTE){
			bytes_remaining = data;
			//buzzer_prompt();
			state = MULT_BYTE;
		} 
		else if (state == MULT_BYTE){
			bytes_remaining = 1;
			state = START_BYTE;
			command_rcvd++;
		}
	}
	rcvd_data_start = start;
}

//The main loop!
int main()
{
	//Initialize all devices
	init_devices();
	lcd_set_4bit();
	lcd_init();
	leftVel=100;
	rightVel=100;
	functionFlag=0;

	while(1)
	{	
		//Extract out commands from received data
		process_rcvd_data();

		//Perform certain actions according to the state of the Firebird
		if (white_line_flag) {
			whiteline_follow_continue();
		}

		if (acc_flag) {
			acc_continue();
		}
		
		if (acc_modified_flag) {			
			acc_modified();
		}	
		
		//If there exists a command on the command buffer, call the invoker on it
		if(command_rcvd >= 1){
			unsigned char a;
			int error = get_char_from_input(&a);
			if (error == -1) {
				//lcd_string("ERROR");
				buzzer_prompt(1000);
				_delay_ms(1000);
				buzzer_prompt(1000);
				command_rcvd--;
				continue;
			}
			//lcd_num(a);
			my_invoker(a);
			command_rcvd--;
			//serial_sendString("ok");
		}

	}	
}
