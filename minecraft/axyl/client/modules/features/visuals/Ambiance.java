package axyl.client.modules.features.visuals;

import java.util.ArrayList;  

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class Ambiance extends Module {
	
	private Setting timeVal;
	private Setting timeMode;
    
    public Ambiance() {
        super("Ambiance", "" , 0, Category.Visuals);
    }
    
    @Override
    public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("Custom");
        options.add("Day");
        options.add("Night");
        options.add("Evening");
        options.add("Morning");
        options.add("Funny");
        
        Axyl.ins.settingManager.createSetting(timeMode = new Setting("Time mode", this, "Custom", options));
        Axyl.ins.settingManager.createSetting(timeVal = new Setting("Time", this, 15000, 0, 20000, 0, false));
        super.moduleSetup();
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
        final String mode = timeMode.getValString();
        this.suffix = mode;
        if(mode.equalsIgnoreCase("Evening")) {
    		mc.theWorld.setWorldTime(14000L);
    	}
    	if(mode.equalsIgnoreCase("Day")) {
    		mc.theWorld.setWorldTime(1000L);
    	}
    	if(mode.equalsIgnoreCase("Night")) {
    		mc.theWorld.setWorldTime(15000L);
    	}
    	if(mode.equalsIgnoreCase("Morning")) {
    		mc.theWorld.setWorldTime(22500L);
    	}
    	if(mode.equalsIgnoreCase("Custom")) {
    		mc.theWorld.setWorldTime((long) timeVal.getValDouble());
    	}
    }
    
    @Subscribe
    public void eventPacket(EventPacket event) {
    	if(event.getPacket() instanceof S03PacketTimeUpdate) {
    		event.setCancelled(true);
    	}
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	super.onDisable();
    }
}
