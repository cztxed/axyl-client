package axyl.client.modules.features.visuals;

import java.util.ArrayList; 

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class NoWeather extends Module {

    public NoWeather() {
        super("NoWeather", "", Keyboard.KEY_NONE, Category.Visuals);
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
        mc.theWorld.setThunderStrength(0);
        mc.theWorld.setRainStrength(0);
    }
}
