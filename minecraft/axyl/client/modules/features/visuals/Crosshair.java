package axyl.client.modules.features.visuals; 

import java.awt.*; 
import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.render.EventCrosshair;
import axyl.client.events.render.EventRender2D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class Crosshair extends Module
{
	private Setting width;
	private Setting length;
	private Setting gap;
	private Setting outline;

	public Crosshair() {
        super("Crosshair", "", 0, Category.Visuals);
    }
	
	@Override
	public void moduleSetup() {
        Axyl.ins.settingManager.createSetting(this.gap = new Setting("Gap", this, 3, 0, 10, 0, false));
        Axyl.ins.settingManager.createSetting(this.length = new Setting("Length", this, 5, 1, 10, 0, false));
        Axyl.ins.settingManager.createSetting(this.width = new Setting("Width", this, 1, 0, 10, 0, false));
        Axyl.ins.settingManager.createSetting(this.outline = new Setting("Outline", this, true, false));
	}
	
    @Subscribe
    public void eventCrosshair(EventCrosshair event) {
    	event.setCancelled(true);
    }
    
    @Subscribe
    public void eventRender2D(EventRender2D event) {
        final ScaledResolution sr = new ScaledResolution(mc);
        int color = -1;
        float y = sr.getScaledHeight() / 2;
        float x = sr.getScaledWidth() / 2f;
        

        RenderUtil.drawBorderedRect(x - width.getValDouble()/2, y - gap.getValDouble()-1, width.getValDouble()+1.5, -length.getValDouble(), 0.5, 0xff000000, -1);

        RenderUtil.drawBorderedRect(x - width.getValDouble()/2, y + gap.getValDouble()+2, width.getValDouble()+1.5, length.getValDouble()+1, 0.5, 0xff000000, -1);
        
        RenderUtil.drawBorderedRect(x+gap.getValDouble()+2, y-width.getValDouble()/2, length.getValDouble()+1, 1.5+width.getValDouble(), 0.5, 0xff000000, -1);

        RenderUtil.drawBorderedRect(x-gap.getValDouble()-1, y-width.getValDouble()/2, -length.getValDouble(), 1.5+width.getValDouble(), 0.5, 0xff000000, -1);
	}
}
