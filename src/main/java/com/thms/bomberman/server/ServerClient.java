package com.thms.bomberman.server;

import com.thms.bomberman.client.BombermanType;

import java.net.InetAddress;

public class ServerClient {
    public static int userIDCounter = 1;

    private int userID;
    private InetAddress address;
    private int port;
    private BombermanType clientOwner;
    private boolean status = false;

    public ServerClient(InetAddress address, int port, BombermanType clientOwner) {
//        userID = userIDCounter;
        this.address = address;
        this.port = port;
        this.clientOwner = clientOwner;
        status = true;
        userIDCounter++;
    }

    @Override
    public int hashCode() {
        return userID;
    }

    @Override
    public String toString() {
        return address.getHostAddress()+":"+port;
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

    public BombermanType getClientOwner() {
        return clientOwner;
    }

    public void setClientOwner(BombermanType clientOwner) {
        this.clientOwner = clientOwner;
    }
}
