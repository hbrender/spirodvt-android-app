package com.example.incentive_spirometer_and_dvt_application.helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.incentive_spirometer_and_dvt_application.activities.ConnectDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothThread {
    static final String TAG = "BluetoothThreadTag";
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_CONNECTED_THREAD_SUCCESS = 5;
    static final int STATE_CONNECTED_THREAD_FAILED = 6;
    static final int STATE_MESSAGE_RECEIVED = 7;

    private ServerClass serverClass;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    // not needed yet
    private static final String APP_NAME = "SpiroDVT";
    private static final UUID APP_UUID = UUID.fromString("e92a03ac-3bc5-4102-8b27-ea643ece6210");


    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothThread(Handler handler){
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;

        public ServerClass(){

            try{
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
                Log.d(TAG, "ServerClass: " + serverSocket.toString());
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;

            while(socket == null){
                try{
                    socket = serverSocket.accept();
                    Log.d(TAG, "run: ACCEPT THREAD OPEN");
                }
                catch(IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what  = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                    Log.d(TAG, "run: ACCEPT THREAD FAILED");
                }

                if(socket != null){
                    ConnectDevice.manageConnectedSocket(socket);
                    break;
                }
            }
        }
    }

    public void startConnectThread(BluetoothDevice device){
      connectThread = new ConnectThread(device);
      connectThread.start();
    }
    public void endConnectThread(){
        connectThread.cancel();
    }

    public class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            ParcelUuid[] uuids = mmDevice.getUuids();


            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                Log.d(TAG, "ConnectThread: " + uuids[0].getUuid().toString());
                tmp = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                Message message1 = Message.obtain();
                message1.what  = STATE_CONNECTING;
                handler.sendMessage(message1);
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.d(TAG, "run: connection established and data link opened");

                Message message2 = Message.obtain();
                message2.what  = STATE_CONNECTED;
                handler.sendMessage(message2);
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Message message3 = Message.obtain();
                message3.what  = STATE_CONNECTION_FAILED;
                handler.sendMessage(message3);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            ConnectDevice.manageConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public void startConnectedThread(BluetoothSocket socket){
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

    }
    public void endConnectedThread(){
        connectedThread.cancel();
    }
    public void sendMessage(byte[] bytes){ connectedThread.write(bytes); }

    public class ConnectedThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;


        public ConnectedThread(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;


            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            }
            catch (IOException e){
                e.printStackTrace();
                Message message4 = Message.obtain();
                message4.what  = STATE_CONNECTED_THREAD_FAILED;
                handler.sendMessage(message4);
            }

            inputStream = tempIn;
            outputStream = tempOut;

            Message message5 = Message.obtain();
            message5.what  = STATE_CONNECTED_THREAD_SUCCESS;
            handler.sendMessage(message5);
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: " + text);
            try{
                outputStream.write(bytes);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                    break;
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
