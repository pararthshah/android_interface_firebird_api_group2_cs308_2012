/*
*	This file contains functions to enable to the firebird to follow a whiteline
*/

//If this flag is set, the main loop calls whiteline_follow_continue() in every iteration
extern unsigned char white_line_flag;

//If this flag is set, the firebird stops (and also stops whiteline following) on reaching an intersection and sends back "SUCCESS" via the communication channel
extern unsigned char whiteline_stop_intersection_flag;

extern unsigned char Left_white_line;
extern unsigned char Center_white_line;
extern unsigned char Right_white_line;

void error () {
	//lcd_string ("error");
}

//Called when whiteline following needs to be disabled
void whiteline_follow_end() {
	stop();
	white_line_flag = 0;
}


#define W_THRESHOLD 0x0f
#define W_THRESHOLD_STOP 0x08
#define ROTATE_THRESHOLD 0x0f //0x41

#define LEFT_SENSOR 3
#define CENTER_SENSOR 2
#define RIGHT_SENSOR 1
#define FRONT_IR_SENSOR 6

#define CONT_BLACK 2

extern unsigned char Left_white_line;
extern unsigned char Center_white_line;
extern unsigned char Right_white_line;
extern unsigned char Front_IR_Sensor;


/**
  Prints White line sensor values on the screen
*/
void print_sensor_data()
{
		lcd_clear();
		print_sensor(1,1,3);	//Prints value of White Line Sensor1
		print_sensor(1,5,2);	//Prints Value of White Line Sensor2
		print_sensor(1,9,1);	//Prints Value of White Line Sensor3
}

/**
  Reads all relevant sensor values and stores it in appropriate global variables.
*/
void read_sensors()
{
	Left_white_line = ADC_Conversion(LEFT_SENSOR);	
	Center_white_line = ADC_Conversion(CENTER_SENSOR);	
	Right_white_line = ADC_Conversion(RIGHT_SENSOR);	
	Front_IR_Sensor = ADC_Conversion(FRONT_IR_SENSOR);
}

/**
 * Reset shaft counters.
 */
void reset_shaft_counters()
{
	ShaftCountLeft = 0;
	ShaftCountRight = 0;
}

//Global variables required for whiteline following. Maintains the state of the bot and what action it had taken previously
unsigned char flag = 0;

char last_on = LEFT_SENSOR;
char black_flag = 0;



/**
  Go forward upto the next intersection, while following a white line.
  Uses 7-fold scheme :
  (left, center, right) - Action
			(0,1,0)			- Go Forward.
			(1,1,0)			- Turn right (slightly)
			(1,0,0)			- Turn right (hard)
			(0,1,1)			- Turn left (slightly)
			(0,0,1)			- Turn left (hard)
			(1,1,1)			- Reached the intersection
			(0,0,0)			- Recovery mode. Move in the direction of the last sensor that was on white line
*/
//This is a non-blocking function. It is called once on every iteration of the main loop if "white_line_flag" is ON
//It reads the whiteline sensor values and determines what it should do next to stay on the white line
void whiteline_follow_continue() {

		read_sensors();

		flag=0;
		print_sensor_data();

		if(Center_white_line<W_THRESHOLD_STOP && Left_white_line<W_THRESHOLD_STOP && Right_white_line<W_THRESHOLD_STOP ){
		 	if (whiteline_stop_intersection_flag) {
				whiteline_follow_end();
				send_char(SUCCESS);
			}
			/*else {
				forward();
				velocity(100,100);
				stop_on_timer4_overflow = 1;
				start_timer4();
				while (stop_on_timer4_overflow != 0) {;}
			}
			return;*/
		}

		
		if( Front_IR_Sensor<0xF0)
		{
			stop();
			buzzer_on();
		}
		//Sensor config : 010
		else if(Left_white_line > W_THRESHOLD && Center_white_line < W_THRESHOLD && Right_white_line > W_THRESHOLD)
		{
			forward();
			velocity(150,150);
			black_flag = 0;
			buzzer_off();
		}

		//Sensor config : 110
		else if(Left_white_line < W_THRESHOLD && Center_white_line < W_THRESHOLD && Right_white_line > W_THRESHOLD)
		{
			forward();
			velocity(120,150);
			black_flag = 0;
			buzzer_off();
		}
		
		//Sensor config : 100
		else if(Left_white_line < W_THRESHOLD && Center_white_line > W_THRESHOLD && Right_white_line > W_THRESHOLD)
		{
			PORTA = 0x05;
			velocity(50,130);
			last_on = LEFT_SENSOR;
			black_flag = 0;
			buzzer_off();
		}

		//Sensor config : 011
		else if(Left_white_line > W_THRESHOLD && Center_white_line < W_THRESHOLD && Right_white_line < W_THRESHOLD)
		{
			forward();
			velocity(150,120);
			black_flag = 0;
			buzzer_off();
		}

		//Sensor config : 001
		else if(Left_white_line > W_THRESHOLD && Center_white_line > W_THRESHOLD && Right_white_line < W_THRESHOLD)
		{
			PORTA = 0x0A;
			velocity(130,50);
			last_on = RIGHT_SENSOR;
			black_flag = 0;
			buzzer_off();
		}
		//Sensor config : 000
		else
		{
			buzzer_off();
			if(black_flag >= CONT_BLACK)  {
				if(last_on == LEFT_SENSOR)
					motion_set(0x05);
				else if(last_on == RIGHT_SENSOR)
					motion_set(0x0A);
				velocity(100,100);
				while(1){
					print_sensor_data();
					read_sensors();
					if(Center_white_line < W_THRESHOLD) break;
				}
			}
			black_flag = (black_flag < CONT_BLACK)?black_flag+1:CONT_BLACK;
			forward();
			velocity(0,0);
		}
}

//Called when whiteline following needs to be enabled
void whiteline_follow_start() {
	white_line_flag = 1;
	whiteline_follow_continue();
}

/**
  Turn right at an intersection.
*/
void turn_right(){
	buzzer_off();
	motion_set(0x0A);
	velocity(100,100);
	_delay_ms(1000);
	while(1){
		print_sensor_data();
		read_sensors();
		if(Center_white_line < W_THRESHOLD) break;
	}
	velocity(0,0);
}

/**
  Turn left at an intersection.
*/
void turn_left(){
	buzzer_off();
	motion_set(0x05);
	velocity(100,100);
	_delay_ms(1000);
	while(1){
		print_sensor_data();
		read_sensors();
		if(Center_white_line < W_THRESHOLD) break;
	}
	velocity(0,0);
}

/**
  Go forward by a certain specified number of steps.
*/
void go_distance(unsigned char x)
{
	reset_shaft_counters();
   forward();
	velocity(100,100);
	PORTJ = 0x00;
	while(1){
		read_sensors();
		print_sensor_data();
		if( Front_IR_Sensor<0xF0)
		{
			stop();
			buzzer_on();
		}
		else
		{
			forward();
			buzzer_off();
		}
		if((ShaftCountLeft + ShaftCountRight)*5 > x*10)
			break;
	}
	velocity(0,0);
}
