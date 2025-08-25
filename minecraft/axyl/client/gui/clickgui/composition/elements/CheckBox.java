package axyl.client.gui.clickgui.composition.elements;

import net.minecraft.client.Minecraft;   
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.jar.Attributes.Name;

import org.lwjgl.opengl.GL11;

import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;

public class CheckBox extends Composition {

	public int OPACITY = 255;
	public Timer timer;
	
    public CheckBox(double x, double y, CUI parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
        this.timer = new Timer();
        this.OPACITY = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        {
        	if(timer.hasReached(10)) {
        		if(setting.getValBoolean()) {
        			OPACITY+=(255-OPACITY)*0.1;
        		} else {
        			OPACITY+=(20-OPACITY)*0.1;
        		}
        		timer.reset();
        	}
        	OPACITY = (int) MathUtils.clamp(OPACITY, 0, 255);
        }
		int MAIN_COLOR_1 = new Color(235, 235, 255, OPACITY).getRGB();
		int MAIN_COLOR_3 = new Color(255, 255, 255, 255).getRGB();
		//Gui.drawRect(parent.x + x - 70, parent.y + y, parent.x + x + 10 - 70, parent.y + y + 10, new Color(50,50,50).getRGB());
        RenderUtil.drawBorderedRoundedRect(parent.x + x - 70+2.5f, parent.y + y+2.5f, 10, 5, 5, 1, new Color(70,70,70).darker().getRGB(), new Color(70,70,70).darker().getRGB());
        RenderUtil.drawBorderedRoundedRect(parent.x + x - 70+2.5f, parent.y + y+2.5f, 10, 5, 5, 1, MAIN_COLOR_1, MAIN_COLOR_1);
        Fonts.roboto_small.drawStringWithShadow(setting.getName(), (int)(parent.x + x - 54), (int)(parent.y + y + 4.5f), (int) (MAIN_COLOR_3));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.x + x - 70+2.5f, parent.y + y+1.5f, parent.x + x + 2.5f - 70+10, parent.y + y + +2.5f +5) && mouseButton == 0) {
            setting.setValBoolean(!setting.getValBoolean());
        }
    }

}
