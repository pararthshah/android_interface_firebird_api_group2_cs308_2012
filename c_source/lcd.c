//! A LCD-initialization function.
    /*!
	  the function to initialize use of LCD on the Bot
    */
//Function to configure LCD port
void lcd_port_config (void)
{
 DDRC = DDRC | 0xF7; //all the LCD pin's direction set as output
 PORTC = PORTC & 0x80; // all the LCD pins are set to logic 0 except PORTC 7
}

//! display a number on LCD
    /*!
	 Function used for displaying a number on LCD
    */
void lcd_num(int x)
{
char cVal[10];
itoa(x,cVal,10);
lcd_string(cVal);
char a=" ";
lcd_wr_char(a);
}


//! rolling LCD display
    /*!
	 non-blocking rolling lcd display
    */
void rollLCD(char str[])
{
strcpy(_rollLCD,str);
strcat(_rollLCD,"               ");
lcd_clear();
lcd_cursor(1,1);
lcd_string(_rollLCD);
_LCDpos=1;
_rollLCDFlag=1;

}
