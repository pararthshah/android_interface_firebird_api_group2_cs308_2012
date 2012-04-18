/*
*	This file contains functions for position control of the bot - move by X dist, rotate by Y degrees, etc.
*/

unsigned long int ShaftCountLeft = 0; //to keep track of left position encoder 
unsigned long int ShaftCountRight = 0; //to keep track of right position encoder
unsigned int Degrees; //to accept angle in degrees for turning

//The required interrupt count on the shaft after which the "goal" i.e. the correct position would be reached
extern unsigned int ReqdShaftCountInt;
extern int not_reached_goal;

//Initialize the left and right position encoders
void position_encoder_init(){
	left_position_encoder_interrupt_init();
	right_position_encoder_interrupt_init();
	ShaftCountLeft = 0;
	ShaftCountRight = 0;
}

//Disable the left and right position encoders when not in use
void position_encoder_disable(){
	left_position_encoder_interrupt_disable();
	right_position_encoder_interrupt_disable();
}

//This is to check if the bot is in the required position as asked for by the command
void checkPosition(){
	if((ShaftCountRight >= ReqdShaftCountInt) || (ShaftCountLeft >= ReqdShaftCountInt))
	{
		not_reached_goal = 0;
		stop(); //Stop action
		ShaftCountRight = 0;
		ShaftCountLeft = 0;
	}
}

//Function used for turning robot by specified degrees
void angle_rotate(unsigned int Degrees)
{
	position_encoder_init();
	float ReqdShaftCount = 0;
	ReqdShaftCountInt = 0;

	//Calculate the number of times the position encoder interrupt will need to fire
	ReqdShaftCount = (float) Degrees/ 4.090; // division by resolution to get shaft count
	ReqdShaftCountInt = (unsigned int) ReqdShaftCount;
	ShaftCountRight = 0; 
	ShaftCountLeft = 0; 

	not_reached_goal = 1;

	while (not_reached_goal) {
		checkPosition();
	}
}

//Function used for moving robot forward by specified distance
void linear_distance_mm(unsigned int DistanceInMM)
{
	position_encoder_init();
	float ReqdShaftCount = 0;
	ReqdShaftCountInt = 0;

	//Calculate the number of times the position encoder interrupt will need to fire
	ReqdShaftCount = DistanceInMM / 5.338; // division by resolution to get shaft count
	ReqdShaftCountInt = (unsigned long int) ReqdShaftCount;

	ShaftCountRight = 0;
	not_reached_goal = 1;
}

//Move forward by a specified distance
void forward_mm(unsigned int DistanceInMM)
{
 forward();
 linear_distance_mm(DistanceInMM);
}

//Move back by a specified distance
void back_mm(unsigned int DistanceInMM)
{
 back();
 linear_distance_mm(DistanceInMM);
}

//Turn left by specified degrees
void left_degrees(unsigned int Degrees) 
{
// 88 pulses for 360 degrees rotation 4.090 degrees per count
 left(); //Turn left
 angle_rotate(Degrees);
}

//Turn right by specified degrees
void right_degrees(unsigned int Degrees)
{
// 88 pulses for 360 degrees rotation 4.090 degrees per count
 right(); //Turn right
 angle_rotate(Degrees);
}

//Soft Left by specified degrees (forward)
void soft_left_degrees(unsigned int Degrees)
{
 // 176 pulses for 360 degrees rotation 2.045 degrees per count
 soft_left(); //Turn soft left
 Degrees=Degrees*2;
 angle_rotate(Degrees);
}

//Soft Right by specified degrees (forward)
void soft_right_degrees(unsigned int Degrees)
{
 // 176 pulses for 360 degrees rotation 2.045 degrees per count
 soft_right();  //Turn soft right
 Degrees=Degrees*2;
 angle_rotate(Degrees);
}

//Soft Left_2 by specified degrees (reverse)
void soft_left_2_degrees(unsigned int Degrees)
{
 // 176 pulses for 360 degrees rotation 2.045 degrees per count
 soft_left_2(); //Turn reverse soft left
 Degrees=Degrees*2;
 angle_rotate(Degrees);
}

//Soft Right_2 by specified degrees (reverse)
void soft_right_2_degrees(unsigned int Degrees)
{
 // 176 pulses for 360 degrees rotation 2.045 degrees per count
 soft_right_2();  //Turn reverse soft right
 Degrees=Degrees*2;
 angle_rotate(Degrees);
}
