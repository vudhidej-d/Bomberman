package com.thms.bomberman.client;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private ClientMessageType header;
    private BombermanType packetOwner;
    private String data;

    public ClientMessage(ClientMessageType header, String data) {
        this.header = header;
        this.data = data;
    }

    public ClientMessage(ClientMessageType header, BombermanType packetOwner, String data) {
        this.header = header;
        this.packetOwner = packetOwner;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request: "+header+"\nOwner: "+packetOwner+" -> "+data;
    }

    public ClientMessageType getHeader() {
        return header;
    }

    public void setHeader(ClientMessageType header) {
        this.header = header;
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
