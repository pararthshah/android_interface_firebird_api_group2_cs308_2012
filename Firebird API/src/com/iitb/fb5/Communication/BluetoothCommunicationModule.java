/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iitb.fb5.Communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *
 * @author Darshan
 */
public class BluetoothCommunicationModule implements CommunicationModule {

    private final String TAG = "BT_COMM";
    private String mDeviceAddress;

    
    private int mExpectedBytes = 0;
    private int mExpectedMsg = -1;
    Handler mClientHandler = null;
    
    public synchronized void setBytesExpected(int num){
    	mExpectedBytes = num;
    }

    public synchronized void setMsgExpected(int type){
    	mExpectedMsg = type;
    }
    
    public synchronized void setClientHandler(Handler handler){
    	mClientHandler = handler;
    	
    	if (mClientHandler == null) return;
		if (buffer.size() >= mExpectedBytes){
			byte[] rcvd_bytes = new byte[mExpectedBytes];
			for (int i = 0; i < mExpectedBytes; i++){
				rcvd_bytes[i] = buffer.remove(0);
			}
			Log.d(TAG, "Handler sending message");
			mClientHandler.obtainMessage(mExpectedMsg, rcvd_bytes.length, -1, rcvd_bytes)
						.sendToTarget();
			mClientHandler = null;
			mExpectedBytes = 0;
			mExpectedMsg = -1;
		}
    }
        
    public static final int MESSAGE_READ = 0;
    
    Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg) {
    		Log.d(TAG, "mHandler : " + msg.what + " " + msg.arg1);
    		if (msg.what == MESSAGE_READ){
    			Log.d(TAG, "Handler recieved message");
    			byte[] readBuf = (byte[]) msg.obj;
    			for (int i = 0; i < msg.arg1; i++) {
                    BluetoothCommunicationModule.buffer.add(readBuf[i]);
                }
    		}
    		if (mClientHandler == null) return;
    		
    		synchronized(mClientHandler){
    			if (buffer.size() >= mExpectedBytes){
        			byte[] rcvd_bytes = new byte[mExpectedBytes];
        			for (int i = 0; i < mExpectedBytes; i++){
        				rcvd_bytes[i] = buffer.remove(0);
        			}
        			Log.d(TAG, "Handler sending message");
        			mClientHandler.obtainMessage(mExpectedMsg, rcvd_bytes.length, -1, rcvd_bytes)
        						.sendToTarget();
        			mClientHandler = null;
        			mExpectedBytes = 0;
        			mExpectedMsg = -1;
        		}
    		}
    	}
    };
    
    public boolean connect() {
    	if ("".equals(mDeviceAddress)) return false;
        try {
            return initialise(mDeviceAddress);
        } catch (Exception e) {
            Log.d(mDeviceAddress, "Exception occurred while trying to connect : " + e.getMessage());
            return false;
        }
    }

    
    public byte[] blockingRead(int num) {
        while (buffer.size() < num) {;
        }
        byte[] data_bytes = new byte[num];
        for (int i = 0; i < num; i++) {
            Byte first = buffer.remove(0);
            data_bytes[i] = first;
        }
        return data_bytes;
    }

    /* Non blocking read */
    
    public byte[] read(int num) {
        ArrayList<Byte> data = new ArrayList<Byte>();
        for (int i = 0; i < num && buffer.size() > 0; i++) {
            Byte first = buffer.remove(0);
            data.add(first);
        }
        byte[] data_bytes = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            data_bytes[i] = data.get(i);
        }
        return data_bytes;
    }

    
    public boolean send(byte[] data) {
        BluetoothSend(data);
        return true;
    }

    
    public void disconnect() {
        readThread.cancel();
        free_channel();
    }

    
    public boolean sendCommand(byte value) {
        byte[] data = new byte[1];
        data[0] = value;
        return send(data);
    }
    
    public String getMACAddress(){
    	return mBluetoothDevice.getAddress();
    }
    
    /** BT related objects. */
    private BluetoothSocket mBluetoothSocket = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private BluetoothDevice mBluetoothDevice = null;
    static ConnectedThread readThread = null;

    /** Class for all Bluetooth related functions.
     *  Task: (1)Acquire a BT socket and connect over that socket.
     *        (2)Establish input and output streams over the socket for data transfer
     *  Arguments: Null
     *  Return: True is initialisation was successful, else False. 
     * @throws Exception
     */
    public boolean initialise(String add_string) throws Exception {
        /** Get a handle to the BT hardware. */
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            /** Link the target BT address to be connected. */
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(add_string);
            mDeviceAddress = add_string;
        } catch (IllegalArgumentException e) {
            /** Exception is thrown if BT address is not valid. Then return false*/
            return false;
        }

        buffer = new ArrayList<Byte>();
        Method m;
        m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
        mBluetoothSocket = (BluetoothSocket) m.invoke(mBluetoothDevice, Integer.valueOf(1));
        Log.d(TAG, "Connecting...");

        try {
            /** This is a blocking call and will only return on a successful connection or an exception. */
            mBluetoothSocket.connect();
        } catch (IOException e) {
            /** If target BT device not found or connection refused then return false. */
            try {
                mBluetoothSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            Log.e(TAG, "returning false");
            return false;
        }

        Log.d(TAG, "Connected");
        /** Get input and output stream handles for data transfer. */
        mInputStream = mBluetoothSocket.getInputStream();
        mOutputStream = mBluetoothSocket.getOutputStream();
        
        mOutputStream.write((byte)127);
        mOutputStream.write((byte)127);
        mOutputStream.write((byte)127);
        
        mClientHandler = null;
        mExpectedBytes = 0;
        mExpectedMsg = -1;

        readThread = new ConnectedThread(mBluetoothSocket);
        readThread.start();

        return true;
    }

    /** Function to send data over BT.
     * Task: (1)To send the byte array over Bluetooth Channel.
     * Arguments: An array of bytes to be sent.
     * Return: Null
     */
    public void BluetoothSend(byte[] write_buffer) {
        try {
        	mOutputStream.write(write_buffer);
    		for (int i=0; i<write_buffer.length; i++) Log.d(TAG, "Writing data: " + write_buffer[i]);
    		
        } catch (IOException e) {
            Log.e(TAG, "Writing on command error");
        }
        Log.d(TAG, "Writing on command successful");
    }

    /** Function to close BT connection.
     * Task: (1)Close input and output streams
     * 		 (2)Close Bluetooth socket.
     * Arguments: Null
     * Return: Null
     */
    public void free_channel() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
            Log.d(TAG, "BT Channel free");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static ArrayList<Byte> buffer = null;

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread ");
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    for (int i = 0; i < bytes; i++){
                    	Log.d(TAG, "Read data: " + (int)buffer[i]);
                    }
                    
                    //transferToModuleBuffer(buffer, bytes);
                    mHandler.obtainMessage(BluetoothCommunicationModule.MESSAGE_READ, bytes, -1, buffer)
                    		.sendToTarget();
                    
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    // Start the service over to restart listening mode
                    /*BluetoothCommunicationModule.readThread = null;
                    BluetoothCommunicationModule.readThread = new ConnectedThread(mmSocket);
                    BluetoothCommunicationModule.readThread.start();*/
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}