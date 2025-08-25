package axyl.client.modules.features.player;

import java.util.ArrayList;  

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventTick;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.block.BlockChest;

public class FastPlace extends Module {

	public FastPlace() {
		super("FastPlace", "Removes block place delay", Keyboard.KEY_NONE, Category.Player);
	}
	
	@Subscribe
	public void eventTick(EventTick event) {
		try {
			if(!(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockChest))
				mc.rightClickDelayTimer = 0;
		} catch (Exception e) {
			// TODO: handle exception
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
