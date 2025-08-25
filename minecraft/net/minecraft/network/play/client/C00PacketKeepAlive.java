package net.minecraft.network.play.client;

import java.io.IOException;

import axyl.client.util.time.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive implements Packet<INetHandlerPlayServer>
{
    private int key;
	public Timer timer;

    public C00PacketKeepAlive()
    {
    }

    public C00PacketKeepAlive(int key)
    {
        this.key = key;
        this.timer = new Timer();
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processKeepAlive(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.key = buf.readVarIntFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.key);
    }

    public int getKey()
    {
        return this.key;
    }
}
