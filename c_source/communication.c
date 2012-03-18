 ////zigbee
//!zigbee usage    
	/*!
	 Function used for initializing zigbee usage
    */
void uart0_init(void)
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x47; //set baud rate lo
 UBRR0H = 0x00; //set baud rate hi
 UCSR0B = 0x98;
}
//! internal struct to recieve data from zigbee
	/*! internal struct used to recieve data from zigbee. uses linked list structure
	*/
struct charpack
{
    char data;
    struct charpack *next;
} typedef charPack;
charPack *serialDataStart=NULL;
charPack *serialDataEnd=NULL;
int serial_lock=0;
 
 
 //! zigbee data recieve
    /*!
	 intterupt generated when data is recieved from zigbee
    */
SIGNAL(SIG_USART0_RECV)         // ISR for receive complete interrupt
{
    data = UDR0;                 //making copy of data from UDR0 in 'data' variable 
  
    charPack *temp;

    temp=(charPack *) malloc(sizeof(charPack));
    temp->next=NULL;
    temp->data=(char) data;
    serial_lock=1;
       //lcd_wr_char(temp->data);
    if(serialDataEnd==NULL) serialDataEnd=temp;
    else
    {
         serialDataEnd->next=temp;
         serialDataEnd=temp;
    }
    if(serialDataStart==NULL) serialDataStart=serialDataEnd;     

}

//! zigbee sendString
    /*!
	 function to recieve a string from zigbee followed by a marker terminal #
    */
void serial_sendString(char str[])
{int i;
lcd_cursor(1,1);
UDR0='#';
_delay_ms(1);
char val[20];
//lcd_string("here");
//itoa(strlen(str),val,10);
//	lcd_string(val);

	for(i=0;i<strlen(str);i++)
	{
	UDR0=str[i];
//	lcd_wr_char(str[i]);

	_delay_ms(1);
	}
	UDR0='#';
	_delay_ms(1);
}

 //! zigbee getString
    /*!
	 function to get a string from zigbee followed by a marker terminal #
    */
void serial_getString(char *x , int n)
{
    int i;
    charPack *runner=serialDataStart, *temp;
    for(i=0;i<n;i++)
    {   
	//	lcd_string("reading ");
		
        if(runner==NULL || serial_lock==1) {i=-1; serial_lock=0;runner=serialDataStart;continue;}
        else if(runner->data=='#') {
            x[i]='\0';
            runner=serialDataStart;
            while(runner->data!='#')
            { temp=runner;
              runner=runner->next;
              free(temp);       
            }
            serialDataStart=runner->next;
			if(serialDataStart==NULL) serialDataEnd=NULL;
            free(runner);
            return;                   
        }
        else
        {
            x[i]=runner->data;
			//lcd_wr_char(x[i]);
			runner=runner->next;			
        }
    }
     
}

