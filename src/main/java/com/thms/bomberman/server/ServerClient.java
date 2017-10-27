package com.thms.bomberman.server;

import java.net.InetAddress;

public class ServerClient {
    public static int userIDCounter = 1;

    public int userID;
    public InetAddress address;
    public int port;
    public boolean status = false;

    public ServerClient(InetAddress address, int port) {
        userID = userIDCounter;
        this.address = address;
        this.port = port;
        status = true;
        userIDCounter++;
    }

//    @Override
//    public int hashCode() {
//        return userID;
//    }

    @Override
    public String toString() {
        return Integer.toString(userID);
    }
}
