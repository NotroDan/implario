package vanilla.packet;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class CKekPacket implements Packet {
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        System.out.println("Im alive on read!");
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        System.out.println("Im alive on write!");
    }

    @Override
    public void processPacket(INetHandler handler) {
        System.out.println("Im alive on process?");
    }
}
