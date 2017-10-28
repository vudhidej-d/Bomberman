package com.thms.bomberman.client;

import com.thms.bomberman.server.ServerMessage;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private String ipAddress;
    private int port;
    private InetAddress serverAddress;

    private DatagramSocket socket;
    private Thread listenThread;
    private boolean listening;

    private final int MAX_PACKET_SIZE = 1024;
    private byte[] sendedDataBuffer = new byte[MAX_PACKET_SIZE*10];
    private byte[] receivedDataBuffer = new byte[MAX_PACKET_SIZE*10];

    public Queue<ServerMessage> updateQueue = new ConcurrentLinkedQueue<>();

    public Client(String host, int port) {
        this.ipAddress = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            serverAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            return  false;
        }

        sendConnectionPacket();

        listening = true;
        listenThread = new Thread(() -> listen(), "BombermanClient-ListenThread");
        listenThread.start();
        return true;
    }

    public void disconnect() {

    }

    private void listen() {
        while (listening) {
            System.out.println("Listening...");
            DatagramPacket packet = new DatagramPacket(receivedDataBuffer, MAX_PACKET_SIZE);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            process(packet);
        }
    }

    private void process(DatagramPacket packet) {
        ByteArrayInputStream byteIS = new ByteArrayInputStream(packet.getData());
        Object receivedObj = null;

        try {
            ObjectInputStream objIS = new ObjectInputStream(byteIS);
            try {
                receivedObj = objIS.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (receivedObj instanceof ServerMessage) {
            ServerMessage message = (ServerMessage) receivedObj;
            updateQueue.add(message);
        }
    }

    private void sendConnectionPacket() {
        ClientMessage data = new ClientMessage(ClientMessageType.CONNECTING, "ConnectionPacket");
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
}
