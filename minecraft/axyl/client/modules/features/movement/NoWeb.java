package axyl.client.modules.features.movement;

import java.util.ArrayList; 

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventWeb;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.player.MovementUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class NoWeb extends Module {

	private Setting noWebMode;
	public boolean wasInWeb;

	public NoWeb() {
		super("NoWeb", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		options.add("Vanilla");

		Axyl.ins.settingManager.createSetting(noWebMode = new Setting("Mode", this, "Vanilla", options));
		super.moduleSetup();
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		this.suffix = noWebMode.getValString();
	}
    
    @Subscribe
    public void eventWeb(EventWeb event) {
    	event.setCancelled(true);
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
