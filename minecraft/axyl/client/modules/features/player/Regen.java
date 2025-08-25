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
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {

	private Setting packets;
	private Setting ticks;

	public Regen() {
		super("Regen", "", Keyboard.KEY_NONE, Category.Player);
	}
	
	@Override
	public void moduleSetup() {
		Axyl.ins.settingManager.createSetting(packets = new Setting("Packets", this, 30, 1, 300, 0, false));
		Axyl.ins.settingManager.createSetting(ticks = new Setting("Ticks Per Second", this, 10, 0, 20, 0, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventTick(EventTick event) {
		if(mc.thePlayer.ticksExisted % MathUtils.clamp(20-((int)ticks.getValDouble()), 1, 20-((int)ticks.getValDouble())) == 0) {
			for(int i = 0; i < packets.getValDouble(); i++) {
				PacketUtil.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
			}
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
