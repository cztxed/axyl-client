package axyl.client.modules.features.visuals.interfaces;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import axyl.client.Axyl;
import axyl.client.events.render.EventRender2D;
import axyl.client.font.Fonts;
import axyl.client.font.MCFontRenderer;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.visuals.Interface;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.render.colors.Colors;
import axyl.client.util.render.colors.RainbowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

public class AxylTheme {
	
	public Minecraft mc = Minecraft.getMinecraft();
	
	public static AxylTheme cl;
	static {
		cl = new AxylTheme();
	}
	
	public void renderInterface(EventRender2D event) {
 		ScaledResolution sr = new ScaledResolution(mc);
		int mainColor = 0xffff5050;
		MCFontRenderer font = Fonts.opensans;
		String s = "legit §7<" + "§f1.8§7x>/<§f" + mc.getDebugFPS()+"FPS§7>/<§f"+mc.thePlayer.getName()+"§7>";
		int cl = 0;
		cl = Colors.getMixedColor(1/10, new Color(Interface.interfaceColor.getValColor()), new Color(Interface.interfaceColor2.getValColor()), 2);
		RenderUtil.drawBorderedRoundedRect(3, 3, mc.fontRendererObj.getStringWidth(s)+1, 11, 6, 1, 0x9f101015, 0x9f101015);
		mc.fontRendererObj.drawStringWithShadow(s, 4.5f, 4.5f, cl);
		
		if(Axyl.ins.modManager.getModuleByName("SessionInfo").isToggled()) {
	    	NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
	        String ping = (mc.isSingleplayer() ? "0ms" : ("" + networkPlayerInfo.getResponseTime() + "ms"));
	    	String serverIP = (mc.isSingleplayer() ? "Singleplayer" : mc.getCurrentServerData().serverIP);
	    	
			RenderUtil.drawBorderedRoundedRect(3, 55, 140, 61, 7, 1, 0x9f101015, 0x9f101015);
			RenderUtil.drawRect(5, 70, 136, 44, 0x9f101015);
			mc.fontRendererObj.drawStringWithShadow("Session Stats", 7.5f, 59f, -1);
			mc.fontRendererObj.drawStringWithShadow("Play time:", 7.5f, 73f, -1);
			String r = "Ip: " + serverIP;
			mc.fontRendererObj.drawStringWithShadow(r, 7.5f, 84f, -1);
			String n = "Name: " + mc.thePlayer.getName();
			mc.fontRendererObj.drawStringWithShadow(n, 7.5f, 94f, -1);
			String p = "Ping: " + ping;
			mc.fontRendererObj.drawStringWithShadow(p, 7.5f, 104f, -1);
		}
		
		int posx = (int) Interface.posx.getValDouble();
		int posy = (int) Interface.posy.getValDouble();
		
		int offset = 0;
		Axyl.ins.modManager.getModules().sort(Comparator.comparingDouble(m -> (mc.fontRendererObj.getStringWidth(((Module)m).getDisplayNameWithSpaces()))).reversed());
		for(Module m : Axyl.ins.modManager.getModules()) {
			if(m.getName().equals("ClickGui") || m.getCategory().equals(Category.Visuals))
				continue;
			
			
			if(m.isToggled()) {
				if(m.xMultipler < 1) {
					m.xMultipler+=(1-m.xMultipler)*0.055+0.001;
				} 
				if(m.yMultipler < 1) {
					m.yMultipler+=(1-m.yMultipler)*0.055+0.001;
				}
			} else {
				if(m.xMultipler > 0) {
					m.xMultipler+=(0-m.xMultipler)*0.055-0.001;
				} 
				if(m.yMultipler > 0) {
					m.yMultipler+=(0-m.yMultipler)*0.055-0.001;
				}
			}
			
			m.xMultipler = (float) MathUtils.clamp(m.xMultipler, 0, 1);
			m.yMultipler = (float) MathUtils.clamp(m.yMultipler, 0, 1);
			
			if(m.xMultipler <= 0)
				continue;
			
			int color = 0;
			color = Colors.getMixedColor(offset/10, new Color(Interface.interfaceColor.getValColor()), new Color(Interface.interfaceColor2.getValColor()), 2);
			RenderUtil.drawRect(sr.getScaledWidth()-(mc.fontRendererObj.getStringWidth(m.getDisplayNameWithSpaces())+5)*m.xMultipler+1.5f-posx, offset + posy, mc.fontRendererObj.getStringWidth(m.getDisplayNameWithSpaces())+3.5f, 11, 0x7f101010);
			mc.fontRendererObj.drawStringWithShadow(m.getDisplayNameWithSpaces(), (float) (sr.getScaledWidth()-(mc.fontRendererObj.getStringWidth(m.getDisplayNameWithSpaces())+1.5f)*m.xMultipler) - posx, 2f+offset + posy, color);
			offset+=11*m.yMultipler;
		}
	}
}
