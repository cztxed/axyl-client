package axyl.client.modules.features.other;

import java.util.ArrayList; 
import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.network.EventPacket;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public class AntiGuiClose extends Module {

	public AntiGuiClose() {
		super("AntiGuiClose", "", Keyboard.KEY_NONE, Category.Other);
	}

	@Subscribe
	public void eventPacket(EventPacket event) {
		if(!(mc.currentScreen instanceof GuiContainer))
		if(event.getPacket() instanceof S2EPacketCloseWindow) {
			event.setCancelled(true);
		}
	}
}
