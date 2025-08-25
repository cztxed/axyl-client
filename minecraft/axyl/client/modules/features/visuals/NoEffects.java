package axyl.client.modules.features.visuals;

import net.minecraft.client.Minecraft;      
import java.util.Comparator;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S40PacketDisconnect;

import java.awt.Color;
import java.util.ArrayList;

public class NoEffects extends Module
{ 
    public NoEffects() {
        super("NoEffects", "" , 0, Category.Visuals);
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
        if (mc.thePlayer.isPotionActive(9)) {
            mc.thePlayer.removePotionEffectClient(9);
        }
        if (mc.thePlayer.isPotionActive(16)) {
            mc.thePlayer.removePotionEffectClient(16);
        }
        if (mc.thePlayer.isPotionActive(15)) {
            mc.thePlayer.removePotionEffectClient(15);
        }
    }
	@Subscribe
    public void eventPacket(EventPacket event) {
        if (event.getPacket() instanceof S1DPacketEntityEffect) {
            S1DPacketEntityEffect packet = (S1DPacketEntityEffect) event.getPacket();
            if (packet.getEffectId() == 9 | packet.getEffectId() == 16 || packet.getEffectId() == 15) {
                event.setCancelled(true);
            }
        }
	}
    @Override
    public void onEnable() {
    	super.onEnable();
    }
}
