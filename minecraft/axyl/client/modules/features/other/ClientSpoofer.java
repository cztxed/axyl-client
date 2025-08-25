package axyl.client.modules.features.other;

import java.util.ArrayList;  
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class ClientSpoofer extends Module {
    
	public static Setting spooferMode;

	public ClientSpoofer() {
		super("ClientSpoofer", "", Keyboard.KEY_NONE, Category.Other);
	}
	
	@Override
	public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("Null");
        options.add("Lunar");
        options.add("Forge");
        options.add("CheatBreaker");

        Axyl.ins.settingManager.createSetting(spooferMode = new Setting("Mode", this, "Lunar", options));
		super.moduleSetup();
	}
	
	@Subscribe
	public void updatePacket(EventPacket event) {
		String mode = spooferMode.getValString();
		this.suffix = mode;
		if(event.getPacket() instanceof C17PacketCustomPayload) {
			C17PacketCustomPayload p = (C17PacketCustomPayload)event.getPacket();
			if(mode.equalsIgnoreCase("Null")) {
                p.channel = null;
                p.data = null;
			}
			if(mode.equalsIgnoreCase("Lunar")) {
                p.channel = "REGISTER";
                p.data = createCustomPacketBuffer("Lunar-Client", false);
			}
			if(mode.equalsIgnoreCase("Forge")) {
                p.data = createCustomPacketBuffer("FML", true);
			}
			if(mode.equalsIgnoreCase("CheatBreaker")) {
                p.data = createCustomPacketBuffer("CB", false);
			}
		}
	}
    
    public PacketBuffer createCustomPacketBuffer(String data, boolean wrapped) {
        if (wrapped) {
            return new PacketBuffer(Unpooled.buffer()).writeString(data);
        } else {
            return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
        }
    }
}
