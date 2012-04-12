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
    public boolean connect ();
    public byte[] read (int num) ;
    public byte[] blockingRead (int num) ;
    public boolean send (byte[] data);
    public void disconnect();
    public boolean sendCommand(byte value);
	public void free_channel();
    
	public void setBytesExpected(int num);
    public void setClientHandler(Handler handler);
    public void setMsgExpected(int type);
    
    public String getMACAddress();
}
