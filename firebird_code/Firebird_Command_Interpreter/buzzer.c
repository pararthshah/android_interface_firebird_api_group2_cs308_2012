/*
*	This file contains functions to initialize the buzzer, turn it on, turn it off, or prompt for some time
*/

//! A buzzer-configuration function.
    /*!
	  the function to configure use of buzzer on the Bot
    */
//Function to configure Buzzer pins
void buzzer_pin_config (void)
{
 DDRC = DDRC | 0x08;		//Setting PORTC 3 as outpt
 PORTC = PORTC & 0xF7;		//Setting PORTC 3 logic low to turnoff buzzer
}

//! A Buzzer on function.
    /*!
	  the function to switch the buzzer on
    */
void buzzer_on (void)
{
 unsigned char port_restore = 0;
 port_restore = PINC;
 port_restore = port_restore | 0x08;
 PORTC = port_restore;
}

//! A Buzzer off function.
    /*!
	  the function to switch the buzzer off
    */
void buzzer_off (void)
{
 unsigned char port_restore = 0;
 port_restore = PINC;
 port_restore = port_restore & 0xF7;
 PORTC = port_restore;
}

//! A Buzzer prompt function.
    /*!
	  the function to switch the buzzer on for some time and then switch off
    */
void buzzer_prompt(int time){
	buzzer_on();
	_delay_ms(time);
	buzzer_off();

}
