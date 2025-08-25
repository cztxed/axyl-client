package axyl.client.modules.features.movement;

import java.util.ArrayList;
import java.util.Arrays;   

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class MovementCorrection extends Module
{
    public static Setting killAura;
    public static Setting scaffold;
    public static Setting rotationHelper;
    public static Setting bedFucker;

	public MovementCorrection() {
        super("MovementCorrection", "", Keyboard.KEY_NONE, Category.Movement);
    }
    
    @Override
    public void moduleSetup() {
		Axyl.ins.settingManager.createSetting(killAura = new Setting("KillAura", this, true, false));
		Axyl.ins.settingManager.createSetting(scaffold = new Setting("Scaffold", this, true, false));
		Axyl.ins.settingManager.createSetting(rotationHelper = new Setting("RotationHelper", this, true, false));
		Axyl.ins.settingManager.createSetting(bedFucker = new Setting("BedFucker", this, true, false));
		super.moduleSetup();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
}
