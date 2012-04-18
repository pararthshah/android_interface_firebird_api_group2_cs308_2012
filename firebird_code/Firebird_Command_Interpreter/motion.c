/*
*	This file contains functions to set the motion of the bot, i.e. make it move in any direction, control its velocity, etc.
*/

//Global counters required by various functions
extern unsigned long long int curLeftCounter,curRightCounter,splLeftCounter,splRightCounter,functionFlag;
extern unsigned char leftVel,rightVel;
extern float botRatio,botAngle,botDistance;

//! A motion-configuration function.
    /*!
	  the function to configure use of motion on the Bot
    */
//Function to configure ports to enable robot's motion
void motion_pin_config (void) 
{
 DDRA = DDRA | 0x0F;
 PORTA = PORTA & 0xF0;
 DDRL = DDRL | 0x18;   //Setting PL3 and PL4 pins as output for PWM generation
 PORTL = PORTL | 0x18; //PL3 and PL4 pins are for velocity control using PWM.
}

//! velocity control
    /*!
	 Function for setting velocities of both wheels. pulse width modulation
    */
void velocity (unsigned char left_motor, unsigned char right_motor)
{
	OCR5AL = (unsigned char)left_motor;
	OCR5BL = (unsigned char)right_motor;
	leftVel=left_motor;
	rightVel=right_motor;
}
//! setting motor's direction
    /*!
	 Function used for setting motor's direction
    */
void motion_set (unsigned char Direction)
{
 unsigned char PortARestore = 0;

 Direction &= 0x0F; 		// removing upper nibbel for the protection
 PortARestore = PORTA; 		// reading the PORTA original status
 PortARestore &= 0xF0; 		// making lower direction nibbel to 0
 PortARestore |= Direction; // adding lower nibbel for forward command and restoring the PORTA status
 PORTA = PortARestore; 		// executing the command
}

//! setting motor's forward
    /*!
	 Function used for setting motor's direction forward
    */
void forward (void) 
{
  motion_set (0x06);
}

//! setting motor's direction back
    /*!
	 Function used for setting motor's direction back
    */
void back (void) //both wheels backward
{
  motion_set(0x09);
}

//! setting motor's direction left
    /*!
	 Function used for setting motor's direction left
    */
void left (void) //Left wheel backward, Right wheel forward
{
  motion_set(0x05);
}

//! setting motor's direction right
    /*!
	 Function used for setting motor's direction right
    */
void right (void) //Left wheel forward, Right wheel backward
{
  motion_set(0x0A);
}

//! setting motor's direction soft left
    /*!
	 Function used for setting motor's direction soft left
    */
void soft_left (void) //Left wheel stationary, Right wheel forward
{
 motion_set(0x04);
}

//! setting motor's direction soft right
    /*!
	 Function used for setting motor's direction soft right 
	 Left wheel forward, Right wheel is stationary
    */
void soft_right (void) //Left wheel forward, Right wheel is stationary
{
 motion_set(0x02);
}

//! setting motor's direction soft left
    /*!
	 Function used for setting motor's direction soft left
	 Left wheel backward, right wheel stationary
    */
void soft_left_2 (void) //Left wheel backward, right wheel stationary
{
 motion_set(0x01);
}

//! setting motor's direction soft right
    /*!
	 Function used for setting motor's direction
	 Left wheel stationary, Right wheel backward
    */
void soft_right_2 (void) //Left wheel stationary, Right wheel backward
{
 motion_set(0x08);
}

//! stop the bot
    /*!
	 Function used for stoping the bot.
    */
void stop (void)
{
  motion_set (0x00);
}



////interrupt vectore...............
//! starting the left wheel encoder
    /*!
	 Function used for starting the left wheel encoder
    */
void left_encoder_pin_config (void)
{
 DDRE  = DDRE & 0xEF;  //Set the direction of the PORTE 4 pin as input
 PORTE = PORTE | 0x10; //Enable internal pullup for PORTE 4 pin
}

