package axyl.client.gui.clickgui.composition.elements;

import net.minecraft.client.Minecraft;  
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import axyl.client.Axyl;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.render.colors.RainbowUtil;
import axyl.client.util.time.Timer;

public class ColorBox extends Composition {

	public Timer timer;
	
    private boolean draggingIndex = false;
    private double renderWidthIndex;
    private double renderWidthIndex2;

    private boolean draggingSaturation = false;
    private double renderWidthSaturation;
    private double renderWidthSaturation2;
    
    private boolean draggingBrightness = false;
    private double renderWidthBrightness;
    private double renderWidthBrightness2;
    
    public ColorBox(double x, double y, CUI parent, Module module, Setting setting) {
        this.x = x;
        this.y = y-8;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
        this.timer = new Timer();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        
		int MAIN_COLOR_3 = new Color(235, 235, 255, 255).getRGB();
		int MAIN_COLOR_2 = new Color(116, 116, 129, 255).getRGB();
		int MAIN_COLOR_1 = new Color(86, 86, 96, 255).getRGB();
		
        double l = 90;
        RenderUtil.drawBorderedRect(parent.x + x - 72.5f, parent.y + y + 10, l+12.5f, 30, -0.5, 0xff505060, 0xff202030);
		{
	        double min = 0;
	        double max = 255;
	        
	        double orginalValue = (l) * (setting.getValColorIndex() - min) / (max - min);
	        if(timer.hasReached(10)) {
	        	renderWidthIndex+=(orginalValue-renderWidthIndex)*0.125;
	        }
	        renderWidthIndex2 = (l) * (setting.getValColorIndex() - min) / (max - min);

	        double diff = Math.min(l, Math.max(0, mouseX - (parent.x + x - 70)));
	        if (draggingIndex) {
	        	if(diff == 0) {
	        		setting.setValColorIndex((int) min);
	        	} else {
	        		double newValue = ((diff / l) * (max - min) + min);
	                setting.setValColorIndex((int) newValue);
	        	}
	        }
	        for(int i = 0; i < l; i++) {
				RenderUtil.drawRect(parent.x + x+i-70, parent.y + y+20, 1, 4, RainbowUtil.getStaticRainbow(1, 1f, 1f, i*17));
			}
	        RenderUtil.drawBorderedRect(parent.x + x - 70.5f + renderWidthIndex, parent.y + y + 19, 1, 6, -0.5, 0xff505060, 0xff202030);
	        Fonts.roboto_small.drawStringWithShadow(setting.getName() + ": ",(int)(parent.x + x - 69),(int)(parent.y + y) + 14f, MAIN_COLOR_3);
		}
		{
			double y = this.y + 6.5f;
	        double min = 1;
	        double max = 100;

	        double orginalValue = (l) * (setting.getValColorBrightness() - min) / (max - min);
	        
	        if(timer.hasReached(10)) {
	        	renderWidthBrightness+=(orginalValue-renderWidthBrightness)*0.125;
	        }

	        renderWidthBrightness2 = (l) * (setting.getValColorBrightness() - min) / (max - min);

	        double diff = Math.min(l, Math.max(0, mouseX - (parent.x + x - 70)));
	        if (draggingBrightness) {
	        	if(diff == 0) {
	        		setting.setValColorBrightness((int) min);
	        	} else {
	        		double newValue = ((diff / l) * (max - min) + min);
	                setting.setValColorBrightness((int) newValue);
	        	}
	        }
	        Gui.drawGradientRectSideways(parent.x + x-70, parent.y + y+20, parent.x + x-70 + l, parent.y + y+20 + 4, 0xff000000, setting.getValColor());
	        RenderUtil.drawBorderedRect(parent.x + x - 70.5f + renderWidthBrightness, parent.y + y + 19, 1, 6, -0.5, 0xff505060, 0xff202030);
		}
		{
			double y = this.y + 13;
	        double min = 1;
	        double max = 100;

	        double orginalValue = (l) * (setting.getValColorSaturation() - min) / (max - min);
	        
	        if(timer.hasReached(10)) {
	        	renderWidthSaturation+=(orginalValue-renderWidthSaturation)*0.125;
	        }

	        renderWidthSaturation2 = (l) * (setting.getValColorSaturation() - min) / (max - min);

	        double diff = Math.min(l, Math.max(0, mouseX - (parent.x + x - 70)));
	        if (draggingSaturation) {
	        	if(diff == 0) {
	        		setting.setValColorSaturation((int) min);
	        	} else {
	        		double newValue = ((diff / l) * (max - min) + min);
	                setting.setValColorSaturation((int) newValue);
	        	}
	        }
	        int minS = RainbowUtil.getStaticRainbow(1, 1f, setting.getValColorBrightness()/100, (int)setting.getValColorIndex()*6);
	        int maxS = RainbowUtil.getStaticRainbow(1, 0, setting.getValColorBrightness()/100, (int)setting.getValColorIndex()*6);
	        Gui.drawGradientRectSideways(parent.x + x-70, parent.y + y+20, parent.x + x-70 + l, parent.y + y+20 + 4, maxS, minS);
	        RenderUtil.drawBorderedRect(parent.x + x - 70.5f + renderWidthSaturation, parent.y + y + 19, 1, 6, -0.5, 0xff505060, 0xff202030);
		}
		if(timer.hasReached(10))
			timer.reset();
		
        int finalC = RainbowUtil.getStaticRainbow(1, setting.getValColorSaturation()/100, setting.getValColorBrightness()/100, (int)setting.getValColorIndex()*6);
        setting.setColor(finalC);
        RenderUtil.drawRect(parent.x + x-68+l, parent.y + y+20, 6f, 17f, setting.getValColor());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        {
        	if (isInside(mouseX, mouseY, parent.x + x - 70, parent.y + y + 19, parent.x + x - 70 + 96, parent.y + y + 24) && mouseButton == 0) {
                draggingIndex = true;
            }
        }
        {
        	double y = this.y+6.5;
            if (isInside(mouseX, mouseY, parent.x + x - 70, parent.y + y + 20, parent.x + x - 70 + 96, parent.y + y + 24) && mouseButton == 0) {
                draggingBrightness = true;
            }
        }
        {
        	double y = this.y+13;
            if (isInside(mouseX, mouseY, parent.x + x - 70, parent.y + y + 20, parent.x + x - 70 + 96, parent.y + y + 24) && mouseButton == 0) {
                draggingSaturation = true;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        draggingIndex = false;
        draggingBrightness = false;
        draggingSaturation = false;
    }
}
