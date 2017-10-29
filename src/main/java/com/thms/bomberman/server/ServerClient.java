//5810401040 Vudhidej Dejmul

package com.thms.bomberman.server;

import com.thms.bomberman.messages.BombermanType;

import java.net.InetAddress;

public class ServerClient {
    public static int userIDCounter = 1;

    private int userID;
    private InetAddress address;
    private int port;
    private BombermanType clientOwner;
    private boolean status = false;

    public ServerClient(InetAddress address, int port, BombermanType clientOwner) {
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

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isStatus() {
        return status;
    }

    public BombermanType getClientOwner() {
        return clientOwner;
    }
}
