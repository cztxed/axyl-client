package axyl.client.modules.features.other;

import java.util.ArrayList;  
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

public class SecurityFeatures extends Module {
    
	public boolean blockPacket;
	public static Setting spooferMode;
	private Setting coffeeProtAntiCrash;
	private Setting antiParticleCrash;
	private Setting antiS49;
	private Setting debug;

	public SecurityFeatures() {
		super("SecurityFeatures", "", Keyboard.KEY_NONE, Category.Other);
	}
	
	@Override
	public void moduleSetup() {
        Axyl.ins.settingManager.createSetting(coffeeProtAntiCrash = new Setting("Coffee Protect Anti Crash", this, true, false));
        Axyl.ins.settingManager.createSetting(antiParticleCrash = new Setting("Particle limiter", this, true, false));
        Axyl.ins.settingManager.createSetting(antiS49 = new Setting("Prevent S48 debugging", this, true, false));
        Axyl.ins.settingManager.createSetting(debug = new Setting("Debug messaages", this, true, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void updatePacket(EventPacket event) {
		if(mc.theWorld == null)
			return;
		
		if(antiS49.getValBoolean()) {
			if(event.getPacket() instanceof S48PacketResourcePackSend) {
				S48PacketResourcePackSend p = (S48PacketResourcePackSend)event.getPacket();
				if(debug.getValBoolean())
					Axyl.sendMessage("§cBlocked Resourcepack Packet / URL: " + p.getURL());
				event.setCancelled(true);
			}
		}
		if(antiParticleCrash.getValBoolean()) {
			if(event.getPacket() instanceof S2APacketParticles) {
				S2APacketParticles p = (S2APacketParticles)event.getPacket();
				if(p.getParticleCount() > 1000) {
					if(debug.getValBoolean())
						Axyl.sendMessage("§cBlocked " + p.getParticleCount() + " Packets");
					event.setCancelled(true);
				}
			}
		}
		if(coffeeProtAntiCrash.getValBoolean()) {
			if(event.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion p = (S27PacketExplosion)event.getPacket();
				if(p.getX() == 3.4028234346940236E35
				|| p.getY() == 3.4028234E35
				|| p.getX() == p.getZ()
				|| p.getY() == p.getZ()
				|| p.getX() == p.getY()) {
					blockPacket = true;
					if(debug.getValBoolean())
						Axyl.sendMessage("§cPrevented CoffeProtect Crash attempt");
					event.setCancelled(true);
				}
			}
			if(event.getPacket() instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook p = (S08PacketPlayerPosLook)event.getPacket();
				if(blockPacket) {
					event.setCancelled(true);
					blockPacket = false;
				}
			}
		}
	}
	
	@Override
	public void onEnable() {
		this.blockPacket = false;
		super.onEnable();
	}
}
