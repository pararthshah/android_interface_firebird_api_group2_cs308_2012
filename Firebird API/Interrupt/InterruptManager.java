/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.Interrupt;

import java.util.HashMap;

import com.iitb.Basic.*;

/**
 *
 * @author Darshan
 */
public class InterruptManager {

    public static final int LEFT_WHEEL = Commands.ENABLE_LEFT_WHEEL_INTERRUPT;
    public static final int RIGHT_WHEEL = Commands.ENABLE_RIGHT_WHEEL_INTERRUPT;
    public static final int TIMER3 = 2;
    public static int left_wheel_interrupt_count = 0;
    public static int right_wheel_interrupt_count = 0;
    static HashMap<Integer, Thread> interruptPollingThreads = null;

    public static void useInterrupt(int interrupt) {

        if (interruptPollingThreads == null) {
            interruptPollingThreads = new HashMap<Integer, Thread>();
        }
        int get_count_command = 0;
        if (interrupt == LEFT_WHEEL) {
            get_count_command = Commands.GET_LEFT_WHEEL_INTERRUPT_COUNT;
        } else if (interrupt == RIGHT_WHEEL) {
            get_count_command = Commands.GET_RIGHT_WHEEL_INTERRUPT_COUNT;
        }
        InterruptThread it = new InterruptThread(interrupt, get_count_command);
        Thread poller = new Thread(it);
        poller.start();
        interruptPollingThreads.put(interrupt, poller);

    }

    public static int getInterruptCount(int interrupt) {
        if (interrupt == LEFT_WHEEL) {
            int temp = left_wheel_interrupt_count;
            left_wheel_interrupt_count = 0;
            return temp;
        } else if (interrupt == RIGHT_WHEEL) {
            int temp = right_wheel_interrupt_count;
            right_wheel_interrupt_count = 0;
            return temp;
        }
        return 0;
    }

    static void setInterruptCount(int interrupt, int count) {
        if (interrupt == LEFT_WHEEL) {
            left_wheel_interrupt_count = count;
        } else if (interrupt == RIGHT_WHEEL) {
            right_wheel_interrupt_count = count;
        }
    }
}
