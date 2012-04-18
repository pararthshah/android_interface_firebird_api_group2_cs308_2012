package com.iitb.FBController;

import java.util.StringTokenizer;

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

import com.iitb.fb5.Basic.Firebird;
 
public class FBControllerActivity extends Activity {
	final String tag = "Firebird_API";
	
	final int PORT_VALUE = 10;
	
	Firebird fb5;
	boolean mConnected;
	
	Button mConnectButton;
	Button mDisconnectButton;
	Button mSendButton;
	Button mSetPortButton;
	Button mGetPortButton;
	
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
		}
		
		//If not connected disable the buttons, except connect		
		else{
			Toast.makeText(this, " No connection established ", Toast.LENGTH_SHORT).show();
			mConnectButton.setEnabled(true);
			mDisconnectButton.setEnabled(false);
			mSendButton.setEnabled(false);
			mGetPortButton.setEnabled(false);
			mSetPortButton.setEnabled(false);
		}
			


	}

	/** Called when 'Connect' button is clicked. Starts the connection procedure over BT*/
	private OnClickListener ConnectListener = new OnClickListener()  
	{  
		public void onClick(View v)  
		{         
			Log.d(tag,"Connect Requested");
			Toast.makeText(FBControllerActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
			mConnected = fb5.startup();
			if (mConnected){
				Toast.makeText(FBControllerActivity.this, " Connection established ", Toast.LENGTH_SHORT).show();
				mConnectButton.setEnabled(false);
				mDisconnectButton.setEnabled(true);
				mSendButton.setEnabled(true);
				mGetPortButton.setEnabled(true);
				mSetPortButton.setEnabled(true);
			}
				
			else
				Toast.makeText(FBControllerActivity.this, " No connection established ", Toast.LENGTH_SHORT).show();

			
		}  
	};

	/** Called when 'Disconnect' button is pressed. Frees the BT channel and stop accelerometer listener. */
	private OnClickListener DisconnectListener = new OnClickListener()  
	{  
		public void onClick(View v)  
		{         
			Log.d(tag,"Disonnect Requested");
			fb5.disconnect();
			Toast.makeText(FBControllerActivity.this, " Disconnected ", Toast.LENGTH_SHORT).show();

			mConnectButton.setEnabled(true);
			mDisconnectButton.setEnabled(false);
			mSendButton.setEnabled(false);
			mGetPortButton.setEnabled(false);
			mSetPortButton.setEnabled(false);
		}    
	};

	/** Called when 'Send' button is clicked. Sends the byte currently in the text field*/
	private OnClickListener SendListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			Toast.makeText(FBControllerActivity.this, "Sending... " + data.length, Toast.LENGTH_SHORT).show();
			fb5.getCommunicationModule().send(data);//Sends the data to the firebird
		}
	};
	
	/** Called when 'SetPort' button is clicked. Sends the bytes currently in the text field. The first byte is the Port Number and the second byte is the value it is to be set*/
	private OnClickListener SetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			if (data.length == 2){
				Toast.makeText(FBControllerActivity.this, "Setting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
				fb5.setPort((char)data[0],data[1]);//sets the port value
			}
			else Toast.makeText(FBControllerActivity.this, "Bad Command!", Toast.LENGTH_SHORT).show();
			
		}
	};
	
	/** Called when 'GetPort' button is clicked. Sends the byte currently in the text field. The byte signifies the Port Number, whose value is being requested*/
	private OnClickListener GetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());			
			Toast.makeText(FBControllerActivity.this, "Getting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
			
			if (data.length == 1){
				Toast.makeText(FBControllerActivity.this, "Waiting for Firebird...", Toast.LENGTH_SHORT).show();
				
				//The Android now expects a value from the Firebird, so it sets the message Expected to PORT_VALUE, and sets BytesExpected to 1
				//The Handler listens for this value on the channel
				fb5.getCommunicationModule().setBytesExpected(1);
				fb5.getCommunicationModule().setMsgExpected(PORT_VALUE);
				fb5.getCommunicationModule().setClientHandler(mHandler);
				
				fb5.getPort((char)data[0]);//gets the port value
				
				Log.d(tag, "Waiting for message");
				/*
				 tell bt_comm i am expecting num bytes
				 pass it a handler which should be called when num bytes are received
				 sleep
				
				 */
				
			}
			else Toast.makeText(FBControllerActivity.this, "Bad Command!", Toast.LENGTH_SHORT).show();
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
	
	/*The handler code recieves a message, (in this case the PORT_VALUE), and prnts it out onto the text field*/
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.d(tag, "Handler recieved message");
			if (msg.what == PORT_VALUE){
				Log.d(tag, "Handler processing message");
				byte[] value = (byte[])msg.obj;
				if (value.length == 1){
					Log.d(tag, "Handler processing message");
					mOutputText = (EditText) FBControllerActivity.this.findViewById(R.id.output);
					mOutputText.setText(value[0] + "");
				}
				else Toast.makeText(FBControllerActivity.this, "Firebird Read Error", Toast.LENGTH_SHORT).show();
			}
		}
	};

};
