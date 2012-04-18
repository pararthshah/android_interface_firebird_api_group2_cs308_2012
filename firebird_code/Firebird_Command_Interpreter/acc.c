/*
	This file contains functions for Adaptive Cruise Control (ACC)
	There are 2 types of ACC :
		1. Normal ACC - Stop and beep when something get in front of the bot and start moving again after the path is cleared
		2. Modified ACC - Stop and beep when something get in front of the bot and don't restore motion
*/

//These are the 2 flags for ACC
//If it is ON it means that ACC is ON and the main loop will call acc_continue in every iteration
extern unsigned char acc_flag;
extern unsigned char acc_modified_flag;

//These 2 flags are used in ACC to check if bot is already stationary because of ACC. If so, we need not "stop" it again and again
unsigned char already_stopped = 0;
unsigned char already_modified_stopped = 0;

//Used to restore the motion of the bot after the path has cleared. e.g. If bot was turning left and it stopped due to ACC, then it would use the value in this variable to make the bot start going left again after path gets cleared
unsigned char stored_direction;

//Store the current motion in "stored_direction"
void store_motion () {
	stored_direction = PORTA & 0x0F;
}

//Restore the motion of the bot using value in "stored_direction"
void restore_motion () {
	PORTA = PORTA | stored_direction;
}

//Called on every iteration of the main loop is acc_flag is TRUE
//Checks if there is an obstacle in front of the bot using the front Sharp and IR sensor and stops the bot if there is
void acc_continue(){
	int Front_Sharp_Sensor = ADC_Conversion(11);
	int Front_IR_Sensor = ADC_Conversion(6);

	if(Front_Sharp_Sensor>0x82 || Front_IR_Sensor<0xF0)
	{
		if (already_stopped) return;
		already_stopped = 1;
		store_motion();
		stop();
		buzzer_on();
		
	}
	else{
		restore_motion();		
		buzzer_off();
		already_stopped = 0;
	}
}

//Called on every iteration of the main loop is acc_modified_flag is TRUE
//Checks if there is an obstacle in front of the bot using the front Sharp and IR sensor and stops the bot if there is
 void acc_modified(){
	int Front_Sharp_Sensor = ADC_Conversion(11);
	int Front_IR_Sensor = ADC_Conversion(6);

	if(Front_Sharp_Sensor>0x82 || Front_IR_Sensor<0xF0)
	{
		//if (already_modified_stopped) return;
		already_modified_stopped = 1;
		
		stop();
		acc_modified_flag = 0;
		//buzzer_on();
		
	}
	else{
		
		buzzer_off();
		already_modified_stopped = 0;
	}


 }
