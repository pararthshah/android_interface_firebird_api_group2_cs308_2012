package com.iitb.fb5.Basic;

import android.util.Log;

import com.iitb.fb5.Communication.BluetoothCommunicationModule;
import com.iitb.fb5.Communication.CommunicationModule;

/**
 *
 * @author pararth
 */
public class Firebird {
	
	final String tag = "Firebird";
	
	/** MAC address of bluetooth module attached to Firebird */
	private String FB_5_BT_ADDRESS = "00:19:A4:02:C6:7E";//enter the MAC Address of your firebird here

    private CommunicationModule comm;
    
	public Firebird(){}
	
	public Firebird(String addr){//Sets the MAC ADDRESS TO addr
		FB_5_BT_ADDRESS = addr;
	}
	
    public boolean connect() {//initiates the connection process
        return startup();
    }
    
    public CommunicationModule getCommunicationModule() {// returns the CommunicationModule
        return comm;
    }
    
    /** Initialisation function.
  	 * Called from : 'Connect' button click listener.
  	 * Task: (1)Establish connection between phone and bluetooth module on FB5. 
  	 * 		 (2)Start a listener for changes in value of Accelerometer sensor.
  	 * Arguments : Null 
  	 * Return : Null
  	 */
  	public boolean startup()//initialises the connection process
  	{
  		comm = new BluetoothCommunicationModule();
  		
  		try {
  			Log.d(tag, "Initialisation Started...");
  			
  			/** Bluetooth initialise function returns true if connection is succesful, else false. */
  			if(comm.initialise(FB_5_BT_ADDRESS) == false) 
  			{	
  				return false;
  			}
  			else 
  			{
  	  			Log.d(tag, "Initialisation Successful");
  	  	        //task = new Task();
  				return true;
  			}
  		} catch (Exception e) {
  			e.printStackTrace();
  			Log.e(tag, "Initialisation Failed");
  			return false;
  		}  		
  		
  	}
  	
  	public boolean disconnect(){//disconnects the channel
  		sendDisconnect();
  		if(comm != null){
  			comm.free_channel(); /**Free up the BT channel. */
  			return true;
  		}
  		else return false;
		
  	}

	/*
	The following functions are called to send commands to the Firebird
	If the command does not require any arguments (e.g) Move Forward, the only one byte of data is sent
	If the command requires arguments (e.g) Get Port, then multiples byte are sent.
	The format is that the first byte specifies the command, the second byte specifies the number of bytes (arguments) further expected
	The next few bytes sent (as specified by the second byte sent) will be treated as arguments for the command
	*/
	
