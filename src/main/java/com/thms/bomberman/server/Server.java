package com.thms.bomberman.server;

import com.thms.bomberman.messages.ClientMessage;
import com.thms.bomberman.messages.ServerMessage;
import com.thms.bomberman.messages.ServerMessagePhrase;

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

    ServerMessage message;
    String lastAction;

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

        System.out.println("Started server on port "+port+"...");

        listening = true;

        listenThread = new Thread(() -> listen(), "BombermanServer-ListenThread");
        listenThread.start();
    }

    private void listen() {
        while (listening) {
            System.out.println("\nListening...");
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

        if (receivedObj instanceof ClientMessage) {
            ClientMessage receivedMessage = (ClientMessage) receivedObj;

            switch (receivedMessage.getHeader()) {
                case CONNECTING:
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    if (clients.size() <= 4) {
                        clients.add(new ServerClient(address, port, receivedMessage.getPacketOwner()));
                        System.out.println("==============================");
                        System.out.println("------------------------------");
                        System.out.println("########Client Connect########");
                        System.out.println("------------------------------");
                        for (ServerClient client : clients) {
                            System.out.println("-> "+client.getAddress().getHostAddress()+":"+client.getPort());
                        }
                        System.out.println("------------------------------");
                        System.out.println("Client connected: "+clients.size());
                        System.out.println("==============================");
                        message = new ServerMessage(ServerMessagePhrase.CONNECTING,
                                receivedMessage.getPacketOwner(),
                                receivedMessage.getData()+"/Server connected...");
                        lastAction = "Connected...";
                    }
                    break;

                case DISCONNECTING:
                    System.out.println("==============================");
                    System.out.println("------------------------------");
                    System.out.println("########Client Connect########");
                    System.out.println("------------------------------");
                    for (ServerClient client : clients) {
                         if (client.toString().equals(packet.getAddress().getHostAddress()+":"+packet.getPort())) {
                             clients.remove(client);
                         } else {
                             System.out.println("-> "+client.getAddress().getHostAddress()+":"+client.getPort());
                         }
                    }
                    System.out.println("------------------------------");
                    System.out.println("Client connected: "+clients.size());
                    System.out.println("==============================");
                    message = new ServerMessage(ServerMessagePhrase.CONNECTING,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData()+"/Server disconnected...");
                    lastAction = "Disconnected...";
                    break;

//                case PLAYER_SPAWN:
//                    String data = receivedMessage.getData();
//                    if (!clients.isEmpty()) {
//                        for (ServerClient client : clients) {
//                            data += "/"+client.getClientOwner().toString();
//                        }
//                    }
//                    message = new ServerMessage(ServerMessagePhrase.PLAYER_SPAWN,
//                            receivedMessage.getPacketOwner(),
//                            receivedMessage.getData()+"/"+clients.size());
//                    lastAction = receivedMessage.getPacketOwner()+" spawn...";
//                    break;

                case MOVE_RIGHT:
                    message = new ServerMessage(ServerMessagePhrase.MOVE_RIGHT,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" move right...";
                    break;

                case MOVE_LEFT:
                    message = new ServerMessage(ServerMessagePhrase.MOVE_LEFT,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" move left...";
                    break;

                case MOVE_UP:
                    message = new ServerMessage(ServerMessagePhrase.MOVE_UP,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" move up...";
                    break;

                case MOVE_DOWN:
                    message = new ServerMessage(ServerMessagePhrase.MOVE_DOWN,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" move down...";
                    break;

                case PLACE_BOMB:
                    message = new ServerMessage(ServerMessagePhrase.PLACE_BOMB,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" place bomb...";
                    break;

                case POWERUP_SPAWN:
                    message = new ServerMessage(ServerMessagePhrase.POWERUP_SPAWN,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    break;

                case POWERUP:
                    message = new ServerMessage(ServerMessagePhrase.POWERUP,
                            receivedMessage.getPacketOwner(),
                            receivedMessage.getData());
                    lastAction = receivedMessage.getPacketOwner()+" power up...";
            }
            responseClients(message, lastAction);
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

    public void responseClients(ServerMessage message, String lastAction) {
        for (ServerClient client : clients) {
            send(message, client.getAddress(), client.getPort());
            System.out.println(client.getAddress().getHostAddress()+":"+ client.getPort() +" -> "+lastAction);
        }
    }
}
