package axyl.client.modules.features.other;

import java.util.ArrayList; 
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO.PI;

import axyl.client.Axyl;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventStrafe;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.movement.MovementCorrection;
import axyl.client.util.math.MathUtils;
import axyl.client.util.time.Timer;
import axyl.client.util.world.EntityUtil;
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

public class RotationsHelper extends Module {

	public Timer timer = new Timer();
	public static boolean SYNC_ROTS;
	public static float[] ROTS_TO_SYNC;
	public boolean SYNCED;
	public float YAW;
	public float PITCH;
	
	private Setting speedVal;

	public RotationsHelper() {
		super("RotationsHelper", "", Keyboard.KEY_NONE, Category.Other);
	}
	
	@Override
	public void moduleSetup() {
        Axyl.ins.settingManager.createSetting(speedVal = new Setting("Speed", this, 50, 10, 100, 0, false));
         super.moduleSetup();
	}
	
	@Subscribe
	public void eventRots(EventPlayerPreUpdate event) {
		if(SYNC_ROTS) {
			timer.reset();
			PITCH = ROTS_TO_SYNC[1];
			YAW = (float) MathUtils.clamp(ROTS_TO_SYNC[0], mc.thePlayer.rotationYaw-135, mc.thePlayer.rotationYaw+135);
			SYNCED = false;
			SYNC_ROTS = false;
		}
		if(!SYNCED) {
			event.setYaw(YAW);
			event.setPitch(PITCH);
			mc.thePlayer.rotationPitchHead = PITCH;
		}
	}
	
	@Subscribe
	public void eventUpdateRots(EventUpdateRotation event) {
		float sens = (float) (Math.abs(mc.gameSettings.mouseSensitivity-MathUtils.roundToPlace(mc.gameSettings.mouseSensitivity, 1))+MathUtils.roundToPlace((speedVal.getValDouble()/100), 1));
		if(Math.abs(mc.thePlayer.rotationYaw - YAW) < 5 && Math.abs(mc.thePlayer.rotationPitch - PITCH) < 9) {
			SYNCED = true;
		}
		if(!SYNCED) {
			{
				int diff = (int) (mc.thePlayer.rotationYaw-YAW);
				float randDiff = RandomUtils.nextInt(0, 8)-RandomUtils.nextInt(0, 21);
				diff = (int) MathUtils.clamp(diff, -35-RandomUtils.nextInt(0, 5), 35+RandomUtils.nextInt(0, 5));
				if(diff > 0 && diff < 9) {
					diff = 9;
				}
				if(diff > -9 && diff < 0) {
					diff = -9;
				}
				float f1 = sens * sens * sens * 8.0F;
				float fin = (int)diff * f1;
				YAW = (float) (YAW + fin * 0.15D);
			}
			{
				int diff = (int) (mc.thePlayer.rotationPitch-PITCH);
				diff = (int) MathUtils.clamp(diff, -35-RandomUtils.nextInt(0, 5), 35+RandomUtils.nextInt(0, 5));
				float f1 = sens * sens * sens * 8.0F;
				float fin = diff * f1;
				PITCH = (float) (PITCH + fin * 0.15D);
			}
		}
	}
	
	@Subscribe
	public void eventStrafe(EventStrafe event) {
		if(SYNC_ROTS) {
			timer.reset();
			PITCH = ROTS_TO_SYNC[1];
			YAW = (float) MathUtils.clamp(ROTS_TO_SYNC[0], mc.thePlayer.rotationYaw-135, mc.thePlayer.rotationYaw+135);
			SYNCED = false;
			SYNC_ROTS = false;
		}
    	if(MovementCorrection.rotationHelper.getValBoolean()
    	&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
    		if(!SYNCED) {
    			event.setYaw(YAW);
    		}
    	}
	}
	
	@Subscribe
	public void eventJump(EventJump event) {
		if(SYNC_ROTS) {
			timer.reset();
			PITCH = ROTS_TO_SYNC[1];
			YAW = (float) MathUtils.clamp(ROTS_TO_SYNC[0], mc.thePlayer.rotationYaw-135, mc.thePlayer.rotationYaw+135);
			SYNCED = false;
			SYNC_ROTS = false;
		}
    	if(MovementCorrection.rotationHelper.getValBoolean()
    	&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
    		if(!SYNCED) {
    			event.setYaw(YAW);
    		}
    	}
	}
	
	@Override
	public void onEnable() {
		SYNC_ROTS = false;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
