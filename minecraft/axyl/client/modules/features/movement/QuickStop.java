package axyl.client.modules.features.movement;

import org.lwjgl.input.Keyboard; 

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.settings.KeyBinding;

public class QuickStop extends Module {

	public int left;
	public int right;
	public int forward;
	public int back;
	
	public QuickStop() {
		super("QuickStop", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Subscribe
	public void eventUpdateRotation(EventUpdateRotation event) {
		if(!mc.thePlayer.isMoving()) {
			if(mc.thePlayer.hurtTime == 0) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionX *= 0.98;
					mc.thePlayer.motionZ *= 0.98;
				}
			}
		}
	}
	
	@Override
	public void onEnable() {
		left = 2;
		right = 2;
		forward = 2;
		back = 2;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
