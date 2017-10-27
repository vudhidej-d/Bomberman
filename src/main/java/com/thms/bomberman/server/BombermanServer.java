package com.thms.bomberman.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BombermanServer {

    public static void main(String[] args) {
        Server server = new Server(13000);
        server.start();

//        InetAddress address = null;
//        try {
//            address = InetAddress.getByName("localhost");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        int port = 8192;
//        server.send(new byte[] {0, 1, 2}, address, port);
    }

}
