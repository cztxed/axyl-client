package axyl.client.modules.features.visuals;

import java.util.ArrayList; 

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Timer extends Module
{
	private Setting speed;
	private Setting ticks;
    
    public Timer() {
        super("Timer", "Makes Your game run faster", 0, Category.Player);
    }
    
    @Override
    public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        Axyl.ins.settingManager.createSetting(ticks = new Setting("Ticks", this, 1, 1, 20, 0, false));
        Axyl.ins.settingManager.createSetting(speed = new Setting("Speed", this, 1, 0.1, 5, 1, false));
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
		mc.timer.timerSpeed = 1f;
    	if(mc.thePlayer.ticksExisted % ticks.getValDouble() == 0) {
    		mc.timer.timerSpeed = (float) speed.getValDouble();
    	}
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	mc.timer.timerSpeed = 1f;
        super.onDisable();
    }
}
