package axyl.client.modules.features.movement;

import java.util.Arrays;   

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class InvMove extends Module
{
    private Object GuiContainer;
    private GuiScreen GuiIngameMenu;
    
    public InvMove() {
        super("InvMove", "", Keyboard.KEY_NONE, Category.Movement);
    }
    
    @Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		if (mc.currentScreen != GuiIngameMenu && (mc.currentScreen != GuiContainer) && !(mc.currentScreen instanceof GuiChat)) {
			mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
        }
	}
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
}
