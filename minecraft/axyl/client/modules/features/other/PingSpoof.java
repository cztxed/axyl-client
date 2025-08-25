package axyl.client.modules.features.other;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue; 
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.events.render.EventRender2D;
import axyl.client.events.render.EventRender3D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;

public class PingSpoof extends Module
{
    public static final List<C0FPacketConfirmTransaction> c0fs = new ArrayList();
    public static final List<C00PacketKeepAlive> c00s = new ArrayList();
    
    public final Queue<Long> c0fsList = new LinkedList<>();
    public int additionalDelay;
    public int s08Fix;

	public Setting ping;
	private Setting sendingEvent;
	private Setting additionalRandomization;
	private Setting randomizationLevel;

	public PingSpoof() {
		super("PingSpoof", "Increses ping", Keyboard.KEY_NONE, Category.Other);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		options.add("Update");
		options.add("Packet-IN");
		options.add("Packet-OUT");
		options.add("Event2D");
		options.add("Event3D");
		
		ArrayList<String> options2 = new ArrayList<>();
		options2.add("None");
		options2.add("Low");
		options2.add("Medium");
		options2.add("High");
		
		Axyl.ins.settingManager.createSetting(sendingEvent = new Setting("Sending event", this, "Packet-IN", options));
		Axyl.ins.settingManager.createSetting(randomizationLevel = new Setting("Randomization Level", this, "None", options2));
		Axyl.ins.settingManager.createSetting(ping = new Setting("Ping", this, 3000, 10, 10000, 0, false));
		//Axyl.ins.settingManager.createSetting(additionalRandomization = new Setting("Additional Randomization", this, false, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventRender2D(EventRender2D event) {
		String mode = sendingEvent.getValString();
		if(mode.equalsIgnoreCase("Event2D")) {
			sendPackets();
		}
	}
	
	@Subscribe
	public void eventRender3D(EventRender3D event) {
		String mode = sendingEvent.getValString();
		if(mode.equalsIgnoreCase("Event3D")) {
			sendPackets();
		}
	}

	@Subscribe
	public void eventPreUpdate(EventPlayerPreUpdate event) {
		String mode = sendingEvent.getValString();
		if(mode.equalsIgnoreCase("Update")) {
			sendPackets();
		}
	}
	
	@Subscribe
	public void packetEvent(EventPacket event) {
		String mode = sendingEvent.getValString();
		if(event.getPacketDirection().equals(PacketDir.IN)) {
			if(mode.equalsIgnoreCase("Packet-IN")) {
				sendPackets();
			}
		} else {
			if(mode.equalsIgnoreCase("Packet-OUT")) {
				sendPackets();
			}
		}
    	if(event.getPacket() instanceof C0FPacketConfirmTransaction) {
    		C0FPacketConfirmTransaction p = (C0FPacketConfirmTransaction)event.getPacket();
    		c0fsList.add(System.currentTimeMillis() + 1000L);
      		c0fs.add(p);
    		p.timer.reset();
    		event.setCancelled(true);
    	}
    	if(event.getPacket() instanceof C00PacketKeepAlive) {
    		C00PacketKeepAlive p = (C00PacketKeepAlive)event.getPacket();
    		p.timer.reset();
    		c00s.add(p);
    		event.setCancelled(true);
    	}
 	}
	
	public void sendPackets() {
		String mode = randomizationLevel.getValString();
		if(mc.thePlayer.ticksExisted % 30 == 0) {
			if(!c0fsList.isEmpty()) {
				additionalDelay = (int) (c0fsList.size()*1);
			} else {
				additionalDelay = 0;
			}
			if(mode.equalsIgnoreCase("None")) {
				additionalDelay = (int)0;
			}
			if(mode.equalsIgnoreCase("Low")) {
				additionalDelay *= (int)RandomUtils.nextDouble(0.8, 1.2);
			}
			if(mode.equalsIgnoreCase("Medium")) {
				additionalDelay *= (int)RandomUtils.nextDouble(0.45, 1.55);
			}
			if(mode.equalsIgnoreCase("High")) {
				additionalDelay *= (int)RandomUtils.nextDouble(0, 2.5);
			}
		}
		int delay = (int)ping.getValDouble() + additionalDelay;
		if(!c0fs.isEmpty())
		for(C0FPacketConfirmTransaction packet : c0fs) {
			if(packet.timer.hasReached(delay)) {
				PacketUtil.sendPacketNoEvent(packet);
				c0fs.remove(packet);
			}
		}
		if(!c00s.isEmpty())
		for(C00PacketKeepAlive packet : c00s) {
			if(packet.timer.hasReached(delay)) {
				PacketUtil.sendPacketNoEvent(packet);
				c00s.remove(packet);
			}
		} 
	}
    
	@Override
	public void onEnable() {
		additionalDelay = 0;
		c0fsList.clear();
    	super.onEnable();
	}
}