//Function to configure INT5 (PORTE 5) pin as input for the right position encoder
//! starting the right wheel encoder
    /*!
	 Function used for starting the right wheel encoder
    */
void right_encoder_pin_config (void)
{
 DDRE  = DDRE & 0xDF;  //Set the direction of the PORTE 4 pin as input
 PORTE = PORTE | 0x20; //Enable internal pullup for PORTE 4 pin
}

//Function to initialize ports

//! enabling the left wheel encoder
    /*!
	 Function used for enabling the left wheel encoder
    */
void left_position_encoder_interrupt_init (void) //Interrupt 4 enable
{
 cli(); //Clears the global interrupt
 EICRB = EICRB | 0x02; // INT4 is set to trigger with falling edge
 EIMSK = EIMSK | 0x10; // Enable Interrupt INT4 for left position encoder
 sei();   // Enables the global interrupt 
}

//! enabling the right wheel encoder
    /*!
	 Function used for enabling the right wheel encoder
    */
void right_position_encoder_interrupt_init (void) //Interrupt 5 enable
{
 cli(); //Clears the global interrupt
 EICRB = EICRB | 0x08; // INT5 is set to trigger with falling edge
 EIMSK = EIMSK | 0x20; // Enable Interrupt INT5 for right position encoder
 sei();   // Enables the global interrupt 
}


//! disable the left wheel encoder
    /*!
	 Function used for disabling the left wheel encoder
    */
void left_position_encoder_interrupt_disable (void) //Interrupt 4 enable
{
 curLeftCounter = 0;
 EIMSK = EIMSK & 0xEF; // Enable Interrupt INT4 for left position encoder
}

//! disable the right wheel encoder
    /*!
	 Function used for disabling the right wheel encoder
    */
void right_position_encoder_interrupt_disable (void) //Interrupt 5 enable
{
 curRightCounter = 0;
 EIMSK = EIMSK & 0xDF; // Enable Interrupt INT5 for right position encoder
}


//! calculate turn ratio
    /*!
	 for traversing a arc/ a straight line, we have to calculate a urn ratio, that is the ratio of velocities of the left and right 
	 wheel velocities.
    */
float turnRatio(float radius,int dir)
{//radius is in cm

	int w;
	w=WHEEL_DIST;
	float ratio;
		ratio=(float) ((float)(radius+w/2.0))/((float) (radius-w/2.0));
		if(dir==2)//means left
		{	ratio=1.0/ratio;
		}
	return ratio;
}


//! strictForward
    /*!
	 Function used for setting motor's direction strictForward
    */
void strictForward()
{
	botRatio=1.0;

	functionFlag=1;
	splRightCounter=curRightCounter;
	splLeftCounter=curLeftCounter;
	forward();
}

//! strictBackward
    /*!
	 Function used for setting motor's direction strictBackward
    */
void strictBackward()
{
	botRatio=1.0;

	functionFlag=1;
	splRightCounter=curRightCounter;
	splLeftCounter=curLeftCounter;
	back();
}

//! setting motor's direction to move on arc
    /*!
	 Function used for setting motor's direction to move on arc of given radius.
	 dir=1 means left dir=0 means right
    */
void moveOnArc(float radius,int dir)
{

	float ratio,newLeftVel,newRightVel;
	ratio=turnRatio(radius,dir);

	functionFlag=1;
	botRatio=ratio;

	leftVel=150;
	rightVel=150;
	if(ratio>1)
	{
		newRightVel=rightVel;
		newLeftVel=(float) newRightVel*ratio;
		if(newLeftVel>254)
		{
			newLeftVel=leftVel;
			newRightVel=(float)newLeftVel/ratio;
		}
	}
	else 
	{
		newLeftVel=leftVel;
		newRightVel=(float) newLeftVel/ratio;
		if(newRightVel>254)
		{
			newRightVel=rightVel;
			newLeftVel=(float)newRightVel*ratio;
		}
	}
	splLeftCounter=curLeftCounter;
	splRightCounter=curRightCounter;

	velocity((unsigned char) newLeftVel,(unsigned char) newRightVel);
	forward();
}
