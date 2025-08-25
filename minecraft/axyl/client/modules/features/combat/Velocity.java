package axyl.client.modules.features.combat;

import net.minecraft.entity.Entity;  
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet; 
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import tv.twitch.chat.ChatTextMessageToken;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil; 

public class Velocity extends Module {

	public boolean setMotion;
	public int veloCounter;
	public double mx;
	public double mz;

	public Setting velocityMode;
	public Setting hori;
	public Setting vert;
	private Setting debug;
	
    public Velocity() {
        super("Velocity", "", 0, Category.Combat);
    }
    
    @Override
    public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        options.add("Vanilla");
        options.add("Watchdog");
        options.add("Grim-OLD");
        options.add("Reversed");
        options.add("PacketSpoof");
        options.add("Legit");
        
        Axyl.ins.settingManager.createSetting(velocityMode = new Setting("Velocity Mode", this, "Vanilla", options));
        Axyl.ins.settingManager.createSetting(hori = new Setting("Horizontal", this, 0, 0, 100, 1, false));
        Axyl.ins.settingManager.createSetting(vert = new Setting("Vertical", this, 0, 0, 100, 1, false));
        
        Axyl.ins.settingManager.createSetting(debug = new Setting("Debug", this, false, false));
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
        final String mode = velocityMode.getValString();
        this.suffix = mode;
        if(mode.equalsIgnoreCase("Watchdog")) {
        	/* Bypasses Manual velocity Check */
        	if(setMotion) {
            	if(mc.thePlayer.hurtTime > 0) {
            		mc.thePlayer.motionX = mx;
                	mc.thePlayer.motionZ = mx;
            	}
            	setMotion = false;
            }
        }
        if(mode.equalsIgnoreCase("Legit")) {

        }
        if(mode.equalsIgnoreCase("PacketSpoof")) {
        	mc.timer.timerSpeed = 1f;
        }
    }

    @Subscribe
    public void eventPacket(EventPacket event) {
        final String mode = velocityMode.getValString();
        if (event.getPacket() instanceof S32PacketConfirmTransaction) {
            if(mode.equalsIgnoreCase("Grim-OLD")) {
        		if(mc.thePlayer.ticksExisted > 20) {
        			event.setCancelled(true);
        		}
            }
    	}
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
        	S12PacketEntityVelocity p = (S12PacketEntityVelocity)event.getPacket();
        	if(mc.theWorld.getEntityByID(p.getEntityID()) instanceof EntityPlayer)
        	if(p.getEntityID() == mc.thePlayer.getEntityId()) {
        		veloCounter++;
        		if(debug.getValBoolean())
        			Axyl.sendMessage("§cApplied Velocity: "+mc.thePlayer.ticksExisted + " X:"+p.getMotionX()/8000 + " Y:"+p.getMotionY()/8000 + " Z:"+p.getMotionZ()/8000);
        		
                if(mode.equalsIgnoreCase("Grim-OLD")) {
            		event.setCancelled(true);
                }
                if(mode.equalsIgnoreCase("PacketSpoof")) {
                   	PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX+p.getMotionX()/8000, mc.thePlayer.posY+p.getMotionY()/8000, mc.thePlayer.posZ+p.getMotionZ()/8000, false));
                	mc.timer.timerSpeed = 0.3f;
                	event.setCancelled(true);
                }
                if(mode.equalsIgnoreCase("Reversed")) {
	        		mc.thePlayer.motionX = -(p.getMotionX()/8000);
	        		mc.thePlayer.motionZ = -(p.getMotionZ()/8000);
	          		mc.thePlayer.motionY = (p.getMotionY()/8000);
	        		event.setCancelled(true);
                }
	            if (mode.equalsIgnoreCase("Vanilla")) {
	            	if(hori.getValDouble() > 0) {
		        		mc.thePlayer.motionX = (p.getMotionX()/8000)*hori.getValDouble()/100;
		        		mc.thePlayer.motionZ = (p.getMotionZ()/8000)*hori.getValDouble()/100;
	            	}
	            	if(vert.getValDouble() > 0)
	            		mc.thePlayer.motionY = (p.getMotionY()/8000)*vert.getValDouble()/100;
	        		event.setCancelled(true);
	            }	
	            if(mode.equalsIgnoreCase("Legit")) {
	                if(mc.thePlayer.onGround) {
	                    mc.thePlayer.movementInput.jump = true;
	                }
	            }
	            if(mode.equalsIgnoreCase("Watchdog")) {
	            	if(mc.thePlayer.onGround)
	            	mc.thePlayer.motionY = (p.getMotionY()/8000);
	            	event.setCancelled(true);
	            }
	        } 
        }
   }
    @Override
    public void onEnable() {
    	setMotion = false;
        super.onEnable();
    }
}
