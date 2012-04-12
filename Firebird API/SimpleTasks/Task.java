/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.SimpleTasks;

import com.iitb.Basic.Commands;
import com.iitb.Basic.Firebird;
import com.iitb.communication.CommunicationModule;

/**
 *
 * @author pararth
 */
public class Task {

    
    public boolean whitelineFollowStart() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.WHITELINE_START);
    }
    
    public boolean whitelineFollowStop() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.WHITELINE_STOP);
    }
    
    public boolean buzzerOn() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.BUZZER_ON);
    }
    
    public boolean buzzerOff() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.BUZZER_OFF);
    }
    
    public boolean moveForward() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.MOVE_FORWARD);
    }
    
    public boolean moveBack() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.MOVE_BACKWARD);
    }
    
    public boolean moveRight() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.MOVE_RIGHT);
    }
    
    public boolean moveLeft() {
        return Firebird.getCommunicationModule().sendCommand((byte)Commands.MOVE_LEFT);
    }
    
    public boolean setLcdString(byte[] value) {
        CommunicationModule comm = Firebird.getCommunicationModule();
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
    	return Firebird.getCommunicationModule().send(data);
    }
    
    /*Have to define functions for getSensor() and getPort();*/
    
}
