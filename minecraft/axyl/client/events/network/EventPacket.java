package axyl.client.events.network;

import axyl.client.events.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class EventPacket extends Event {

    public final PacketDir packetDirection;
    public Packet packet;
    public boolean shouldCancel;
    public INetHandler handler;

    public EventPacket(PacketDir packetDirection, Packet packet, boolean shouldCancel) {
        this.packetDirection = packetDirection;
        this.packet = packet;
        this.shouldCancel = shouldCancel;
    }
    
    public EventPacket(PacketDir packetDirection, Packet packet, INetHandler packetListener) {
        this.packetDirection = packetDirection;
        this.packet = packet;
        this.handler = packetListener;
    }

	public INetHandler getHandler() {
		return handler;
	}

	public Packet getPacket() {
		if(packet instanceof C09PacketHeldItemChange) {
			C09PacketHeldItemChange p = (C09PacketHeldItemChange)packet;
			Minecraft.getMinecraft().thePlayer.c09CurrentSlot = p.getSlotId();
		}
        return packet;
    }
    
    public PacketDir getPacketDirection() {
        return packetDirection;
    }

	public boolean shouldCancelPackets() {
		return shouldCancel;
	}
}
