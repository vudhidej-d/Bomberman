package com.thms.bomberman.messages;

import com.thms.bomberman.client.BombermanType;

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
    }

    public ServerMessagePhrase getHeader() {
        return header;
    }

    public void setHeader(ServerMessagePhrase header) {
        this.header = header;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public BombermanType getPacketOwner() {
        return packetOwner;
    }

    public void setPacketOwner(BombermanType packetOwner) {
        this.packetOwner = packetOwner;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
