package axyl.client.modules.features.movement;

import java.util.ArrayList; 

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventStrafe;
import axyl.client.events.render.EventRender2D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class KeepDirection extends Module {

	public boolean isKeyPressed;
	public float lYaw;
	public float lastS;
	
	public KeepDirection() {
		super("KeepDirection", "", Keyboard.KEY_NONE, Category.Movement);
	}

	@Subscribe
	public void updateStrafe(EventStrafe event)
	{
		if(isKeyPressed) {
			event.setYaw(lYaw);
			event.setStrafe(lastS);
		} else {
			lastS = event.getStrafe();
		}
	}

	@Subscribe
	public void eventJump(EventJump event)
	{
		if(isKeyPressed) {
			event.setYaw(lYaw);
		}
	}
	
	@Subscribe
	public void event2D(EventRender2D event)
	{
		isKeyPressed = Keyboard.isKeyDown(Keyboard.KEY_CAPITAL);
		if(!isKeyPressed) {
			lYaw = Math.round(mc.thePlayer.rotationYaw/45)*45;
			//Axyl.sendMessage(""+lYaw);
		}
	}

	@Override
	public void onEnable() {
		lYaw = mc.thePlayer.rotationYaw;
		isKeyPressed = Keyboard.isKeyDown(Keyboard.KEY_CAPITAL);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
