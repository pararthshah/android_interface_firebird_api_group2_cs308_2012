//ISR for right position encoder
//! Interrupt 5 - right wheel
    /*!
	 ISR for right position encoder
    */
ISR(INT5_vect)  
{
	rightInt++;
	curRightCounter++;  //increment right shaft position count

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

//! setting motor's direction
    /*!
	 Function used for setting motor's direction
    */
ISR(TIMER4_OVF_vect)
{
//lcd_clear();
//lcd_string("timer");
if(functionFlag!=0)
{
//bot distance chk kr ke stop bhi karna hai-----------------------------------------


		
		int left,right;float cratio,velRatio;
	float dist;
		right=curRightCounter-splRightCounter;
		left=curLeftCounter-splLeftCounter;
	
		if(left!=0 && right!=0)
		{
			cratio=(float)left/right;
			velRatio = (float)leftVel/rightVel;
			int newLeftVel,newRightVel;
			if(cratio < botRatio - 0.001 )
			{
				float newVelRatio;
				newVelRatio =(float) ((float)botRatio/cratio)*velRatio;

				newRightVel=rightVel;
				newLeftVel=rightVel * newVelRatio;
				if(newLeftVel>254)
				{
				newLeftVel=leftVel;
				newRightVel=(float)leftVel/newVelRatio;
				}
				//lcd_clear();
	/*			
				lcd_cursor(1,1);
			//	lcd_string("H");
				lcd_num(left);
				lcd_num(right);

				lcd_num(leftVel);
				lcd_num(rightVel);
				lcd_cursor(2,2);
				lcd_num(newLeftVel);
				lcd_num(newRightVel);
				*/

				velocity((unsigned char) newLeftVel,(unsigned char) newRightVel);
				
			}
			else if( cratio > botRatio+0.001 )
			{
				float newVelRatio;
				newVelRatio = (botRatio/cratio)*velRatio;
				newLeftVel=leftVel;

				newRightVel=(float)leftVel/newVelRatio;
				if(newRightVel>254)
				{	
					newRightVel = rightVel;
					newLeftVel = newRightVel * newVelRatio;
				}
					//lcd_clear();
			/*	
				lcd_cursor(1,1);
			//	lcd_string("H");
				lcd_num(left);
				lcd_num(right);

				lcd_num(leftVel);
				lcd_num(rightVel);
				lcd_cursor(2,2);
				lcd_num(newLeftVel);
				lcd_num(newRightVel);
				*/
				velocity((unsigned char) newLeftVel,(unsigned char) newRightVel);
				
			}

		}
		if(functionFlag==1)
		{
			splRightCounter=curRightCounter;
			splLeftCounter=curLeftCounter;
		}
	
	
}
if(_rollLCDFlag==1)
{lcd_cursor(1,1);
	lcd_string(_rollLCD+_LCDpos);
	_LCDpos++;
		if(_LCDpos==strlen(_rollLCD) -14)
		{ 
		_rollLCDFlag=0;
		lcd_clear();
	
		}
}
 //TIMER4 has overflowed
 TCNT4H = 0xEF; //reload counter high value
 TCNT4L = 0x00; //reload counter low value
//lcd_clear();
} 
