//! A timer5-initialization function.
    /*!
	  the function to initialize use of  timer5 on the Bot
    */
// Timer 5 initialised in PWM mode for velocity control
// Prescale:64
// PWM 8bit fast, TOP=0x00FF
// Timer Frequency:674.988Hz
void timer5_init()
{
	TCCR5B = 0x00;	//Stop
	TCNT5H = 0xFF;	//Counter higher 8-bit value to which OCR5xH value is compared with
	TCNT5L = 0x01;	//Counter lower 8-bit value to which OCR5xH value is compared with
	OCR5AH = 0x00;	//Output compare register high value for Left Motor
	OCR5AL = 0xFF;	//Output compare register low value for Left Motor
	OCR5BH = 0x00;	//Output compare register high value for Right Motor
	OCR5BL = 0xFF;	//Output compare register low value for Right Motor
	OCR5CH = 0x00;	//Output compare register high value for Motor C1
	OCR5CL = 0xFF;	//Output compare register low value for Motor C1
	TCCR5A = 0xA9;	/*{COM5A1=1, COM5A0=0; COM5B1=1, COM5B0=0; COM5C1=1 COM5C0=0}
 					  For Overriding normal port functionalit to OCRnA outputs.
				  	  {WGM51=0, WGM50=1} Along With WGM52 in TCCR5B for Selecting FAST PWM 8-bit Mode*/
	
	TCCR5B = 0x0B;	//WGM12=1; CS12=0, CS11=1, CS10=1 (Prescaler=64)
}


//! setting timer 3
    /*!
	 Function used for setting timer 3 function for Oninterrupt usage
    */
void setTimer3(int time, void (*f) (void))
{
long long int ticks=TICKS_PER_SEC*time;
TCCR3B = 0x00; //stop
 TCNT3H = 0xFF - ticks/0xFF; //Counter higher 8 bit value
 TCNT3L = 0xFF-ticks%0xFF; //Counter lower 8 bit value
 TCCR3B = 0x05; //start Timer
 timer3func=f;

}


//! setting timer 1
    /*!
	 Function used for setting timer 1 function for Oninterrupt usage
    */
void setTimer1(int time, void (*f) (void))
{
long long int ticks=TICKS_PER_SEC*time;
TCCR1B = 0x00; //stop
 TCNT1H = 0xFF - ticks/0xFF; //Counter higher 8 bit value
 TCNT1L = 0xFF-ticks%0xFF; //Counter lower 8 bit value
 TCCR1B = 0x05; //start Timer
 timer1func=f;

}

//! timer 4 initialization
    /*!
	 timer 4 initialization and setting high and low values and enable overflow interrupt
    */
void timer4_init(void)
{
 TCCR4B = 0x00; //stop
 TCNT4H = 0xEF; //Counter higher 8 bit value
 TCNT4L = 0x00; //Counter lower 8 bit value
 OCR4AH = 0x00; //Output Compair Register (OCR)- Not used
 OCR4AL = 0x00; //Output Compair Register (OCR)- Not used
 OCR4BH = 0x00; //Output Compair Register (OCR)- Not used
 OCR4BL = 0x00; //Output Compair Register (OCR)- Not used
 OCR4CH = 0x00; //Output Compair Register (OCR)- Not used
 OCR4CL = 0x00; //Output Compair Register (OCR)- Not used
 ICR4H  = 0x00; //Input Capture Register (ICR)- Not used
 ICR4L  = 0x00; //Input Capture Register (ICR)- Not used
 TCCR4A = 0x00; 
 TCCR4C = 0x00;
 TCCR4B = 0x05; //start Timer
}
