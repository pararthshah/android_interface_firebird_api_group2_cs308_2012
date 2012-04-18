/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.fb5.Communication;

import android.os.Handler;

/**
 *
 * @author pararth
 */
public interface CommunicationModule {
    
	public boolean initialise(String add_string) throws Exception;
	
	//Called to establish a bluetooth connection with the Firebird
    public boolean connect ();
	
	//Reads num bytes of data from the buffer, if available
    public byte[] read (int num) ;
	
	//Reads num bytes of data from the buffer, if available. If num bytes are not available, this command blocks the android until the remaining numbe of bytes are recieved
    public byte[] blockingRead (int num) ;
	
	//Sends an array of bytes through the bluetooth channel to the Firebird
    public boolean send (byte[] data);
	
	//Called to disconnect the bluetooth connection
    public void disconnect();
	
	//Sends a single byte command (i.e no arguments commands) to the firebird
    public boolean sendCommand(byte value);
	
	//Closes the bluetooth connection
	public void free_channel();
    
	//This sets the number of bytes expected by the Android from the Firebird
	public void setBytesExpected(int num);
	
	//This sets the Client Handler of the Android App which will be called when a message is recieved
    public void setClientHandler(Handler handler);
	
	//This sets the type of message expected by the Android from the Firebird
    public void setMsgExpected(int type);
    
	//returns the MAC address of the Android device
    public String getMACAddress();
}
