package axyl.client.modules.features.movement;
 
import java.util.ArrayList;   

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventNoSlow;
import axyl.client.events.player.EventPlayerPostUpdate;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventPrePreUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.time.Timer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module {

	public ArrayList<Packet> packets = new ArrayList<>();
	
	public int nextTick;
	public Timer dTimer = new Timer();
	public int intaveDelay;
	public int intaveTicks;
	public int ticks;
	public Setting noSlowMode;

	public NoSlowDown() {
		super("NoSlowDown", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Vanilla");
		options.add("Simple");
		options.add("NextTick");
		options.add("NextTick2");
		options.add("Intave");
		options.add("Post");
		
		Axyl.ins.settingManager.createSetting(this.noSlowMode = new Setting("Mode", this, "Vanilla", options));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventNoSlowDown(EventNoSlow event) {
		String mode = noSlowMode.getValString();
		if(mode.equalsIgnoreCase("Simple")) {
	        if (mc.thePlayer.getHeldItem() != null && mc.gameSettings.keyBindUseItem.isKeyDown()) {
	        	if(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
	        		PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0f, 0.0f, 0.0f));
	        	}
	        }
		}
		event.setCancelled(true);
	}
	
	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = noSlowMode.getValString();
		this.suffix = mode;
	}
	
	@Subscribe
	public void eventPreUpdate(EventPlayerPreUpdate event) {
		String mode = noSlowMode.getValString();
		boolean canSendPackets = mc.thePlayer.getHeldItem() != null && mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
		if(canSendPackets) {
			nextTick++;
		} else {
			nextTick = 0;
		}
		if(mode.equalsIgnoreCase("Vanilla")) {

		}
		if(mode.equalsIgnoreCase("Intave")) {
			if(canSendPackets)
			if(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood)
	        if (mc.thePlayer.isBlocking() && dTimer.hasReached(intaveDelay)) {
	        	mc.playerController.syncCurrentPlayItem();
	            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	        }
		}
		if(mode.equalsIgnoreCase("Simple")) {

		}
		if(mode.equalsIgnoreCase("NextTick")) {
	        if(canSendPackets) {
        		if(nextTick == 2) {
        			mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        		} else {
        			if(nextTick > 3) {
        				mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        			}
        		}
	        }
		}
		if(mode.equalsIgnoreCase("NextTick2")) {
	        if(canSendPackets) {
	        	if (dTimer.hasReached(intaveDelay)) {
	        		if(nextTick == 2) {
        	            mc.playerController.syncCurrentPlayItem();
        	            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	        		} else {
	       				if(nextTick > 3) {
	        	            mc.playerController.syncCurrentPlayItem();
	        	            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	        			}
	        		}
	        	}
	        }
		}
	}
	
	@Subscribe
	public void eventPostUpdate(EventPlayerPostUpdate event) {
		String mode = noSlowMode.getValString();
		boolean canSendPackets = mc.thePlayer.getHeldItem() != null && mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
		if(mode.equalsIgnoreCase("Post")) {
	       	if(canSendPackets) {
        		mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        	}
		}
		if(mode.equalsIgnoreCase("Intave")) {
			if(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood)
	        if (mc.thePlayer.isBlocking() && dTimer.hasReached(intaveDelay)) {
	        	mc.playerController.syncCurrentPlayItem();
	            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	            intaveTicks++;
	            if(intaveTicks % 2 == 0) {
	            	intaveDelay = 100;
	            } else {
	            	intaveDelay = 200;
	            }
	            dTimer.reset();
	        }
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		if(mc.theWorld == null)
			return;
		String mode = noSlowMode.getValString();
		boolean canSendPackets = mc.thePlayer.getHeldItem() != null && mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
		if(mode.equalsIgnoreCase("Vanilla")) {

		}
	}
	
	@Override
	public void onEnable() {
		intaveTicks = 1;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
