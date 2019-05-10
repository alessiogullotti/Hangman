package net;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket mySocket = null;
    int porta=6789;
    boolean connected;
    DataInputStream in;
    DataOutputStream out;
    BufferedReader keyboard;
    static int i=0;
    public boolean communicate(){
        try{
            //String message=keyboard.readLine();
            String received=in.readLine();
            if(received.equals("CLOSED")){
                while(!(received=in.readLine()).equals("End")){
                    System.out.printf(received + '\n');
                }
                keyboard = new BufferedReader(new InputStreamReader(System.in));
                String message = keyboard.readLine();
                out.writeBytes(message + "\n");
                if (message.toUpperCase().equals("NO"))
                        connected=false;
                return  connected;
            }
            else {
                while (!(received = in.readLine()).equals("End")) {
                    System.out.printf(received + '\n');
                }
                System.out.println("Inserisci:");
                keyboard = new BufferedReader(new InputStreamReader(System.in));
                String message = keyboard.readLine();
                out.writeBytes(message + "\n");
                return connected;
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
            connected=false;
        }
        return connected;
    }

    public Socket connect(){
        try{
            System.out.println("[0]-Inizializzo il client....");
            Socket mySocket=new Socket(InetAddress.getLocalHost(),porta);
            System.out.println("[1]-Bonjour, Finesse");
            connected=true;
            out=new DataOutputStream(mySocket.getOutputStream());
            in=new DataInputStream(mySocket.getInputStream());
        }
        catch(UnknownHostException e){
            System.err.println("Host sconosciuto");
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        return mySocket;

    }
    public static void main(String argv[]){
        Client c=new Client();
        c.connect();
        while(c.communicate()){}
    }
}