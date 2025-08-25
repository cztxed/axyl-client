package axyl.client.modules.features.combat;
 
import java.awt.List;
import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.input.InputClickEvent;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventHitEntity;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.time.Timer;
import axyl.client.util.world.EntityUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class WTap extends Module
{
	public int ticksToWTap;
	public Setting delay;
	private Setting wtapMode;
	
	public WTap() {
		super("WTap", "", Keyboard.KEY_NONE, Category.Combat);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		options.add("Legit");
		options.add("Packet");
		
		Axyl.ins.settingManager.createSetting(wtapMode = new Setting("Mode", this, "Legit", options));
		super.moduleSetup();
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = wtapMode.getValString();
		this.suffix = mode;
        ticksToWTap++;

        if(mc.thePlayer.isSprinting() && mc.thePlayer.isMoving()) {
        	if(mode.equalsIgnoreCase("Legit")) {
                if (ticksToWTap == 1)
                	mc.thePlayer.setSprinting(false);
                if (ticksToWTap == 3)
                	mc.thePlayer.setSprinting(true);
        	}
        	if(mode.equalsIgnoreCase("Packet")) {
                if (ticksToWTap == 2) 
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                if(ticksToWTap == 3) 
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        	}
        }
	}
	
	@Subscribe
	public void eventAttackPlayer(EventHitEntity event) {
		ticksToWTap = 0;
    }

    @Override
    public void onEnable() {
    	ticksToWTap = 0;
    	super.onEnable();
    }
}
