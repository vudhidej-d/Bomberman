//5810401040 Vudhidej Dejmul

package com.thms.bomberman.messages;

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
        return "Request: "+header;
    }

    public ClientMessagePhrase getHeader() {
        return header;
    }

    public BombermanType getPacketOwner() {
        return packetOwner;
    }

    public String getData() {
        return data;
    }
}
