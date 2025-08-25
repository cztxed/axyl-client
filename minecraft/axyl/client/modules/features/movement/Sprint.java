package axyl.client.modules.features.movement;
 
import java.util.ArrayList;  

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerSprint;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class Sprint extends Module {

	public Setting sprintMode;

	public Sprint() {
		super("Sprint", "Automatically sprints for You", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Normal");
		options.add("Multi");
		
		Axyl.ins.settingManager.createSetting(this.sprintMode = new Setting("Mode", this, "Normal", options));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = this.sprintMode.getValString();
		this.suffix = mode;

		if(Axyl.ins.modManager.getModuleByName("Scaffold").isToggled()
		|| Axyl.ins.modManager.getModuleByName("Flight").isToggled())
			return;
		
		if(sprintMode.getValString().equalsIgnoreCase("Normal")) {
	    	if (mc.thePlayer.moveForward > 0.0f && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
    			mc.thePlayer.setSprinting(true);
	    	}
		}
		if(sprintMode.getValString().equalsIgnoreCase("Multi")) {
			mc.thePlayer.setSprinting(true);
		}
	}
	
	@Subscribe
	public void eventSprint(EventPlayerSprint event) {
		if(Axyl.ins.modManager.getModuleByName("Scaffold").isToggled()
		|| Axyl.ins.modManager.getModuleByName("Flight").isToggled())
			return;
		if(mc.thePlayer.isMoving())
		if(sprintMode.getValString().equalsIgnoreCase("Multi")) {
			event.setCancelled(true);
		}
	}
	
	@Subscribe
	public void eventJump(EventJump event) {
		if(Axyl.ins.modManager.getModuleByName("Scaffold").isToggled()
		|| Axyl.ins.modManager.getModuleByName("Flight").isToggled())
			return;
		
		if(sprintMode.getValString().equalsIgnoreCase("Multi")) {
            float yaw = mc.thePlayer.rotationYaw;
            final float forward = mc.thePlayer.moveForward;
            final float strafe = mc.thePlayer.moveStrafing;
            yaw += ((forward < 0.0f) ? 180 : 0);
            if (strafe < 0.0f) {
                yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
            }
            if (strafe > 0.0f) {
                yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
            }
            event.setYaw(yaw);
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
