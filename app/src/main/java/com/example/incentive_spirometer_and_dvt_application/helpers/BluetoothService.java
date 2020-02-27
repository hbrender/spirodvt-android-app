package com.example.incentive_spirometer_and_dvt_application.helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService {
    private static final String APP_NAME = "SpiroDVT";
    private static  UUID androidUUID;
    private static final String TAG = "BluetoothTest";

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private BluetoothDevice mmDevice;

    public BluetoothService(Context context, UUID uuid){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        androidUUID = uuid;
        start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, androidUUID);
                Log.d(TAG, "AcceptThread: socket's listen method succeeded");
            }
            catch(IOException e){
                Log.d(TAG, "Socket's listen() method failed");
            }
            mmServerSocket = tmp;
            Log.d(TAG, "AcceptThread: " + mmServerSocket);
        }

        public void run(){
            Log.d(TAG, "run: accept thread running!");

            BluetoothSocket socket;
            while(true){
                try{
                    socket = mmServerSocket.accept();
                    Log.d(TAG, "run: socket's accept method succeeded");
                }
                catch(IOException e){
                    Log.d(TAG, "Socket's accept() method failed");
                    break;
                }

                if(socket != null){
                    Log.d(TAG, "run: Starting managing connected socket");
                    manageMyConnectedSocket(socket, mmDevice);
                    cancel();
                    break;
                }
            }
            Log.d(TAG, "run: end accept thread");
        }

        public void cancel(){
            try{
                mmServerSocket.close();
            }
            catch(IOException e){
                Log.e(TAG, "failed to close the connect socket in run", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            mmDevice = device;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.d(TAG, "run: RUNNING CONNECTED THREAD!");

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(androidUUID);
                Log.d(TAG, "ConnectThread: socket's create method succeeded");
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            Log.d(TAG, "run: bt socket - " + mmSocket);
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.d(TAG, "run: connected!");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.d(TAG, "run: unable to connect!");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket, mmDevice);
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
     * Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start(){
        Log.d(TAG, "start: ");
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     AcceptThread starts and sits waiting for a connection.
     Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/
    public void startClient(BluetoothDevice device){
        Log.d(TAG, "startClient: connecting to the device please wait");
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

    }

    /**
     Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     receiving incoming data through input/output streams respectively.
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                Log.d(TAG, "ConnectedThread: input stream created");
            }
            catch(IOException e){
                Log.d(TAG, "ConnectedThread: error occurred when creating input stream");
            }
            try{
                tmpOut = socket.getOutputStream();
                Log.d(TAG, "ConnectedThread: output stream created");
            }
            catch(IOException e){
                Log.d(TAG, "ConnectedThread: error occurred when creating output stream");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] mmBuffer = new byte[1024];
            int numBytes;

            while(true){
                try{
                    numBytes = mmInStream.read(mmBuffer);
                    String incomingMessage = new String(mmBuffer, 0, numBytes);
                    Log.d(TAG, "run: " + incomingMessage);
                }
                catch(IOException e){
                    Log.d(TAG, "run: input stream was disconnected");
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            try{
                mmOutStream.write(bytes);
            }
            catch(IOException e){
                Log.d(TAG, "write: error writing to output stream");
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket mmSocket, BluetoothDevice mmDevice){
        Log.d(TAG, "manageMyConnectedSocket: starting.");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out){
        mConnectedThread.write(out);
    }


}
