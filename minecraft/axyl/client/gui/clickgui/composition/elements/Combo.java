package axyl.client.gui.clickgui.composition.elements;

import net.minecraft.client.Minecraft; 
import net.minecraft.client.gui.Gui;

import java.awt.*;

import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;

public class Combo extends Composition {

	public Timer timer;
	public float textWid = 0;
	
    public Combo(double x, double y, CUI parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
        this.timer = new Timer();
        setting.additionalOffset = 0;
        for(String o : setting.getOptions()) {
        	setting.additionalOffset+=8;
        }
        int offset = 0;
        int offsetY = 0;
        for(String o : setting.getOptions()) {
        	if(offset >= 70) {
        		offsetY+=10;
        		offset-=offset;
        		setting.additionalOffsetComp = offsetY;
        	}
            offset+=Fonts.roboto_small.getStringWidth(o)+5;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int offsetY = 0;
        int offset = 0;
        int index = 0;
        for(String o : setting.getOptions()) {
        	if(offset >= 70) {
        		offsetY+=10;
        		offset-=offset;
        		setting.additionalOffsetComp = offsetY;
        	}
            if(isInside(mouseX, mouseY, (int)(parent.x + x - 69)-1+offset, (int)(parent.y + y+offsetY + 2)-3, (int)(parent.x + x - 67)-1+Fonts.roboto_small.getStringWidth(o)+2+offset, (int)(parent.y + y+offsetY + 2)-3+8)) {
            	setting.setValString(o);
            	setting.index = index;
            }
            offset+=Fonts.roboto_small.getStringWidth(o)+5;
            index++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        {
        	if(timer.hasReached(10)) {
        		textWid += (Fonts.roboto_small.getStringWidth(setting.getValString())-textWid)*0.25;
        		timer.reset();
        	}
        }
		int MAIN_COLOR_1 = new Color(235, 235, 255, 255).getRGB();
		int MAIN_COLOR_2 = new Color(235, 235, 255, 255).getRGB();
		
        int offset = 0;
        int offsetY = 0;
        int colorFix = 0;
        Fonts.roboto_small.drawStringWithShadow(setting.getName()+" :", (int)(parent.x + x - 69), (int)(parent.y + y + 2-8), MAIN_COLOR_1);
        for(String o : setting.getOptions()) {
        	if(offset >= 70) {
        		offsetY+=10;
        		offset-=offset;
        		setting.additionalOffsetComp = offsetY;
        	}
            if(setting.getValString().equals(o)) {
            	Fonts.roboto_small.drawStringWithShadow(o, (int)(parent.x + x - 69)+offset, (int)(parent.y + y + 3)+offsetY, MAIN_COLOR_2);
            } else {
            	Fonts.roboto_small.drawStringWithShadow(o, (int)(parent.x + x - 69)+offset, (int)(parent.y + y + 3)+offsetY, new Color(129, 126, 126, 255-colorFix).getRGB());
            }
            offset+=Fonts.roboto_small.getStringWidth(o)+5;
            colorFix+=1;
        }
    }
}
