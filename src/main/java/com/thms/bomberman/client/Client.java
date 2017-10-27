package com.thms.bomberman.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class Client {
    private String ipAddress;
    private int port;
    private Error errorCode = Error.NONE;

    private InetAddress serverAddress;

    private DatagramSocket socket;

    private final int MAX_PACKET_SIZE = 1024;
    private byte[] sendedDataBuffer = new byte[MAX_PACKET_SIZE*10];

    public enum Error{
        NONE, INVALID_HOST, SOCKET_EXCEPTION;
    }

    /**
     *
     * @param host
     *          Eg. 192.168.1.1:5000
     *
     */
    public Client(String host) {
        String[] parts = host.split(" ");
        if (parts.length != 2) {
            errorCode = Error.INVALID_HOST;
            return;
        }

        ipAddress = parts[0];

        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            errorCode = Error.INVALID_HOST;
            return;
        }

    }

    /**
     *
     * @param host
     *          Eg. 192.168.1.1
     * @param port
     *          Eg. 5000
     */
    public Client(String host, int port) {
        this.ipAddress = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            serverAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            errorCode = Error.INVALID_HOST;
            return false;
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            errorCode = Error.SOCKET_EXCEPTION;
            return  false;
        }
        sendConnectionPacket();
        // W8 4 server to reply
        return true;
    }

    private void sendConnectionPacket() {
//        String data = "Connection Packet";
        DataMessage data = new DataMessage("ConnectionPacket");
        send(data);
    }

    public void send(Object obj) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        ObjectOutputStream objOS = null;
        try {
            objOS = new ObjectOutputStream(byteOS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            objOS.writeObject(obj);
            objOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendedDataBuffer = byteOS.toByteArray();

        assert(socket.isConnected());
        DatagramPacket packet = new DatagramPacket(sendedDataBuffer, sendedDataBuffer.length, serverAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Error getErrorCode() {
        return errorCode;
    }
}
