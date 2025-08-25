package axyl.client.modules.features.combat;

import java.util.ArrayList;  
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.render.EventRender2D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class AntiBot extends Module
{
    public static ArrayList<Entity> bots = new ArrayList<>();
    public int ticksExisted;
	private Setting antiBotMode;
    
    public AntiBot() {
        super("AntiBot", "Removes suspicious entities", Keyboard.KEY_NONE, Category.Combat);
    }
    
    @Override
    public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("Simple");
        Axyl.ins.settingManager.createSetting(this.antiBotMode = new Setting("Mode", this, "Simple", options));
        super.moduleSetup();
    }

    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
    	ticksExisted++;
    }
    
    @Subscribe
    public void eventPacket(EventPacket event) {
    	if(event.getPacket() instanceof S08PacketPlayerPosLook) {
    		ticksExisted = 0;
    	}
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
        	S12PacketEntityVelocity p = (S12PacketEntityVelocity)event.getPacket();
        	mc.theWorld.getLoadedEntityList().forEach(player -> {
        		if(p.motionX == 0
        		&& p.motionY == 0
        		&& p.motionZ == 0) {
        			player.isBot = 1;
        		}
        	});
        }
    }

    @Subscribe
    public void eventRender2D(EventRender2D event) {
    	this.suffix = this.antiBotMode.getValString();
    	mc.theWorld.getLoadedEntityList().forEach(player -> {
	        if (this.antiBotMode.getValString().equalsIgnoreCase("Simple")) {
	    		if(player instanceof EntityPlayer) {
	    			boolean isOnTab = mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(info -> info.getGameProfile().getName().equalsIgnoreCase(player.getName()));
                    final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
                    final String entName = player.getName();
  
                    if(!isOnTab) {
                    	if(player.isBot == 0)
                    	player.isBot = 1;
                    } else {
                    	bots.remove(player);
                    	player.isBot = 0;
                    }
                    
                    if(player.isBot == 1) {
                    	bots.add(player);
                    	player.isBot = 2;
                    }
	    		}
	        }
    	});
    }
    
    public static ArrayList<Entity> getBots() {
        return bots;
    }
    
    @Override
    public void onEnable() {
    	mc.theWorld.getLoadedEntityList().forEach(player -> {
    		player.t1 = 0;
    		player.isBot = 0;
    	});
    	ticksExisted = 0;
        bots.clear();
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	mc.theWorld.getLoadedEntityList().forEach(player -> {
    		player.t1 = 0;
    		player.isBot = 0;
    	});
        bots.clear();
    	super.onDisable();
    }
}
