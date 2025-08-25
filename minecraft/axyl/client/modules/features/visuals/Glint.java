package axyl.client.modules.features.visuals;

import net.minecraft.client.renderer.GlStateManager;  

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;

public class Glint extends Module {
	
	public static Setting glintColor;
	
	public Glint() {
        super("Glint", "" , Keyboard.KEY_NONE, Category.Visuals);
    }
    
    @Override
    public void moduleSetup() {
    	Axyl.ins.settingManager.createSetting(glintColor = new Setting("Color", this, 10, 100, 100));
    }

	public static int glintColor() {
		return glintColor.getValColor();
	}
}
