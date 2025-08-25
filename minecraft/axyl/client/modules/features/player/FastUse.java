package axyl.client.modules.features.player;

import java.util.ArrayList;  

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventTick;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastUse extends Module {

	public int delayTicks;
	private Setting regenMode;
	private Setting packets;

	public FastUse() {
		super("FastUse", "", Keyboard.KEY_NONE, Category.Player);
	}

	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		options.add("Vanilla");
		options.add("Test1");
		
		Axyl.ins.settingManager.createSetting(regenMode = new Setting("Mode", this, "Vanilla", options));
		Axyl.ins.settingManager.createSetting(packets = new Setting("Packets", this, 10, 1, 50, 0, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventTick(EventTick event) {
		String mode = regenMode.getValString();
		if(mc.thePlayer.getCurrentEquippedItem().getItem() != null)
		if(mc.thePlayer.isEating() && (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) {
			delayTicks++;
			if(mode.equalsIgnoreCase("Vanilla")) {
				if(delayTicks > 1) {
					for(int i = 0; i < packets.getValDouble(); i++) {
						PacketUtil.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
					}
				}
			}
		} else {
			delayTicks = 0;
		}
	}
	
	@Override
	public void onEnable() {
		delayTicks = 0;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
