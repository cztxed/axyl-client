package axyl.client.modules.features.combat;

import java.awt.AWTException;
import java.awt.Event;  
import java.awt.List;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.input.EventMouseDelta;
import axyl.client.events.input.InputClickEvent;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.time.Timer;
import axyl.client.util.world.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AimAssist extends Module
{
	public Timer timer = new Timer();
	public Timer delay = new Timer();
	
	public float MULTIPLER;
	public float MAX_AIM_FOV;
	public float MIN_AIM_FOV;
	
	private Setting aimFovMode;
	private Setting maxAimFov;
	private Setting minAimFov;
	private Setting aimRange;
	private Setting aimStrenght;
	private Setting movingOnly;
	private Setting forwardOnly;
	private Setting attachingDelay;
	private Setting clickingOnly;

	public AimAssist() {
		super("AimAssist", "", 0, Category.Combat);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Static");
		options.add("Dynamic");
		
		Axyl.ins.settingManager.createSetting(aimFovMode = new Setting("Fov mode", this, "Static", options));
		Axyl.ins.settingManager.createSetting(maxAimFov = new Setting("Max fov", this, 45, 0, 180, 0, false)); 
		Axyl.ins.settingManager.createSetting(minAimFov = new Setting("Min fov", this, 0, 0, 180, 0, false)); 
		Axyl.ins.settingManager.createSetting(attachingDelay = new Setting("Attaching delay (ms)", this, 25, 0, 400, 0, false));
		Axyl.ins.settingManager.createSetting(aimRange = new Setting("Aiming range", this, 5, 2, 10, 0, false));
		Axyl.ins.settingManager.createSetting(aimStrenght = new Setting("Strength %", this, 100, 10, 200, 0, false));
		Axyl.ins.settingManager.createSetting(clickingOnly = new Setting("Clicking only", this, false, false));
		Axyl.ins.settingManager.createSetting(movingOnly = new Setting("Moving only", this, false, false));
		Axyl.ins.settingManager.createSetting(forwardOnly = new Setting("Forward only", this, false, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventMouseDelta(EventMouseDelta event) {
		if(clickingOnly.getValBoolean()) {
			if(mc.gameSettings.keyBindAttack.isKeyDown()) {
				if(MULTIPLER > 0) {
					MULTIPLER+=((0-MULTIPLER)-0.05)*0.01;
				}
			} else {
				MULTIPLER+=((0-MULTIPLER)-0.05)*0.15;
			}
		} else {
			MULTIPLER = 1;
		}
		
		if(minAimFov.getValDouble() > maxAimFov.getValDouble())
			minAimFov.setValDouble(maxAimFov.getValDouble());
		
        Object[] getEnt = EntityUtil.getEntityCustom(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw, aimRange.getValDouble(), aimRange.getValDouble()/2, 1.0F);
        if(getEnt == null)
        	return;
        
        Entity entity = (Entity) getEnt[0];
        if(mc.currentScreen == null)
        if(!AntiBot.bots.contains(entity))
        if(entity instanceof EntityPlayer)
        if(entity != null) {
    		if(aimFovMode.getValString().equalsIgnoreCase("Dynamic")) {
    			MAX_AIM_FOV = (float) (maxAimFov.getValDouble()*(8-mc.thePlayer.getDistanceToEntity(entity)))*0.3f;
        		MIN_AIM_FOV = (float) (minAimFov.getValDouble()*(8-mc.thePlayer.getDistanceToEntity(entity)))*0.3f;
    		} else {
    			MAX_AIM_FOV = (float) maxAimFov.getValDouble();
        		MIN_AIM_FOV = (float) minAimFov.getValDouble();
    		}
    		
            float[] targetRot = getRotations(entity);

            boolean minFov = mc.thePlayer.rotationYaw < targetRot[0]+MIN_AIM_FOV && mc.thePlayer.rotationYaw > targetRot[0]-MIN_AIM_FOV;
			boolean maxFov = mc.thePlayer.rotationYaw > targetRot[0]-MAX_AIM_FOV && mc.thePlayer.rotationYaw < targetRot[0]+MAX_AIM_FOV;
			if(maxFov) {
				if(!minFov) {
					if(movingOnly.getValBoolean())
					if(!mc.thePlayer.isMoving())
						return;
					
					if(forwardOnly.getValBoolean())
					if(mc.thePlayer.moveForward == 0)
						return;
					
					float sens = (float) (mc.gameSettings.mouseSensitivity);
					if(event.deltaY != 0)
					if(MULTIPLER > 0.1 && delay.hasReached((int)attachingDelay.getValDouble())) {
	        			{
	        				int diff = (int) ((targetRot[0]-mc.thePlayer.rotationYaw)*(aimStrenght.getValDouble()/100));
	        				if(diff != 0)
	        					diff+=RandomUtils.nextInt(0, 6)-RandomUtils.nextInt(0, 6);
	        				
	        		        Robot robot;
							try {
								robot = new Robot();
		        				Point point = MouseInfo.getPointerInfo().getLocation();
		        		        int x = (int) point.getX();
		        		        int y = (int) point.getY();
		        		        robot.mouseMove(x+diff, y);
							} catch (AWTException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	        			}
					}
            	}
			} else {
				delay.reset();
			}
        }
	}
    
	@Subscribe
	public void eventClick(InputClickEvent event) {
		MULTIPLER = 1;
	}
	
    public float[] getRotations(Entity entity) {
        if (entity == null)
            return null;
       
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
    	double diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = (entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight()))-0.1;
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight()) - (double) 1.2f;
        }
        
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI)-90;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }
    
    @Override
    public void onEnable() {
    	MULTIPLER = 0;
    	super.onEnable();
    }
}
