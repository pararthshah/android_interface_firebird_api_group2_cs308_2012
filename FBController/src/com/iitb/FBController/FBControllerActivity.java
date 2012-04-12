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
		mConnectButton = (Button) findViewById(R.id.connect);
		mConnectButton.setOnClickListener(ConnectListener);

		mDisconnectButton = (Button) findViewById(R.id.disconnect);
		mDisconnectButton.setOnClickListener(DisconnectListener);

		mSendButton = (Button) findViewById(R.id.send);
		mSendButton.setOnClickListener(SendListener);
		
		mSetPortButton = (Button) findViewById(R.id.setport);
		mSetPortButton.setOnClickListener(SetPortListener);
		
		mGetPortButton = (Button) findViewById(R.id.getport);
		mGetPortButton.setOnClickListener(GetPortListener);

		//Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
		
		fb5 = new Firebird();
		
		mConnected = false;
		if (mConnected){
			Toast.makeText(this, " Connection established ", Toast.LENGTH_SHORT).show();
			mConnectButton.setEnabled(false);
			mDisconnectButton.setEnabled(true);
			mSendButton.setEnabled(true);
			mGetPortButton.setEnabled(true);
			mSetPortButton.setEnabled(true);
		}
			
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

	private OnClickListener SendListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			Toast.makeText(FBControllerActivity.this, "Sending... " + data.length, Toast.LENGTH_SHORT).show();
			fb5.getCommunicationModule().send(data);
		}
	};
	
	private OnClickListener SetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());
			Log.d(tag,"Data size: " + data.length);
			
			if (data.length == 2){
				Toast.makeText(FBControllerActivity.this, "Setting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
				fb5.setPort((char)data[0],data[1]);
			}
			else Toast.makeText(FBControllerActivity.this, "Bad Command!", Toast.LENGTH_SHORT).show();
			
		}
	};
	
	private OnClickListener GetPortListener = new OnClickListener() {
		public void onClick(View v) {
			mInputText = (EditText) FBControllerActivity.this.findViewById(R.id.input);
			byte[] data = parseString(mInputText.getText().toString());			
			Toast.makeText(FBControllerActivity.this, "Getting Port Value... " + data.length, Toast.LENGTH_SHORT).show();
			
			if (data.length == 1){
				Toast.makeText(FBControllerActivity.this, "Waiting for Firebird...", Toast.LENGTH_SHORT).show();
				fb5.getCommunicationModule().setBytesExpected(1);
				fb5.getCommunicationModule().setMsgExpected(PORT_VALUE);
				fb5.getCommunicationModule().setClientHandler(mHandler);
				
				fb5.getPort((char)data[0]);
				
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
