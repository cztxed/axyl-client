package axyl.client.modules.features.player;

import java.util.ArrayList; 
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class NoRotate extends Module {

	public boolean canSetYaw;
	
	public float yaw;
	public float pitch;
	
	public NoRotate() {
		super("NoRotate", "", Keyboard.KEY_NONE, Category.Player);
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		if(canSetYaw) {
			mc.thePlayer.rotationYaw = yaw;
			mc.thePlayer.rotationPitch = pitch;
			canSetYaw = false;
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		if(event.getPacket() instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook p = (S08PacketPlayerPosLook)event.getPacket();
			yaw = mc.thePlayer.rotationYaw;
			pitch = mc.thePlayer.rotationPitch;
			canSetYaw = true;
		}
	}
	
	@Override
	public void onEnable() {
		canSetYaw = false;
		super.onEnable();
	}
}
