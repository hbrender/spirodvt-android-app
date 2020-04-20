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
import com.example.incentive_spirometer_and_dvt_application.fragments.DvtFragment;
import com.example.incentive_spirometer_and_dvt_application.fragments.SpirometerFragment;

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

    // ServerClass inner class is not used so dont worry about it.
    // it is there in case we ever need it
    private ServerClass serverClass;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    // not needed yet
    private static final String APP_NAME = "SpiroDVT";
    private static final UUID APP_UUID = UUID.fromString("e92a03ac-3bc5-4102-8b27-ea643ece6210");


    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;

    /**
     * Constructor for our bluetooth thread class
     * @param handler a handler which allows us to send messages back to the Connect Device activity from the running threads
     */
    public BluetoothThread(Handler handler){
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // again this inner class is not used anywhere. It is only needed if we wanted the phone to be the bluetooth server and the hardware to be the client
    // nice to keep just in case
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

    /**
     * a function to start a new connect thread with a given device
     * @param device the device to start a connection with
     */
    public void startConnectThread(BluetoothDevice device, boolean[] spiroOrDvt){
      connectThread = new ConnectThread(device, spiroOrDvt);
      connectThread.start();
    }

    /**
     * function which ends a connect thread
     */
    public void endConnectThread(){
        connectThread.cancel();
    }

    /**
     * Connect Thread Class
     * This class initiates the inital connection and creates an open bluetooth socket connection with the device
     */
    public class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private boolean[] spiroOrDvt;

        public ConnectThread(BluetoothDevice device, boolean[] flags) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            ParcelUuid[] uuids = mmDevice.getUuids();
            spiroOrDvt = flags;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                Log.d(TAG, "ConnectThread: " + uuids[0].getUuid().toString());
                tmp = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        /**
         * this is the running part of the thread
         */
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                Message message1 = Message.obtain();
                message1.what  = STATE_CONNECTING;
                handler.sendMessage(message1);
                // connects to the remote device through the socket
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

            if(spiroOrDvt == null){
                // The connection attempt succeeded so we call the connect device activity's function manageConnectedSocket to start the connected thread
                ConnectDevice.manageConnectedSocket(mmSocket);
            }
            else if(spiroOrDvt[0]) {
                Log.d(TAG, "run: get spiro data is true in connect thread");
                SpirometerFragment.manageConnectedSocket(mmSocket);
            }
            else if(spiroOrDvt[1]){
                Log.d(TAG, "run: get dvt data is true in connect thread");
                DvtFragment.manageConnectedSocket(mmSocket);
            }
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

    /**
     * starts a connected thread given the socket from the connect thread
     * @param socket the bluetooth socket with a connection to the device
     */
    public void startConnectedThread(BluetoothSocket socket){
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    /**
     *  ends the connected thread
     */
    public void endConnectedThread(){
        connectedThread.cancel();
    }

    /**
     * public function to send a message using the connected thread
     * @param bytes the data to be sent (in byte[] format)
     */
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

            // since the opening of the input and output streams was a success, in connect device the callback is handled and a prompt is sent to the server
            Message message5 = Message.obtain();
            message5.what  = STATE_CONNECTED_THREAD_SUCCESS;
            handler.sendMessage(message5);
        }

        /**
         * writes to the connected socket using the output stream created
         * @param bytes
         */
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

        /**
         * this is the running part of the thread
         */
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