	//prints the value of the current state of the bot
  	public boolean printState() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.PRINT_STATE;
        return comm.send(message);
  	}
  	
	// Initialises the Optical encoder on the left wheel
  	public boolean enableLeftWheelInterrupt() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.ENABLE_LEFT_WHEEL_INTERRUPT;
        return comm.send(message);
  	}
  	
	// Initialises the Optical encoder on the right wheel
  	public boolean enableRightWheelInterrupt() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.ENABLE_RIGHT_WHEEL_INTERRUPT;
        return comm.send(message);
  	}

	// Requests the Firebird to return the left wheel interrupt count
  	public boolean getLeftPosInterruptCount () {
        byte[] message = new byte[1];
        message[0] = (byte) Commands.GET_LEFT_WHEEL_INTERRUPT_COUNT;
        return comm.send(message);
    }
  	
	//Sets the port signified by the 'port' variable (e.g PORT A) equal to the value
  	public boolean setPort(char port, byte value) {
        byte[] message = new byte[4];
        message[0] = (byte) Commands.SET_PORT;
        message[1] = (byte) 2;
        message[2] = (byte) port;
        message[3] = value;
        return comm.send(message);
    }
    
	//Sets the velocity of the left wheel of the bot to valueleft and the right wheel to valueright
	public boolean setVelocity(byte valueleft,byte valueright) {
        byte[] message = new byte[4];
        message[0] = (byte) Commands.SET_VELOCITY;
        message[1] = (byte) 2;
		message[2] = valueleft;
        message[3] = valueright;
        return comm.send(message);
    }
    
    /* This is a blocking call! */
	//Gets the value of the port as signified by the char variable 'port'e.g (PORTA)
    public boolean getPort (char port) {
        byte[] message = new byte[3];
        message[0] = (byte) Commands.GET_PORT;
        message[1] = (byte) 1;
        message[2] = (byte) port;
        boolean sent = comm.send(message);
        if (!sent) return false;
        //byte port_value = comm.blockingRead(1)[0];
        return true;
    }
	
	//Gets the value of the sensor as signified by the char variable 'sensor'e.g (if sensor = 11, it signifies the front sharp sensor)
	public boolean getSensor (char sensor) {
        byte[] message = new byte[3];
        message[0] = (byte) Commands.GET_SENSOR;
        message[1] = (byte) 1;
        message[2] = (byte) sensor;
        boolean sent = comm.send(message);
        if (!sent) return false;
        //byte port_value = comm.blockingRead(1)[0];
        return true;
    }
	
	//This command makes the firebird start white line following
	public boolean whitelineFollowStart() {
        return getCommunicationModule().sendCommand((byte)Commands.WHITELINE_START);
    }
    
	//This command makes the firebird stop white line following
    public boolean whitelineFollowStop() {
        return getCommunicationModule().sendCommand((byte)Commands.WHITELINE_STOP);
    }
	
    //This command makes the firebird start Adaptive Cruise Control. This modified version of Acc is different in that it automatically stops ACC when the Firebird sees an obstacle
    public boolean accModifiedStart() {
        return getCommunicationModule().sendCommand((byte)Commands.ACC_MODIFIED);
    }
    
	//This command checks if the firebird has been stopped by an obstacle during Adaptive Cruise Control
    public boolean accCheck() {
    	return getCommunicationModule().sendCommand((byte)Commands.ACC_CHECK);
    }
    
	//This command makes the firebird stop Adaptive Cruise Control
    public boolean accStop() {
    	return getCommunicationModule().sendCommand((byte)Commands.ACC_STOP);
    }
    
	//This command sets the buzzer on
    public boolean buzzerOn() {
        return getCommunicationModule().sendCommand((byte)Commands.BUZZER_ON);
    }
    
	//This command sets the buzzer off
    public boolean buzzerOff() {
        return getCommunicationModule().sendCommand((byte)Commands.BUZZER_OFF);
    }
    
	//This command makes the firebird move forward
    public boolean moveForward() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_FORWARD);
    }
    
	//This command makes the firebird move backward
    public boolean moveBack() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_BACKWARD);
    }
    
	//This command makes the firebird turn right
    public boolean moveRight() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_RIGHT);
    }
    
	//This command makes the firebird turn left
    public boolean moveLeft() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_LEFT);
    }
    
	//This command stops the motion of the Firebird
    public boolean stop() {
        return getCommunicationModule().sendCommand((byte)Commands.STOP);
    }
    
	//This command asks the Firebird to move forward for a specified number of timer delays (overflows)
    public boolean moveBy(int numdelays) {
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.MOVE_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (numdelays & 0xFF);
        message[3] = (byte) ((numdelays >> 8) & 0xFF);
        return comm.send(message);
    }
    
	//This command asks the Firebird to move backward for a specified number of timer delays (overflows)
    public boolean moveBackBy(int numdelays) {
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.MOVE_BACK_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (numdelays & 0xFF);
        message[3] = (byte) ((numdelays >> 8) & 0xFF);
        return comm.send(message);
    }
    
	//This command asks the Firebird to turn Left for a specified number of timer delays (overflows)
    public boolean turnLeftBy(int numdelays){
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.TURN_LEFT_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (numdelays & 0xFF);
        message[3] = (byte) ((numdelays >> 8) & 0xFF);
        return comm.send(message);
    }
    
	//This command asks the Firebird to turn right for a specified number of timer delays (overflows)
    public boolean turnRightBy(int numdelays){
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.TURN_RIGHT_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (numdelays & 0xFF);
        message[3] = (byte) ((numdelays >> 8) & 0xFF);
        return comm.send(message);
    }
    
	//This command asks the Firebird to print 'value' on the the Lcd
    public boolean setLcdString(byte[] value) {
        CommunicationModule comm = getCommunicationModule();
        int size = value.length;
        byte[] message = new byte[size + 2];
        message[0] = (byte) Commands.SET_LCD_STRING;
        message[1] = (byte) size;
        for(int i =0; i<size;i++){
            message[2 + i] = (byte) value[i];
        }
        return comm.send(message);
    }
    
	//This command sends a byte of data
    public boolean sendRawData(byte[] data){
    	return getCommunicationModule().send(data);
    }
    
	//The command sends a message to the Firebird that the Android is disconnecting the Bluetooth
    public boolean sendDisconnect(){
    	return getCommunicationModule().sendCommand((byte)Commands.DISCONNECT);
    }
}