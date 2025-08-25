package axyl.client.modules.features.other;

import java.util.ArrayList;  
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Blink extends Module
{
    public static ArrayList<Packet> packets = new ArrayList<>();

    public Blink() {
        super("Blink", "Suspends OUT-going packets", 0, Category.Other);
    }

    @Subscribe
    public void eventPacket(EventPacket event) {
        if(event.getPacketDirection().equals(PacketDir.OUT)) {
			if(!(event.getPacket() instanceof C0FPacketConfirmTransaction) || !(event.getPacket() instanceof C00PacketKeepAlive)) {
				packets.add(event.getPacket());
				event.setCancelled(true);
			}
        }
    }
    
    @Override
    public void onEnable() {
    	packets.clear();
    	super.onEnable();
    }
    @Override
    public void onDisable() {
        packets.forEach(PacketUtil::sendPacketNoEvent);
        packets.clear();
    	super.onDisable();
    }
}
