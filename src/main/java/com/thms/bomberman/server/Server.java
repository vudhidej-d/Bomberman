package com.thms.bomberman.server;

import com.thms.bomberman.client.DataMessage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private int port;
    private Thread listenThread;
    private boolean listening = false;
    private DatagramSocket socket;

    private final int MAX_PACKET_SIZE = 1024;
    private byte[] receivedDataBuffer = new byte[MAX_PACKET_SIZE*10];
    private byte[] sendedDataBuffer = new byte[MAX_PACKET_SIZE*10];

    private Set<ServerClient> clients = new HashSet<ServerClient>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Started server on port 13000...");

        listening = true;

        listenThread = new Thread(() -> listen(), "BombermanServer-ListenThread");
        listenThread.start();
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
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        System.out.println("-----------------------------");
        System.out.println("IP: "+address.getHostAddress());
        System.out.println("Port: "+port);

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

        if (receivedObj instanceof String) {
            String s = (String) receivedObj;
            System.out.println(s);
        }

        if (receivedObj instanceof DataMessage) {
            DataMessage data = (DataMessage) receivedObj;
            System.out.println("Contents: "+data);
            System.out.println("-----------------------------");
            clients.add(new ServerClient(address, port));
            for (ServerClient client : clients) {
                System.out.println(client);
            }
        }
    }

    public void send(Object obj, InetAddress address, int port) {
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
        DatagramPacket packet = new DatagramPacket(sendedDataBuffer, sendedDataBuffer.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
