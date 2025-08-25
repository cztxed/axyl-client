package axyl.client.modules.features.player;

import java.util.ArrayList;   

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;

public class AntiVoid extends Module {

	public ArrayList<Packet<?>> packets = new ArrayList<>();
	public boolean shouldSendPackets;
	public int voidTicks;
	public double lastx, lasty, lastz;
	public BlockPos lastPos;
	private Setting antiVoidMode;
	private Setting fallDist;
	
	public AntiVoid() {
		super("AntiVoid", "", Keyboard.KEY_NONE, Category.Player);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Last Position");
		options.add("Creative");
		options.add("Blink");
		options.add("Motion");
		options.add("Flag");
		options.add("Clip");

		Axyl.ins.settingManager.createSetting(this.antiVoidMode = new Setting("Mode", this, "Vanilla", options));
		Axyl.ins.settingManager.createSetting(this.fallDist = new Setting("Distance", this, 3.5, 2, 10, 1, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void updateEvent(EventPlayerUpdate event) {
		String mode = this.antiVoidMode.getValString();
		double dist = fallDist.getValDouble();		
		boolean shouldSave = isOverVoid() && !mc.thePlayer.capabilities.isFlying;
		if(shouldSave) {
			if(mc.thePlayer.fallDistance >= dist) {
				shouldSendPackets = false;
				voidTicks++;
				if(mode.equalsIgnoreCase("Flag")) {
					mc.thePlayer.sendQueue.addToSendQueue(new C06PacketPlayerPosLook());
				}
				if(mode.equalsIgnoreCase("Motion")) {
					mc.thePlayer.motionY = -0.0784000015258789;
				}
				if(mode.equalsIgnoreCase("Clip")) {
					if(voidTicks > 0 && voidTicks % 3 == 0) {
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+fallDist.getValDouble()/2, mc.thePlayer.posZ);
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+fallDist.getValDouble()/2, mc.thePlayer.posZ, false));
					}
				}
				if(mode.equalsIgnoreCase("Last Position")) {
					if(voidTicks > 0 && voidTicks % 3 == 0) {
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(lastz, lasty, lastx, false));
					}
				}
				if(mode.equalsIgnoreCase("Creative")) {
					PlayerCapabilities p = new PlayerCapabilities();
					if(voidTicks >= 1) {
						p.allowFlying = true;
						p.isFlying = true;
						p.setFlySpeed(1);
						PacketUtil.sendPacketNoEvent(new C13PacketPlayerAbilities(p));
					}
					if(voidTicks == 2) {
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(lastz, lasty, lastx, false));
					}
					if(voidTicks > 10)
						voidTicks = 0;
				}
				if(mode.equalsIgnoreCase("Blink")) {
 		        	mc.thePlayer.setPosition(lastPos.getX(), lastPos.getY(), lastPos.getZ());
				}
			}
		} else {
			shouldSendPackets = true;
			lastPos = mc.thePlayer.getPosition();
			voidTicks = 0;
			lastx = mc.thePlayer.posX;
			lasty = mc.thePlayer.posY;
			lastz = mc.thePlayer.posZ;
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		String mode = this.antiVoidMode.getValString();
		double dist = fallDist.getValDouble();
		
	    if(mode.equalsIgnoreCase("Blink")) {
	        if (event.getPacket() instanceof C03PacketPlayer) {
	    		boolean shouldSave = isOverVoid() && !mc.thePlayer.capabilities.isFlying && !Axyl.ins.modManager.getModuleByName("Flight").isToggled();
	    		if(shouldSave) {
		            if(mc.thePlayer.fallDistance < this.fallDist.getValDouble()) {
		            	packets.add(event.getPacket());
		            }
		            event.setCancelled(true);
		        } else {
		        	if(shouldSendPackets)
		            packets.forEach(PacketUtil::sendPacket);
		            packets.clear();
		        }
	        }
	    }
	}
	
	public boolean shouldBlink() {
		if(Axyl.ins.modManager.getModuleByName("Flight").isToggled()) {
			return false;
		}
		if((mc.theWorld.getBlockState(mc.thePlayer.getPosition().down(1)).getBlock() instanceof BlockAir) && mc.thePlayer.fallDistance <= this.fallDist.getValDouble()) {
			return true;
		}
		return false;
	}
	
    public boolean isOverVoid() {
    	if(Axyl.ins.modManager.getModuleByName("Flight").isToggled()) {
    		return false;
    	}
    	for (double posY = 0; posY < mc.thePlayer.posY; posY++) {
    		if (!(mc.theWorld.getBlockState(new BlockPos((mc.thePlayer).posX, posY, (mc.thePlayer).posZ)).getBlock() instanceof BlockAir)) {
    			return false;
    		}
    	} 
    	return true;
    }
    
	@Override
	public void onEnable() {
		shouldSendPackets = true;
		lastPos = mc.thePlayer.getPosition();
		voidTicks = 0;
		super.onEnable();
	}
}
