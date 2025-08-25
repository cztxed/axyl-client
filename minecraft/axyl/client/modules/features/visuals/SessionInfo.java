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

public class SessionInfo extends Module {
	
	public SessionInfo() {
        super("SessionInfo", "" , Keyboard.KEY_NONE, Category.Visuals);
    }
}
