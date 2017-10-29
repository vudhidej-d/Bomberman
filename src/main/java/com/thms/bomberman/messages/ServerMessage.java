package com.thms.bomberman.messages;

import java.io.Serializable;

public class ServerMessage implements Serializable{
    private ServerMessagePhrase header;
    private int statusCode;
    private BombermanType packetOwner;
    private String data;

    public ServerMessage(ServerMessagePhrase header, BombermanType packetOwner, String data) {
        this.header = header;
        this.packetOwner = packetOwner;
        this.data = data;
        this.statusCode = getStatusCode(header);
    }

    public String toString() {
        return "Response: "+statusCode+" "+header;
    }

    public ServerMessagePhrase getHeader() {
        return header;
    }

    public BombermanType getPacketOwner() {
        return packetOwner;
    }

    public String getData() {
        return data;
    }

    public int getStatusCode(ServerMessagePhrase phrase) {
        int code = 0;
        switch (phrase) {
            case CONNECTED:
                code = 200;
                break;
            case DISCONNECTED:
                code = 100;
                break;
            case MOVED_RIGHT:
                code = 301;
                break;
            case MOVED_LEFT:
                code = 302;
                break;
            case MOVED_UP:
                code = 303;
                break;
            case MOVED_DOWN:
                code = 304;
                break;
            case POWER_UP_SPAWNED:
                code = 401;
                break;
            case POWERED_UP:
                code = 305;
                break;
            case PLACED_BOMB:
                code = 306;
                break;
            case PLAYER_SPAWNED:
                code = 402;
                break;
        }
        return code;
    }
}
