import java.io.*;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
public class SimpleBTServer {

    private static final String path = "src/sampledata.csv";
    //start server
    private void startServer() throws IOException {

        //Create a UUID for SPP
        javax.bluetooth.UUID uuid = new UUID("1101", true);
        System.out.println("UUID: " + uuid.toString());

        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection = streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: " + dev.getBluetoothAddress());
        System.out.println("Remote device name: " + dev.getFriendlyName(true));

        // read string from spp client
        InputStream inStream = connection.openInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
        System.out.println("we are about to read the input");
        String tempMessage = bReader.readLine();
        System.out.println(tempMessage);

        //send response to spp client
        OutputStream outStream=connection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        // it makes no difference between spiroid and dvtid, i added it to make sure the logs were being output from the right places
        // easier to leave it since no harm done and it's not too verbose
        if (tempMessage.equals("spiroid")){
            pWriter.write(uuid.toString() + "\r\n");
        }
        else if(tempMessage.equals("dvtid")){
            pWriter.write(uuid.toString() + "\r\n");
        }
        else if(tempMessage.equals("data")){
            String data = readFile();
            if(data != null){
                pWriter.write(data + "\r\n");
                System.out.println(data + "\r\n");
            }
            else{
                pWriter.write("nosession\r\n");
                System.out.println("no session");
            }
        }

        pWriter.flush();
        pWriter.close();
        streamConnNotifier.close();
    }

    public static String readFile(){
        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        String line = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append(" ");
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File was not found");
            e.printStackTrace();
            return null;
        }
        catch(IOException e){
            System.out.println("Could not read the file");
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {

        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());
        SimpleBTServer server = new SimpleBTServer();
        while(true){
            sampleBTServer.startServer();
        }
    }
}
