package com.thms.bomberman.messages;

import com.thms.bomberman.client.BombermanType;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private ClientMessagePhrase header;
    private BombermanType packetOwner;
    private String data;

    public ClientMessage(ClientMessagePhrase header, String data) {
        this.header = header;
        this.data = data;
    }

    public ClientMessage(ClientMessagePhrase header, BombermanType packetOwner, String data) {
        this.header = header;
        this.packetOwner = packetOwner;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request: "+header+"\nOwner: "+packetOwner+" -> "+data;
    }

    public ClientMessagePhrase getHeader() {
        return header;
    }

    public void setHeader(ClientMessagePhrase header) {
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
