package axyl.client.modules.features.movement;
 
import java.util.ArrayList;  

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerSprint;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventTick;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.player.MovementUtil;
import axyl.client.util.time.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;

public class LegitScaffold extends Module {
	
	public boolean shouldReset;
	public Setting sprintMode;
	public Timer delay = new Timer();
		
	public LegitScaffold() {
		super("LegitScaffold", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Subscribe
	public void eventTick(EventTick event) {
		BlockPos blockBelowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ);
		if(mc.thePlayer.rotationPitch > 40) {
			shouldReset = true;
		} else {
			if(shouldReset) {
				mc.gameSettings.keyBindSneak.pressed = false;
				timer.reset();
				shouldReset = false;
			}
		}
		if(mc.theWorld.getBlockState(blockBelowPlayer).getBlock() instanceof BlockAir) {
			if(mc.thePlayer.rotationPitch > 40)
			if(mc.thePlayer.onGround) {
				mc.gameSettings.keyBindSneak.pressed = true;
				delay.reset();
			}
		} else {
			if(delay.hasReached(100) && !delay.hasReached(250))
			mc.gameSettings.keyBindSneak.pressed = false;
		}
	}

	@Override
	public void onEnable() {
		shouldReset = false;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(!delay.hasReached(400)) {
			mc.gameSettings.keyBindSneak.pressed = false;
		}
		super.onDisable();
	}
}
