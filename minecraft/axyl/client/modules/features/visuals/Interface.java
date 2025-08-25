package axyl.client.modules.features.visuals;

import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.render.EventRender2D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.gui.notifications.Notification;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.visuals.interfaces.AxylTheme;
import axyl.client.modules.features.visuals.interfaces.ClassicTheme;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;

public class Interface extends Module {

	public static Setting interfaceColor;
	public static Setting interfaceColor2;
	public static Setting worldFogColor;
	
	public static Setting posx;
	public static Setting posy;
	
	private Setting interfaceMode;
	private Setting color3d3;

	public Interface() {
		super("Interface", "", Keyboard.KEY_NONE, Category.Visuals);
	}
	
	@Override
	public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        options.add("Classic");
        options.add("Axyl");

        Axyl.ins.settingManager.createSetting(interfaceMode = new Setting("Theme", this, "Classic", options));
        Axyl.ins.settingManager.createSetting(posx = new Setting("Position X", this, 0, 0, 50, 0, false));
        Axyl.ins.settingManager.createSetting(posy = new Setting("Position Y", this, 0, 0, 50, 0, false));
        Axyl.ins.settingManager.createSetting(interfaceColor = new Setting("First", this, 10, 100, 100));
        Axyl.ins.settingManager.createSetting(interfaceColor2 = new Setting("Second", this, 255, 100, 100));
        Axyl.ins.settingManager.createSetting(worldFogColor = new Setting("World Fog Color", this, 255, 100, 100));
        super.moduleSetup();
	}
	
	@Subscribe
	public void eventRender2D(EventRender2D event) {
		String mode = interfaceMode.getValString();
		switch (mode) {
			case "Classic":
				ClassicTheme.cl.renderInterface(event);
				break;
			case "Axyl":
				AxylTheme.cl.renderInterface(event);
				break;
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
