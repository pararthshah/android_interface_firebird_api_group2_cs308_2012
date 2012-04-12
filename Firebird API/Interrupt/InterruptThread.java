/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.Interrupt;

import com.iitb.Basic.Firebird;
import com.iitb.communication.CommunicationModule;

/**
 *
 * @author Darshan
 */
public class InterruptThread implements Runnable {

    private int interrupt;
    private int get_count_command;
    private int count = 0;
    
    public InterruptThread(int interrupt, int get_count_command) {
        this.interrupt = interrupt;
        this.get_count_command = get_count_command;
        CommunicationModule comm = Firebird.getCommunicationModule();
        comm.sendCommand((byte)interrupt);
    }

    //TODO
    public int byteArrayToInt (byte[] arr) {
        int i = 0;
        assert (arr.length == 4);
        for (int j=0; j<arr.length; j++) {
            i = (i << j*8) | arr[j];
        }
        return i;
    }
    
    public void run() {
        while (true) {
            try {
                CommunicationModule comm = Firebird.getCommunicationModule();
                comm.sendCommand((byte)get_count_command);
                byte [] count = new byte[4];
                count = comm.read(4);
                int count_ = byteArrayToInt (count);
                InterruptManager.setInterruptCount(interrupt,count_);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
