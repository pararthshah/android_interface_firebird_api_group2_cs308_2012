/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
	private String FB_5_BT_ADDRESS = "00:19:A4:02:C6:7E";
	
	public Firebird(){}
	
	public Firebird(String addr){
		FB_5_BT_ADDRESS = addr;
	}
	
    private CommunicationModule comm;
    //public Task task;
    public boolean connect() {
        return startup();
    }
    
    public CommunicationModule getCommunicationModule() {
        return comm;
    }
    
    /** Initialisation function.
  	 * Called from : 'Connect' button click listener.
  	 * Task: (1)Establish connection between phone and bluetooth module on FB5. 
  	 * 		 (2)Start a listener for changes in value of Accelerometer sensor.
  	 * Arguments : Null 
  	 * Return : Null
  	 */
  	public boolean startup()
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
  	
  	public boolean disconnect(){
  		sendDisconnect();
  		if(comm != null){
  			comm.free_channel(); /**Free up the BT channel. */
  			return true;
  		}
  		else return false;
		
  	}

  	public boolean printState() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.PRINT_STATE;
        return comm.send(message);
  	}
  	
  	public boolean enableLeftWheelInterrupt() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.ENABLE_LEFT_WHEEL_INTERRUPT;
        return comm.send(message);
  	}
  	
  	public boolean enableRightWheelInterrupt() {
  		byte[] message = new byte[1];
        message[0] = (byte) Commands.ENABLE_RIGHT_WHEEL_INTERRUPT;
        return comm.send(message);
  	}

  	public boolean getLeftPosInterruptCount () {
        byte[] message = new byte[1];
        message[0] = (byte) Commands.GET_LEFT_WHEEL_INTERRUPT_COUNT;
        return comm.send(message);
    }
  	
  	public boolean setPort(char port, byte value) {
        byte[] message = new byte[4];
        message[0] = (byte) Commands.SET_PORT;
        message[1] = (byte) 2;
        message[2] = (byte) port;
        message[3] = value;
        return comm.send(message);
    }
    
	public boolean setVelocity(byte valueleft,byte valueright) {
        byte[] message = new byte[4];
        message[0] = (byte) Commands.SET_VELOCITY;
        message[1] = (byte) 2;
		message[2] = valueleft;
        message[3] = valueright;
        return comm.send(message);
    }
    
    /* This is a blocking call! */
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
	
	public boolean whitelineFollowStart() {
        return getCommunicationModule().sendCommand((byte)Commands.WHITELINE_START);
    }
    
    public boolean whitelineFollowStop() {
        return getCommunicationModule().sendCommand((byte)Commands.WHITELINE_STOP);
    }
    
    public boolean accModifiedStart() {
        return getCommunicationModule().sendCommand((byte)Commands.ACC_MODIFIED);
    }
    
    public boolean accCheck() {
    	return getCommunicationModule().sendCommand((byte)Commands.ACC_CHECK);
    }
    
    public boolean accStop() {
    	return getCommunicationModule().sendCommand((byte)Commands.ACC_STOP);
    }
    
    public boolean buzzerOn() {
        return getCommunicationModule().sendCommand((byte)Commands.BUZZER_ON);
    }
    
    public boolean buzzerOff() {
        return getCommunicationModule().sendCommand((byte)Commands.BUZZER_OFF);
    }
    
    public boolean moveForward() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_FORWARD);
    }
    
    public boolean moveBack() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_BACKWARD);
    }
    
    public boolean moveRight() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_RIGHT);
    }
    
    public boolean moveLeft() {
        return getCommunicationModule().sendCommand((byte)Commands.MOVE_LEFT);
    }
    
    public boolean stop() {
        return getCommunicationModule().sendCommand((byte)Commands.STOP);
    }
    
    public boolean moveBy(int secs) {
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.MOVE_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (secs & 0xFF);
        message[3] = (byte) ((secs >> 8) & 0xFF);
        return comm.send(message);
    }
    
    public boolean moveBackBy(int secs) {
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.MOVE_BACK_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (secs & 0xFF);
        message[3] = (byte) ((secs >> 8) & 0xFF);
        return comm.send(message);
    }
    
    public boolean turnLeftBy(int degrees){
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.TURN_LEFT_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (degrees & 0xFF);
        message[3] = (byte) ((degrees >> 8) & 0xFF);
        return comm.send(message);
    }
    
    public boolean turnRightBy(int degrees){
    	CommunicationModule comm = getCommunicationModule();
        byte[] message = new byte[4];
        message[0] = (byte) Commands.TURN_RIGHT_BY;
        message[1] = (byte) 2;
        message[2] = (byte) (degrees & 0xFF);
        message[3] = (byte) ((degrees >> 8) & 0xFF);
        return comm.send(message);
    }
    
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
    
    public boolean sendRawData(byte[] data){
    	return getCommunicationModule().send(data);
    }
    
    public boolean sendDisconnect(){
    	return getCommunicationModule().sendCommand((byte)Commands.DISCONNECT);
    }
}