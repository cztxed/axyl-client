package axyl.client.modules.features.visuals;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.modules.Category;
import axyl.client.modules.Module;

public class ClickGui extends Module {

	public static boolean setClickGui = true;
	public static boolean loadKeybinds = true;
	public static boolean loadVisuals = false;
	public static Module setting;
	public static Module bind;
	public static ArrayList<Composition> composition = new ArrayList<>();
	
	public ClickGui() {
		super("ClickGui", "", Keyboard.KEY_RSHIFT, Category.Visuals);
	}
	
	@Override
	public void onEnable() {
		mc.displayGuiScreen(new CUI());
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		mc.displayGuiScreen(new CUI());
		super.onDisable();
	}
}
