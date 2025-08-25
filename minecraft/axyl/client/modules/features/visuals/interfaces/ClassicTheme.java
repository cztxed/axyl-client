package axyl.client.modules.features.visuals.interfaces;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import axyl.client.Axyl;
import axyl.client.events.render.EventRender2D;
import axyl.client.font.Fonts;
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

public class ClassicTheme {
	
	public Minecraft mc = Minecraft.getMinecraft();
	
	public static ClassicTheme cl;
	static {
		cl = new ClassicTheme();
	}
	
	public void renderInterface(EventRender2D event) {
		ScaledResolution sr = new ScaledResolution(mc);
		int mainColor = 0xffff5050;
		int logoColor = new Color(15, 15, 22, 145).getRGB();
		String s = "Axyl§7/§r "+mc.thePlayer.getName() + " §7/§r " + mc.getDebugFPS()+"FPS" + " §7/§r " + "public release 20102024";
		s = s.toLowerCase();
		double wid = Fonts.no_AA_arial_small.getStringWidth(s);
		RenderUtil.drawRect(4, 4, wid+11, 11.5f, logoColor);
		for(double i = 0; i < wid+10.5f; i+=0.5f) {
			RenderUtil.drawRect(4+i, 4, 1, 1, RainbowUtil.getRainbow(3f, 0.6f, 1f, (long) (i*24)));
		}
		Fonts.no_AA_arial_small.drawStringWithShadow(s, 9f, 9.5f, -1);
		
		if(Axyl.ins.modManager.getModuleByName("SessionInfo").isToggled()) {
	    	NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
	        String ping = (mc.isSingleplayer() ? "0ms" : ("" + networkPlayerInfo.getResponseTime() + "ms"));
	    	String serverIP = (mc.isSingleplayer() ? "Singleplayer" : mc.getCurrentServerData().serverIP);
	    	
			RenderUtil.drawBorderedRoundedRect(3, 55, 140, 61, 7, 1, 0x9f101015, 0x9f101015);
			RenderUtil.drawRect(5, 70, 136, 44, 0x9f101015);
			Fonts.roboto_small2.drawStringWithShadow("Session Stats", 7.5f, 61f, -1);
			Fonts.roboto_small2.drawStringWithShadow("Play time:", 7.5f, 75f, -1);
			String r = "Ip: " + serverIP;
			Fonts.roboto_small2.drawStringWithShadow(r, 7.5f, 85f, -1);
			String n = "Name: " + mc.thePlayer.getName();
			Fonts.roboto_small2.drawStringWithShadow(n, 7.5f, 95f, -1);
			String p = "Ping: " + ping;
			Fonts.roboto_small2.drawStringWithShadow(p, 7.5f, 105f, -1);
		}
		
		int posx = (int) Interface.posx.getValDouble();
		int posy = (int) Interface.posy.getValDouble();
		
		int color = 0;
		int offset = 0;
		
		Axyl.ins.modManager.getModules().sort(Comparator.comparingDouble(m -> (Fonts.poppins.getStringWidth(((Module)m).getDisplayNameWithSpaces()))).reversed());
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

			/*if(m.getCategory().equals(Category.Combat)) {
				color = 0xffdb78a3;
			}
			if(m.getCategory().equals(Category.Player)) {
				color = 0xffe0c5f2;
			}
			if(m.getCategory().equals(Category.Exploit)) {
				color = 0xffe0c5f2;
			}
			if(m.getCategory().equals(Category.Movement)) {
				color = 0xff5b99cc;
			}
			if(m.getCategory().equals(Category.Visuals)) {
				color = 0xffffbb91;
			}
			if(m.getCategory().equals(Category.Other)) {
				color = 0xffc4e0f9;
			}*/
			color = Colors.getMixedColor(offset/10, new Color(Interface.interfaceColor.getValColor()), new Color(Interface.interfaceColor2.getValColor()), 2);
			RenderUtil.drawRect(sr.getScaledWidth()-(Fonts.poppins.getStringWidth(m.getDisplayNameWithSpaces())+5)*m.xMultipler+1.5f-posx, offset + posy, Fonts.poppins.getStringWidth(m.getDisplayNameWithSpaces())+3.5f, 11, 0x7f101010);
			Fonts.poppins.drawStringWithShadow(m.getDisplayNameWithSpaces(), (float) (sr.getScaledWidth()-(Fonts.poppins.getStringWidth(m.getDisplayNameWithSpaces())+1.5f)*m.xMultipler) - posx, 2f+offset + posy, color);
			offset+=11*m.yMultipler;
		}
	}
}
