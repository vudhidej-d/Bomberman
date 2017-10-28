package com.thms.bomberman.server;

public class BombermanServer {
    public static void main(String[] args) {
        Server server = new Server(13000);
        server.start();
    }
}
