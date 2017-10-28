package com.thms.bomberman.server;

import java.net.InetAddress;

public class ServerClient {
    public static int userIDCounter = 1;

    private int userID;
    private InetAddress address;
    private int port;
    private boolean status = false;

    public ServerClient(InetAddress address, int port) {
//        userID = userIDCounter;
        this.address = address;
        this.port = port;
        status = true;
        userIDCounter++;
    }

    @Override
    public int hashCode() {
        return userID;
    }

    @Override
    public String toString() {
        return Integer.toString(userID);
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
