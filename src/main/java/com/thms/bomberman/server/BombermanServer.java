package com.thms.bomberman.server;

public class BombermanServer {

    public static void main(String[] args) {
        Server server = new Server(21488);
        server.start();
    }

}
