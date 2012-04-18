/*
*	This file contains the communication module.
*	It has functions to initialize the communication over UART2 or UART3 and there are ISRs to handle received data
*/

//! internal struct to recieve data from zigbee
	/*! internal struct used to recieve data from zigbee. uses linked list structure
	*/
struct charpack
{
    unsigned char data;
    struct charpack *next;
} typedef charPack;

charPack *serialDataStart=NULL;
charPack *serialDataEnd=NULL;

int serial_lock=0;

//Global Variables which dictate the functioning of our protocol
#define START_BYTE 0
#define SIZE_BYTE 1
#define MULT_BYTE 2

extern int bytes_remaining;
extern int command_rcvd;
extern int accept;

unsigned char data;

extern int state;

//Buffers for storing the received input
extern unsigned char rcvd_data[1024];
extern int rcvd_data_start, rcvd_data_end;

//Set this to 1 if BLUETOOTH is being used. If using serial communication, then set it to 0
#define USE_BLUETOOTH 1

//Send one byte over the bluetooth
void send_data_bt (unsigned char data) {
	UDR3 = data;
	_delay_ms(2);
}

#define USE_PROTOCOL 1

//Add the received character to the buffer (not used)
void add_to_input (unsigned char data) {

	charPack *temp;

	temp=(charPack *) malloc(sizeof(charPack));
	temp->next = NULL;
	temp->data = (unsigned char) data;

	if(serialDataEnd == NULL) {
		serialDataEnd=temp;
	}
	else
	{
		 serialDataEnd->next=temp;
		 serialDataEnd=temp;
	}
	if(serialDataStart == NULL) {
		serialDataStart = serialDataEnd;     
	}

}

//ISR for handling any received input from the bluetooth
//IMPORTANT : Only minimal processing can be done in this function. Otherwise there is a chance that you may not receive all the input!
SIGNAL(SIG_USART3_RECV)         // ISR for receive complete interrupt
{

	rcvd_data[rcvd_data_end] = UDR3;
	rcvd_data_end++;
	if (rcvd_data_end == 1024) rcvd_data_end = 0;
	if (rcvd_data_end == rcvd_data_start) {
		buzzer_on();
		lcd_string("Buffer overflow!");
		_delay_ms(5000);
	}
	
}
   
//!bluetooth usage    
	/*!
	 Function used for initializing uart3
    */
void uart3_init(void)
{
 UCSR3B = 0x00; //disable while setting baud rate
 UCSR3A = 0x00;
 UCSR3C = 0x06;
 //UBRR3L = 0x5F; //set baud rate lo (FOR BOT FREQUENCY 14745600)
 UBRR3L = 0x47; //set baud rate lo (FOR BOT FREQUENCY 11059200)
 UBRR3H = 0x00; //set baud rate hi
 UCSR3B = 0x98;
}
 
 
 //! USB data recieve
    /*!
	 intterupt generated when data is recieved from USB 
    */
//receives the data over the usb and stores it in a linked list. Also parses the input and applies the protocol to extract out the command from the input
SIGNAL(SIG_USART2_RECV)         // ISR for receive complete interrupt
{
    data = UDR2;                 //making copy of data from UDR2 in 'data' variable 
  
	//lcd_wr_char(data);

	/* add character to input buffer */
    charPack *temp;

    temp=(charPack *) malloc(sizeof(charPack));
    temp->next=NULL;
    temp->data=(unsigned char) data;
    serial_lock=1;
    if(serialDataEnd==NULL) serialDataEnd=temp;
    else
    {
         serialDataEnd->next=temp;
         serialDataEnd=temp;
    }
    if(serialDataStart==NULL) serialDataStart=serialDataEnd;     

	/* check if you are done with the current command */
	if (--bytes_remaining) return;
	
	/* next state */
	if (state == START_BYTE){
		buzzer_prompt(1000);
		if (!(data & 0xC0)){ //multiple bytes expected
			state = SIZE_BYTE;
		}
		else command_rcvd = 1;
		bytes_remaining = 1;
	}
	else if (state == SIZE_BYTE){
		bytes_remaining = data;
		//buzzer_prompt();
		state = MULT_BYTE;
	} 
	else if (state == MULT_BYTE){
		bytes_remaining = 1;
		state = START_BYTE;
		command_rcvd = 1;
	}
}

//! zigbee sendString
    /*!
	 function to recieve a string from zigbee followed by a marker terminal #
    */
// Use only with python controller (not used!)
void serial_sendString(char str[])
{
	int i;
	lcd_cursor(1,1);
	UDR2='#';
	_delay_ms(1);

	for(i=0;i<strlen(str);i++)
	{
		UDR2=str[i];

		_delay_ms(1);
	}
	UDR2='#';
	_delay_ms(1);
}

// Use only with python controller
void serial_sendChar(unsigned char c)
{
	lcd_cursor(1,1);
	UDR2='#';
	_delay_ms(1);
	lcd_clear();

	UDR2=c;
	lcd_num((int)c);
	_delay_ms(1);
	
	_delay_ms(1);
	UDR2='#';
	_delay_ms(1);
}

//Function to send a byte over the communication channel
void send_char (unsigned char c) {
	if (USE_BLUETOOTH) send_data_bt (c);
	else serial_sendChar(c);
}

//Function to split and send an integer over the communication channel
void send_int (int i) {
	if (USE_BLUETOOTH) {
		unsigned char c = (i >> 24) & 0xFF;
		send_data_bt (c);
		c = (i >> 16) & 0xFF;
		send_data_bt (c);
		c = (i >> 8) & 0xFF;
		send_data_bt (c);
		c = i & 0xFF;
		send_data_bt (c);
	}
	else {
		char str[10];
		itoa(i,str,10);
		serial_sendString(str);
	}
}
