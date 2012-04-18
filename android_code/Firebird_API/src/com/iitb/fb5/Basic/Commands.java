/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.fb5.Basic;

/**
 *
 * @author aditya
 */
public class Commands {

/*The following set of Commands is the basic API used by the Android to send messages to the Firebird 
Commands which do not require any further arguments have been set to a number > 64 e.g MOVE_ FORWARD
Commands which do require further arguments have been set to a number < 64 e.g SET_PORT (PORTC)
One simple has to call Firebird.send(Command) to send the command to the firebird. e.g Firebird.send(BUZZER_ON);
*/
    public static char SET_PORT = 33;
    public static char GET_PORT = 35;
    public static char GET_SENSOR = 34;
    public static char SET_LCD_STRING = 32;
	public static char SET_VELOCITY = 36;
	public static char MOVE_BY = 37;
	public static char MOVE_BACK_BY = 40;
	public static char TURN_LEFT_BY = 38;
	public static char TURN_RIGHT_BY = 39;
	public static char PRINT_STATE = 90;
    public static char WHITELINE_START = 74;
    public static char WHITELINE_STOP = 75;
    public static char WHITELINE_INTERSECTION = 76;
    public static char BUZZER_ON = 65;
    public static char BUZZER_OFF = 66;
    public static char MOVE_FORWARD = 67;
    public static char MOVE_BACKWARD = 68;
    public static char MOVE_RIGHT = 69;
    public static char MOVE_LEFT = 70;
    public static char STOP = 71;
    public static char ACC_START = 77;
    public static char ACC_STOP = 78;
    public static char ACC_CHECK = 79;
    public static char ACC_MODIFIED = 80;
    public static char GET_LEFT_WHEEL_INTERRUPT_COUNT = 81;
    public static char GET_RIGHT_WHEEL_INTERRUPT_COUNT = 82;
    public static char ENABLE_LEFT_WHEEL_INTERRUPT = 83;
    public static char ENABLE_RIGHT_WHEEL_INTERRUPT = 84;
    
    public static char DISCONNECT = 120;
}
