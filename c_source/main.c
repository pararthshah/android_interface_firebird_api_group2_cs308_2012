#include<avr/io.h>
#include<avr/interrupt.h>
#include<util/delay.h>
#include<stdio.h>
#include<string.h>
#define WHEEL_DIST 15 // in cm
#include <math.h> //included to support power function
#include "lcd.c"


#define FCPU 11059200ul //defined here to make sure that program works properly
#define DIST_PER_TICK 0.544
#define TICKS_PER_SEC 11059

void (*timer3func)(void);
void (*timer2func)(void);
void (*timer1func)(void);
void port_init();
void timer5_init();
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


unsigned long long int curLeftCounter,curRightCounter,splLeftCounter,splRightCounter,functionFlag=0;
unsigned char leftVel,rightVel;
float botRatio,botAngle,botDistance;
unsigned char data;
char _rollLCD[100];
unsigned int leftInt=0,rightInt;

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
}


//! function to invoke actions based on input
    /*!
	 Function used for invoking action based on command recieved form the computer via zigbee
    */
void invoker(char opt[50])
{
	    if(!strcmp("w",opt)) //ASCII value of 8
        {
            PORTA=0x06;  //forward
			return;
        }
 
        if(!strcmp("s",opt)) //ASCII value of 2
        {
            PORTA=0x09; //back
			return;
        }
 
        if(!strcmp("a",opt)) //ASCII value of 4
        {
            PORTA=0x05;  //left
  	        return;
		}
 
        if(!strcmp("d",opt)) //ASCII value of 6
        {
            PORTA=0x0A; //right
			return;
        }
 
        if(!strcmp(" ",opt)) //ASCII value of 5
        {
            PORTA=0x00; //stop
			return;
        }
 
        if(!strcmp("h",opt)) //ASCII value of 7
        {
            buzzer_on();
			return;
        }
 
        if(!strcmp("m",opt)) //ASCII value of 9
        {
            buzzer_off();
			return;
        }
		if(!strcmp("lcd",opt))
		{
			char arg1[50];
			serial_getString(arg1,50);
			lcd_cursor(1,1);
			lcd_clear();
			lcd_string(arg1);
			return;
		}
		if(!strcmp("setPort",opt))
		{
			char portName[50],value[50];
				serial_getString(portName,50);serial_getString(value,50); 
				  if(strcmp(portName,"PORTA")==0) PORTA=atoi(value);
				  else if(strcmp(portName,"PORTB")==0) PORTB=atoi(value);
				  else if(strcmp(portName,"PORTC")==0) PORTC=atoi(value);
				  else if(strcmp(portName,"PORTD")==0) PORTD=atoi(value);
				  else if(strcmp(portName,"PORTE")==0) PORTE=atoi(value);
				  else if(strcmp(portName,"PORTF")==0) PORTF=atoi(value);
				  else if(strcmp(portName,"PORTG")==0) PORTG=atoi(value);
				  else if(strcmp(portName,"PORTH")==0) PORTH=atoi(value);
				  else if(strcmp(portName,"PORTJ")==0) PORTJ=atoi(value);
				  else if(strcmp(portName,"PORTK")==0) PORTK=atoi(value);
				  else if(strcmp(portName,"PORTL")==0) PORTL=atoi(value);
				  else if(strcmp(portName,"DDRA")==0) DDRA=atoi(value);
				  else if(strcmp(portName,"DDRB")==0) DDRB=atoi(value);
				  else if(strcmp(portName,"DDRC")==0) DDRC=atoi(value);
				  else if(strcmp(portName,"DDRD")==0) DDRD=atoi(value);
				  else if(strcmp(portName,"DDRE")==0) DDRE=atoi(value);
				  else if(strcmp(portName,"DDRF")==0) DDRF=atoi(value);
				  else if(strcmp(portName,"DDRG")==0) DDRG=atoi(value);
				  else if(strcmp(portName,"DDRH")==0) DDRG=atoi(value);
				  else if(strcmp(portName,"DDRJ")==0) DDRJ=atoi(value);
				  else if(strcmp(portName,"DDRK")==0) DDRK=atoi(value);
				  else if(strcmp(portName,"DDRL")==0) DDRL=atoi(value);
				return;

		
		
		}
		if(!strcmp("getPin",opt))
		{
			char pinName[10],value[10];
			serial_getString(pinName,10); 
			if(strcmp(pinName,"PINA")==0)   itoa(PINA, value,10 ); 
			  else if(strcmp(pinName,"PINB")==0)   itoa(PINB, value,10 ); 
			  else if(strcmp(pinName,"PINC")==0)   itoa(PINC, value,10 ); 
			  else if(strcmp(pinName,"PIND")==0)   itoa(PIND, value,10 ); 
			  else if(strcmp(pinName,"PINE")==0)   itoa(PINE, value,10 ); 
			  else if(strcmp(pinName,"PINF")==0)   itoa(PINF, value,10 ); 
			  else if(strcmp(pinName,"PING")==0)   itoa(PING, value,10 ); 
			  else if(strcmp(pinName,"PINH")==0)   itoa(PINH, value,10 ); 
			  else if(strcmp(pinName,"PINJ")==0)   itoa(PINJ, value,10 ); 
			  else if(strcmp(pinName,"PINK")==0)   itoa(PINK, value,10 ); 
			  else if(strcmp(pinName,"PINL")==0)   itoa(PINL, value,10 ); 
			serial_sendString(value);
			return;

		}
		if(!strcmp("rollLCD",opt))
		{

			char str[100];
			serial_getString(str,100);
			rollLCD(str);
			return;

		}
		if(!strcmp("strictForward",opt))
		{
			strictForward();return;
			
		}
		if(!strcmp("strictBack",opt))
		{
			strictBackward();return;
			
		}
		if(!strcmp("moveOnArc",opt))
		{

			char rad[20],dir[3];
			serial_getString(rad,20);
			serial_getString(dir,3);
			int radius,direc;
			radius=atoi(rad);
			direc=atoi(dir);
			moveOnArc(radius,direc);
			
			return;

		}
		if(!strcmp("getLeftWLS",opt))
		{
		char value[10];
		int a=ADC_Conversion(3);
		itoa(a, value,10 );
		serial_sendString(value);
		return;
		}
		if(!strcmp("getCenterWLS",opt))
		{
		char value[10];
		int a=ADC_Conversion(2);
		itoa(a, value,10 );
		serial_sendString(value);
		return;
		}
		if(!strcmp("getRightWLS",opt))
		{
		char value[10];
		int a=ADC_Conversion(1);
		itoa(a, value,10 );
		serial_sendString(value);
		return;
		}
		if(!strcmp("getIRProx",opt))
		{
			char sensorNo[4];
			serial_getString(sensorNo,4);
			int sen=atoi(sensorNo);
			if(sen>5||sen<1)
			{	lcd_string("wrong sensor no.");
				return;
			}
			char value[10];
			int a=ADC_Conversion(sen+3);
			itoa(a, value,10 );
			serial_sendString(value);
			return;
		}
		if(!strcmp("getIRSharp",opt))
		{
			char sensorNo[4];
			serial_getString(sensorNo,4);
			int sen=atoi(sensorNo);
			if(sen>5||sen<1)
			{	lcd_string("wrong sensor no.");
				return;
			}
			char value[10];
			int a=ADC_Conversion(sen+8);
			itoa(a, value,10 );
			serial_sendString(value);
			return;
		}
		if(!strcmp("setVelocity",opt))
		{
		
			char left[10],right[10];
			serial_getString(left,10);
			serial_getString(right,10);
			velocity(atoi(left)%255,atoi(right)%255);
			return;
		}
		if(!strcmp("listenForInterrupt",opt))
		{
			char intName[10],value[5];
			
			serial_getString(intName,10);
			if(!(strcmp(intName,"left")))
			{ 
				if(leftInt>0)
				{	
					itoa(leftInt, value, 5 );leftInt=0;
					serial_sendString(value);

				}
				else serial_sendString("0");
			}
			else if(!(strcmp(intName,"right")))
			{ 
				if(rightInt>0)
				{	
					itoa(rightInt, value, 5 );rightInt=0;
					serial_sendString(value);

				}
				else serial_sendString("0");
			}
			else
			{ 
				lcd_clear();
				lcd_string("wrong interrupt"); 
			}
			return;

		}
		if(!strcmp("resetInterrupt",opt))
		{
			char intName[10];
			
			serial_getString(intName,10);
			if(!(strcmp(intName,"left")))
			{ leftInt=0;
			}
			else if(!(strcmp(intName,"right")))
			{ rightInt=0;
			}
			else 
			{ 
				lcd_clear();
				lcd_string("wrong interrupt"); 
			}
			return;
		}


		lcd_string(opt);


}








int c=0;





////timerrrrrrrrrrrrrrrrrr 44444444

//! initialize all devices
    /*!
	 initialize all devices. call all other initialization function
    */
void init_devices (void)
{
 	cli(); //Clears the global interrupts
	port_init();
	adc_init();
	uart0_init();
	timer5_init();
	timer4_init();

 TIMSK4 = 0x01; //timer4 overflow interrupt enable
 TIMSK3 = 0x01;
 left_position_encoder_interrupt_init();
 right_position_encoder_interrupt_init();
 button_interrupt_init();
	sei();   //Enables the global interrupts
}

int a=0;
//! a example function
    /*!
	 a example function for use onInterrupt
    */
void test()
{a++;
	lcd_clear();
	lcd_cursor(1,1);
lcd_num(a);
	lcd_string("hello world");
	setTimer3(3,&test);
}

int main()
{
	init_devices();
	lcd_set_4bit();
	lcd_init();
	leftVel=100;
	rightVel=100;
	functionFlag=0;

velocity(254,254);

while(1)
{
	char a[100];
	serial_getString(a,100);
	invoker(a);
}

	
}

