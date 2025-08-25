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

public class ItemSize extends Module {
	
    public static Setting ItemY;
    public static Setting ItemX;
	public static Setting ItemZ;
	
	public ItemSize() {
        super("ItemSize", "" , Keyboard.KEY_NONE, Category.Visuals);
    }
    
    @Override
    public void moduleSetup() {
    	Axyl.ins.settingManager.createSetting(ItemX = new Setting("ItemX", this, 0, -2.0, 2, 1, false));
    	Axyl.ins.settingManager.createSetting(ItemY = new Setting("ItemY", this, 0, -2.0, 2, 1, false));
    	Axyl.ins.settingManager.createSetting(ItemZ = new Setting("ItemZ", this, 0, -2.0, 2, 1, false));
    }
}
