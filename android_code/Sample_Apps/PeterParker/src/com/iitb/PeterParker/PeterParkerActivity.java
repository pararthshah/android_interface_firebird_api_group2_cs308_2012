package com.iitb.PeterParker;

import java.util.StringTokenizer;

import com.iitb.fb5.Basic.Firebird;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PeterParkerActivity extends Activity {
	final String tag = "Firebird_API";
	
	final int PORT_VALUE = 10;
	final int LEFT_PROX = 4;
	final int RIGHT_PROX = 8;
	final int LEFT_SHARP = 9;
	final int RIGHT_SHARP = 13;
	final int BOT_REACHED_INTERSECTION = 25;
	final int BOT_REACHED_INTERSECTION_2 = 29;
	final int BOT_IN_PARKING_POS = 26;
	final int BOT_PARKED = 27;
	final int TURN_ACK = 28;
	final int LEFT_SHARP_THRESHOLD = 80;
	final int RIGHT_SHARP_THRESHOLD = 80;
	
	final int U_TURN = 35;
	final int LEFT_TURN = 15;
	final int FWD_MOVE_INTERSECTION = 6;
	final int BACK_MOVE_PARKED = 20;
	
	Firebird fb5;
	boolean mConnected;
	
	Button mConnectButton;
	Button mDisconnectButton;
	Button mSendButton;
	Button mSetPortButton;
	Button mGetPortButton;
	Button mParkButton;
	
	int leftsharp,rightsharp,leftwhiteline,centerwhiteline,rightwhiteline;
	boolean take_values = true;
	
	EditText mInputText;
	EditText mOutputText; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*Creating buttons on the App*/
		//Button to connect the Android with the Firebird
		mConnectButton = (Button) findViewById(R.id.connect);
		mConnectButton.setOnClickListener(ConnectListener);

		//Button to disconnect the Android from the Firebird
		mDisconnectButton = (Button) findViewById(R.id.disconnect);
		mDisconnectButton.setOnClickListener(DisconnectListener);

		//Button to send a command to the Firebird
		mSendButton = (Button) findViewById(R.id.send);
		mSendButton.setOnClickListener(SendListener);
		
		//Button which sends the command to set a port to the Firebird
		mSetPortButton = (Button) findViewById(R.id.setport);
		mSetPortButton.setOnClickListener(SetPortListener);
		
		//Button which sends the command to get a port value from the Firebird
		mGetPortButton = (Button) findViewById(R.id.getport);
		mGetPortButton.setOnClickListener(GetPortListener);

		//Button which sends the command to park the Firebird
		mParkButton = (Button) findViewById(R.id.park);
		mParkButton.setOnClickListener(ParkListener);

		//Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
		
		fb5 = new Firebird();
		
		mConnected = false;
		//If connected enable the buttons, except connect
		if (mConnected){
			Toast.makeText(this, " Connection established ", Toast.LENGTH_SHORT).show();
			mConnectButton.setEnabled(false);
			mDisconnectButton.setEnabled(true);
			mSendButton.setEnabled(true);
			mGetPortButton.setEnabled(true);
			mSetPortButton.setEnabled(true);
			mParkButton.setEnabled(true);
		}
		//If connected disable the buttons, except connect	
		else{
			Toast.makeText(this, " No connection established ", Toast.LENGTH_SHORT).show();
			mConnectButton.setEnabled(true);
			mDisconnectButton.setEnabled(false);
			mSendButton.setEnabled(false);
			mGetPortButton.setEnabled(false);
			mSetPortButton.setEnabled(false);
			mParkButton.setEnabled(false);
		}
			


	}

	/** Called when 'Connect' button is clicked. Starts the connection procedure over BT*/
	private OnClickListener ConnectListener = new OnClickListener()  
	{  
		public void onClick(View v)  
		{         
			Log.d(tag,"Connect Requested");
			Toast.makeText(PeterParkerActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
			mConnected = fb5.startup();
			if (mConnected){
				Toast.makeText(PeterParkerActivity.this, " Connection established ", Toast.LENGTH_SHORT).show();
				mConnectButton.setEnabled(false);
				mDisconnectButton.setEnabled(true);
				mSendButton.setEnabled(true);
				mGetPortButton.setEnabled(true);
				mSetPortButton.setEnabled(true);
				mParkButton.setEnabled(true);
			}
				
			else
				Toast.makeText(PeterParkerActivity.this, " No connection established ", Toast.LENGTH_SHORT).show();

			
		}  
	};

	/** Called when 'Disconnect' button is pressed. Frees the BT channel. */
	private OnClickListener DisconnectListener = new OnClickListener()  
	{  
		public void onClick(View v)  
		{         
			Log.d(tag,"Disonnect Requested");
			fb5.disconnect();
			Toast.makeText(PeterParkerActivity.this, " Disconnected ", Toast.LENGTH_SHORT).show();

			mConnectButton.setEnabled(true);
			mDisconnectButton.setEnabled(false);
			mSendButton.setEnabled(false);
			mGetPortButton.setEnabled(false);
			mSetPortButton.setEnabled(false);
			mParkButton.setEnabled(false);
		}    
	};

	/** Called when 'Send' button is clicked. Sends the byte currently in the text field*/
	private OnClickListener SendListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) PeterParkerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			Toast.makeText(PeterParkerActivity.this, "Sending... " + data.length, Toast.LENGTH_SHORT).show();
			fb5.getCommunicationModule().send(data);
		}
	};
	
	/** Called when 'SetPort' button is clicked. Sends the bytes currently in the text field. The first byte is the Port Number and the second byte is the value it is to be set*/
	private OnClickListener SetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) PeterParkerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			if (data.length == 2){
				Toast.makeText(PeterParkerActivity.this, "Setting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
				fb5.setPort((char)data[0],data[1]);
			}
			else Toast.makeText(PeterParkerActivity.this, "Bad Command!", Toast.LENGTH_SHORT).show();
			
		}
	};
	
	/** Called when 'GetPort' button is clicked. Sends the byte currently in the text field. The byte signifies the Port Number, whose value is being requested*/
	private OnClickListener GetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) PeterParkerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());			
			Toast.makeText(PeterParkerActivity.this, "Getting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
			
			if (data.length == 1){
				Toast.makeText(PeterParkerActivity.this, "Waiting for Firebird...", Toast.LENGTH_SHORT).show();
				fb5.getCommunicationModule().setBytesExpected(1);
				fb5.getCommunicationModule().setMsgExpected(PORT_VALUE);
				fb5.getCommunicationModule().setClientHandler(mHandler);
				
				fb5.getPort((char)data[0]);
				
				Log.d(tag, "Waiting for message");
			}
			else Toast.makeText(PeterParkerActivity.this, "Bad Command!", Toast.LENGTH_SHORT).show();
		}
	};

	/*This thread sends messages to Firebird to turn on Adaptive Cruise Control, and to stop on an intersection while white line following*/
	class ParkingThread extends Thread {
		public void run() {
			while (true) {
				
				if (take_values) {
					
					Log.d("PARKING APP", "Taking values..");
					
					fb5.getCommunicationModule().sendCommand((byte)77); // Turn on ACC
					fb5.getCommunicationModule().sendCommand((byte)76); // White line stop on intersection
					fb5.whitelineFollowStart();

					/*The Firebird awaits a meassage from the Firebird that it has reached the intersection*/
					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(BOT_REACHED_INTERSECTION);
					fb5.getCommunicationModule().setClientHandler(mHandler);
					
					take_values = false;
				}
			}
		}
	}
	
	ParkingThread pt = null;
	/** Called when 'Park' button is clicked. Starts the Parking thread*/
	private OnClickListener ParkListener = new OnClickListener() {
		public void onClick(View v) {
			
			Toast.makeText(PeterParkerActivity.this, "Parking... " , Toast.LENGTH_SHORT).show();
			Log.d ("PARKING APP", "Parking...");

			Toast.makeText(PeterParkerActivity.this, "Waiting for Firebird...", Toast.LENGTH_SHORT).show();
			Log.d ("PARKING APP", "Waiting for Firebird...");
			
			take_values = true;
			
			pt = new ParkingThread();
			pt.start();
			
		}
	};	
	
	private byte[] parseString(String send_str){
		StringTokenizer st = new StringTokenizer(send_str, " ");
		byte[] data = new byte[st.countTokens()];
		int count = 0;
		while(st.hasMoreTokens()) { 
			String val = st.nextToken(); 
			int num = Integer.parseInt(val);
			data[count] = (byte)num;
			count++;
		}
		return data;
	}
	
	/*Handler Code:*/
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.d(tag, "Handler recieved message " + msg.what);
			//Handler code for recieving the value of a Port
			if (msg.what == PORT_VALUE){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					mOutputText = (EditText) PeterParkerActivity.this.findViewById(R.id.output);
					mOutputText.setText(value[0] + "");
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//Handler code for when the bot has almost finished parking. The bot is asked to reverse into the parking slot
			else if (msg.what == BOT_PARKED){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					fb5.setVelocity((byte)200, (byte)200);
					fb5.moveBackBy(BACK_MOVE_PARKED);
					Toast.makeText(PeterParkerActivity.this, "Parked!", Toast.LENGTH_SHORT).show();
					Log.d ("PARKING APP", "Parked!");
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//Handler code for when the bot has reached the parking position(but not yet parked). The bot is asked to make a U-Turn. The Android expexcts an Acknowledment in the form of a BOT_PARKED message after the U-turn is complete
			else if (msg.what == BOT_IN_PARKING_POS){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					//Go forward and park yourself!
					fb5.turnRightBy(U_TURN); // U-turn!

					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(BOT_PARKED);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//The Handler code which asks the bot to move forward and park itself at the next intersection
			else if (msg.what == TURN_ACK){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					//Go forward and park yourself!
					fb5.getCommunicationModule().sendCommand((byte)76); // White line stop on intersection
					fb5.whitelineFollowStart();
					Toast.makeText(PeterParkerActivity.this, "Going to Park...", Toast.LENGTH_SHORT).show();
					Log.d ("PARKING APP", "Going to Park...");
					
					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(BOT_IN_PARKING_POS);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//When the bot has reached the intersection, we ask it to move slightly ahead and send a BOT_REACHED_INTERSECTION_2 msg on completion
			else if (msg.what == BOT_REACHED_INTERSECTION){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					fb5.moveBy (FWD_MOVE_INTERSECTION);
					//fb5.getSensor ((char) RIGHT_SHARP);
					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(BOT_REACHED_INTERSECTION_2);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//When the bot has reached this intersection point, it requests the left sharp sensor value from the bot to check if the parking space is free
			else if (msg.what == BOT_REACHED_INTERSECTION_2){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					fb5.getSensor ((char) LEFT_SHARP);
					//fb5.getSensor ((char) RIGHT_SHARP);
					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(LEFT_SHARP);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			//Handler code which reads the left sharp sensor value. If there is no obstacle on the left, we start the procedure and expect a TURNIG_ACK message from the bot after it turns left. Else we ask the bot to move forward as usual
			else if (msg.what == LEFT_SHARP){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					leftsharp = (int)value[0];
					leftsharp = leftsharp < 0 ? 256 + leftsharp : leftsharp;
					if (leftsharp < LEFT_SHARP_THRESHOLD) {
						//space to left is free!
						fb5.whitelineFollowStop();
						fb5.turnLeftBy(LEFT_TURN);
						fb5.getCommunicationModule().setBytesExpected(1);
						fb5.getCommunicationModule().setMsgExpected(TURN_ACK);
						fb5.getCommunicationModule().setClientHandler(mHandler);
					}
					/*else if (rightsharp < RIGHT_SHARP_THRESHOLD) {
						//space to right is free!
						fb5.turnRightBy(10);
						fb5.getCommunicationModule().setBytesExpected(1);
						fb5.getCommunicationModule().setMsgExpected(TURN_ACK);
						fb5.getCommunicationModule().setClientHandler(mHandler);
					} */
					else {
						take_values = true;
					}
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
			
			//Handler code which reads the right sharp sensor value. The next value expected is the Left Sharp Sensor Value
			else if (msg.what == RIGHT_SHARP){
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					rightsharp = (int)value[0];
					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(LEFT_SHARP);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
				else Toast.makeText(PeterParkerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
		}
	};
}