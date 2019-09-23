package net.minecraft.network;

public abstract class PlayProtocol {
    public ConnectionState
            HANDSHAKING,
            PLAY,
            STATUS,
            LOGIN;
}
