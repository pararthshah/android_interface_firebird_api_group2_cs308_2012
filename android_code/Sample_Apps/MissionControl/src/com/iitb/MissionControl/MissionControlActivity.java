package com.iitb.MissionControl;

import com.iitb.fb5.Basic.Firebird;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MissionControlActivity extends Activity {
	Button forwardButton;
	Button leftButton;
	Button buzzerButton;
	Button rightButton;
	Button reverseButton;

	Button disconnectButton;
	Button connectButton;

	EditText leftProxy;
	EditText centerProxy;
	EditText rightProxy;

	SeekBar velocity;

	TextView statusText;

	Firebird fb5 = null;

	boolean mConnected;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		/*Creating buttons on the App*/
		
		//Button which sends the command to the Firebird to move forward
		forwardButton = (Button) findViewById(R.id.fwdButton);
		
		//Button which sends the command to the Firebird to move backward
		reverseButton = (Button) findViewById(R.id.backButton);
		
		//Button which sends the command to the Firebird to turn left
		leftButton = (Button) findViewById(R.id.leftButton);
		
		//Button which sends the command to the Firebird to turn right
		rightButton = (Button) findViewById(R.id.rightButton);
		
		//Button which sends the command to the Firebird to turn the buzzer on
		buzzerButton = (Button) findViewById(R.id.buzzerButton);

		//Button to disconnect the Android from the Firebird
		disconnectButton= (Button) findViewById(R.id.disconnectButton);
		
		//Button to connect the Android with the Firebird
		connectButton= (Button) findViewById(R.id.connectButton);

		//Button which sends the command to the Firebird to return the value of the left proximity sensor
		leftProxy  = (EditText) findViewById(R.id.leftProxy);
		
		//Button which sends the command to the Firebird to return the value of the right proximity sensor
		rightProxy  = (EditText) findViewById(R.id.rightProxy);
		
		//Button which sends the command to the Firebird to return the value of the front proximity sensor
		centerProxy  = (EditText) findViewById(R.id.forwardProxy);

		velocity = (SeekBar) findViewById(R.id.velocityBar);

		fb5 = new Firebird();

		mConnected = false;
		Toast.makeText(this, " Not connected", Toast.LENGTH_SHORT).show();
		//disable buttons
		forwardButton.setEnabled(false);
		reverseButton.setEnabled(false);
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		reverseButton.setEnabled(false);
		buzzerButton.setEnabled(false);
		velocity.setEnabled(false);
		disconnectButton.setEnabled(false);

		velocity.setProgress(100);
		
		//assign listeners
		/** Called when 'Forward' button is clicked. The firebird moves forward while this button is pressed*/
		forwardButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					fb5.moveForward();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					fb5.stop();
				}
				return true;
			}
		});

		/** Called when 'Backward' button is clicked. The firebird moves backward while this button is pressed*/
		reverseButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					fb5.moveBack();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					fb5.stop();
				}
				return true;
			}
		});

		/** Called when 'Left' button is clicked. The firebird turns left while this button is pressed*/
		leftButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					fb5.moveLeft();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					fb5.stop();
				}
				return true;
			}
		});

		/** Called when 'Right' button is clicked. The firebird turns right while this button is pressed*/
		rightButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					fb5.moveRight();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					fb5.stop();
				}
				return true;
			}
		});

		/** Called when 'Buzzer' button is clicked. The firebird's buzzer is turned on while this button is pressed*/
		buzzerButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					fb5.buzzerOn();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					fb5.buzzerOff();
				}
				return true;
			}
		});

		/** Called when 'Connect' button is clicked. Starts the connection procedure over BT*/
		connectButton.setOnClickListener (new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MissionControlActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
				mConnected = fb5.connect();
				if (mConnected) {
					Toast.makeText(MissionControlActivity.this, "Connection Established", Toast.LENGTH_SHORT).show();
					forwardButton.setEnabled(true);
					reverseButton.setEnabled(true);
					leftButton.setEnabled(true);
					rightButton.setEnabled(true);
					reverseButton.setEnabled(true);
					buzzerButton.setEnabled(true);
					velocity.setEnabled(true);
					connectButton.setEnabled(false);
					disconnectButton.setEnabled(true);
					spt = new SensorPollThread();
					spt.start();
				}
				else {
					Toast.makeText(MissionControlActivity.this, "Please retry", Toast.LENGTH_SHORT).show();
					forwardButton.setEnabled(false);
					reverseButton.setEnabled(false);
					leftButton.setEnabled(false);
					rightButton.setEnabled(false);
					reverseButton.setEnabled(false);
					buzzerButton.setEnabled(false);
					velocity.setEnabled(false);
					connectButton.setEnabled(true);
					disconnectButton.setEnabled(false);
				}
			}
		});

		/** Called when 'Disconnect' button is pressed. Frees the BT channel.*/
		disconnectButton.setOnClickListener (new OnClickListener() {
			public void onClick(View v) {
				fb5.disconnect();
				mConnected = false;
				Toast.makeText(MissionControlActivity.this, "Disconnected!", Toast.LENGTH_SHORT).show();
				forwardButton.setEnabled(false);
				reverseButton.setEnabled(false);
				leftButton.setEnabled(false);
				rightButton.setEnabled(false);
				reverseButton.setEnabled(false);
				buzzerButton.setEnabled(false);
				velocity.setEnabled(false);
				connectButton.setEnabled(true);
				disconnectButton.setEnabled(false);
			}
		});

		velocity.setMax(255);

		//Velocity is set by the value on the seek Bar
		velocity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				seekBar.setSecondaryProgress(seekBar.getProgress());
				int val = seekBar.getProgress();
				fb5.setVelocity((byte)val,(byte)val);
			}
		});

	}

	SensorPollThread spt = null;

	final int LEFT_PROXY = 4;
	final int FWD_PROXY = 6;
	final int RIGHT_PROXY = 8;
	boolean take_reading = true;
	
	//Thread for reading the values of the proximity sensors
	class SensorPollThread extends Thread {

		public void run() {
			while (true) {
				if (take_reading && mConnected) {	
					take_reading = false;
					fb5.getSensor((char)LEFT_PROXY);
					fb5.getSensor((char)FWD_PROXY);
					fb5.getSensor((char)RIGHT_PROXY);

					fb5.getCommunicationModule().setBytesExpected(1);
					fb5.getCommunicationModule().setMsgExpected(LEFT_PROXY);
					fb5.getCommunicationModule().setClientHandler(mHandler);
				}
			}
		}
	};

	Handler mHandler = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			Log.d("CONTROL PANEL", "Handler recieved message " + msg.what);
			//reads the value of the left proximity sensor, sets msg expected to fws_proxy
			if (msg.what == LEFT_PROXY){
				byte[] value = (byte[])msg.obj;
				leftProxy.setText(getReading(value[0]) + "");
				fb5.getCommunicationModule().setBytesExpected(1);
				fb5.getCommunicationModule().setMsgExpected(FWD_PROXY);
				fb5.getCommunicationModule().setClientHandler(mHandler);
			}
			//reads the value of the forward proximity sensor, sets msg expected to Right_proxy
			else if (msg.what == FWD_PROXY){
				byte[] value = (byte[])msg.obj;
				centerProxy.setText(getReading(value[0]) + "");
				fb5.getCommunicationModule().setBytesExpected(1);
				fb5.getCommunicationModule().setMsgExpected(RIGHT_PROXY);
				fb5.getCommunicationModule().setClientHandler(mHandler);
			}
			//reads the value of the right proximity sensor, sets take_reading to true so that more readings can be taken
			else if (msg.what == RIGHT_PROXY){
				byte[] value = (byte[])msg.obj;
				rightProxy.setText(getReading(value[0]) + "");
				take_reading = true;
			}
		}
		private int getReading (byte b) {
			if (b < 0) return b + 256;
			else return b;
		}
		
	};


}