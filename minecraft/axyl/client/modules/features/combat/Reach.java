package axyl.client.modules.features.combat;
 
import java.awt.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.input.InputClickEvent;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.world.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Reach extends Module
{
	
	public Setting maxReach;
	public Setting minReach;
	
	public Reach() {
		super("Reach", "", Keyboard.KEY_NONE, Category.Combat);
	}
	
	@Override
	public void moduleSetup() {
		Axyl.ins.settingManager.createSetting(maxReach = new Setting("Max Reach", this, 3, 3, 6, 2, false));
		Axyl.ins.settingManager.createSetting(minReach = new Setting("Min Reach", this, 3, 3, 6, 2, false));
		super.moduleSetup();
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		this.suffix = minReach.getValDouble() + " - " + maxReach.getValDouble();
	}
	
	@Subscribe
	public void onClick(InputClickEvent event) {
        Object[] ent = EntityUtil.getEntityCustom(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw, RandomUtils.nextDouble(minReach.getValDouble(), maxReach.getValDouble()), -0.0f, 1.0F);
        mc.objectMouseOver = new MovingObjectPosition((Entity) ent[0], (Vec3) ent[1]);
        mc.pointedEntity = (Entity)ent[0];
    }

    @Override
    public void onEnable() {
    	super.onEnable();
    }
}
