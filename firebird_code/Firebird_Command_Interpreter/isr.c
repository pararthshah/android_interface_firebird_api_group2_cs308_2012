/*
*	This file contains various interrupt service routines
*/

extern unsigned int leftInt,rightInt;
extern unsigned long long int curLeftCounter,curRightCounter,splLeftCounter,splRightCounter,functionFlag;

extern unsigned long int ShaftCountLeft; //to keep track of left position encoder 
extern unsigned long int ShaftCountRight; //to keep track of right position encoder



//ISR for right position encoder
//! Interrupt 5 - right wheel
    /*!
	 ISR for right position encoder
    */
ISR(INT5_vect)  
{
	rightInt++;
	curRightCounter++;  //increment right shaft position count
	ShaftCountRight++;  //increment right shaft position count
}


//SR for left position encoder
//! Interrupt 4 - left wheel
    /*!
	 ISR for left position encoder
    */
ISR(INT4_vect)
{
	leftInt++;
	curLeftCounter++;  //increment left shaft position count
	ShaftCountLeft++;  //increment left shaft position count
}

//! timer 3 overflow
    /*!
	 Interrupt on overflow of timer 3
    */
ISR(TIMER3_OVF_vect)
{

(*timer3func)();
}


//! timer 1 overflow
    /*!
	 Interrupt on overflow of timer 1
    */
ISR(TIMER1_OVF_vect)
{

(*timer1func)();
}

extern int stop_on_timer4_overflow;

//! setting motor's direction
    /*!
	 Function used for setting motor's direction
    */
//This ISR can be used to schedule events like refreshing ADC data, LCD data
//Timer 4 overflow interrupt
ISR(TIMER4_OVF_vect)
{

  if (stop_on_timer4_overflow) {
	stop_on_timer4_overflow = 0;
    TCCR4B = 0x00;
  }
}