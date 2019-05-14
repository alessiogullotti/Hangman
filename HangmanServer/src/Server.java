package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    public static void main(String args[]) throws IOException {
        ServerSocket server = new ServerSocket(6789);
        while (true) {
            Socket socket = server.accept();
            System.out.println(socket.getInetAddress()+" connesso");
            ConnectedClient client = new ConnectedClient(socket);
            Thread thread = new Thread(client);
            thread.start();
        }
    }
}