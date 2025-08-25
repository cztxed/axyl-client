package axyl.client.events.network;

import axyl.client.events.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class EventPacketType extends Event {

    public final PacketDir packetDirection;
    public final EventType type;
    public Packet packet;
    public boolean shouldCancel;
    public INetHandler handler;

    public EventPacketType(PacketDir packetDirection, EventType type, Packet packet, boolean shouldCancel) {
        this.packetDirection = packetDirection;
        this.type = type;
        this.packet = packet;
        this.shouldCancel = shouldCancel;
    }
    
    public EventPacketType(PacketDir packetDirection, EventType type, Packet packet, INetHandler packetListener) {
        this.packetDirection = packetDirection;
        this.type = type;
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
	
	public EventType getType() {
		return type;
	}
}
