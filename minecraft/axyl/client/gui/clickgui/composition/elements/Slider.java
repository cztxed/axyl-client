package axyl.client.gui.clickgui.composition.elements;

import net.minecraft.client.Minecraft;  
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;

public class Slider extends Composition {

	public Timer timer;
    private boolean dragging = false;
    private double renderWidth;
    private double renderWidth2;

    public Slider(double x, double y, CUI parent, Module module, Setting setting) {
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
        
		int MAIN_COLOR_3 = new Color(255, 255, 255, 255).getRGB();
		int MAIN_COLOR_2 = new Color(116, 116, 129, 255).getRGB();
		int MAIN_COLOR_1 = new Color(86, 86, 96, 255).getRGB();
		
        double min = setting.getMin();
        double max = setting.getMax();
        double l = 96;
        
        double orginalValue = (l) * (setting.getValDouble() - min) / (max - min);
        if(timer.hasReached(10)) {
        	renderWidth+=(orginalValue-renderWidth)*0.125;
        	timer.reset();
        }

        renderWidth2 = (l) * (setting.getMax() - min) / (max - min);

        double diff = Math.min(l, Math.max(0, mouseX - (parent.x + x - 70)));
        if (dragging) {
            if (diff == 0) {
                setting.setValDouble(setting.getMin());
            }
            else {
                double newValue = ((diff / l) * (max - min) + min);
                setting.setValDouble(newValue);
            }
        }
        //Gui.drawRect(parent.x + x - 70, parent.y + y + 10,parent.x + x - 70 + renderWidth2, parent.y + y + 12, new Color(70,70,70).darker().getRGB());
        RenderUtil.drawBorderedRect(parent.x + x - 70, parent.y + y + 10, renderWidth2, 6, -0.5f, MAIN_COLOR_1, MAIN_COLOR_1);
        RenderUtil.drawBorderedRect(parent.x + x - 70, parent.y + y + 10, renderWidth, 6, -0.5f, MAIN_COLOR_1, MAIN_COLOR_1);
        RenderUtil.drawBorderedRect(parent.x + x - 70 + renderWidth, parent.y + y + 10, 3, 6, -0.5, MAIN_COLOR_2, MAIN_COLOR_2);
        Fonts.roboto_small.drawStringWithShadow(setting.getName() + ": " + setting.getValDouble(),(int)(parent.x + x - 68),(int)(parent.y + y) + 12.5f, MAIN_COLOR_3);
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.x + x - 70, parent.y + y + 8,parent.x + x - 70 + renderWidth2, parent.y + y + 18) && mouseButton == 0) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }
}
