package axyl.client.gui.clickgui;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import axyl.client.Axyl;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.composition.elements.CheckBox;
import axyl.client.gui.clickgui.composition.elements.ColorBox;
import axyl.client.gui.clickgui.composition.elements.Combo;
import axyl.client.gui.clickgui.composition.elements.Slider;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.visuals.ClickGui;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;

public class Frame {
	
	public Minecraft mc = Minecraft.getMinecraft();

	public void drawFrame(int mouseX, int mouseY, double x, double y, Category category) {
		int main_color = new Color(15, 15, 25, 230).getRGB();
		int module_color_bg = new Color(28, 28, 38, 230).getRGB();
		int module_color_text = new Color(255, 255, 255, 180).getRGB();
		int settings_color_bg = new Color(16, 16, 26, 230).getRGB();
		
		int line_color = new Color(125, 124, 135, 255).getRGB();
		int line_color2 = new Color(175, 175, 235, 255).getRGB();
		
		int category_color = new Color(255, 255, 255, 190).getRGB();
		
		int width = 110;
		int size = 15;
		
		for(Module m : Axyl.ins.modManager.unfilteredModules) {
			if(m.getCategory().equals(category)) {
				size+=20;
			}
		}
		int height = size;
		RenderUtil.drawBorderedRoundedRect(x, y, width, 13, 1, 2, main_color, main_color);
		Gui.drawGradientRectSideways(x-0.5f, y+12.5f, x+width/2+0.5f, y+12.5f+0.5f, 0, line_color2);
		Gui.drawGradientRectSideways(x-0.5f+width/2, y+12.5f, x+width+0.5f, y+12.5f+0.5f, line_color2, 0);
		Fonts.roboto_small.drawString(category.name(), x+5f, y+5.5f, category_color);
		
		int offset = 0;
		for(Module m : Axyl.ins.modManager.unfilteredModules) {
			if(m.getCategory().equals(category)) {
				RenderUtil.drawRect(x-0.5f, y+13.5f+offset, width+1, 17, module_color_bg);
				if(!m.isToggled()) {
					m.guiOpacity+=(0-m.guiOpacity)*0.01f;
				} else {
					m.guiOpacity+=(45-m.guiOpacity)*0.1f;
				}
				m.guiOpacity = (int) MathUtils.clamp(m.guiOpacity, 0, 35);
				
				RenderUtil.drawRect(x-0.5f, y+13.5f+offset, width+1, 17, new Color(125, 124, 135, m.guiOpacity).getRGB());
				m.expanded = m.equals(ClickGui.setting);
				if(m.expanded) {
					m.settingX = x+1.5f;
					m.settingY = y+15.5f+offset;
					RenderUtil.drawRect(m.settingX-2, m.settingY+15, width+1, m.settingOffset, settings_color_bg);
				} else {
					m.settingOffset = 0;
					m.settingX = -1000;
					m.settingY = -1000;
				}
				if(m.equals(ClickGui.bind)) {
					Fonts.roboto_small.drawString(m.getName() + " (Waiting...)", x+5, y+21f+offset, module_color_text);
				} else {
					String s = "";
					if(!Keyboard.getKeyName(m.getKey()).equals("NONE"))
					s = " ("+Keyboard.getKeyName(m.getKey())+")";
					Fonts.roboto_small.drawString(m.getName() + s, x+5, y+21f+offset, module_color_text);
				}
				if(mouseX >= x-0.5f && mouseY > y+13.5f+offset && mouseX <= x-0.5f+width+1 && mouseY <= y+13.5f+offset+17) {
					m.guiDescOpacity+=(255-m.guiDescOpacity)*0.04f;
					if(!m.getDesc().equals("")) {
						int text = new Color(255, 255, 255, m.guiDescOpacity).getRGB();
						int bg = new Color(16, 16, 26, m.guiDescOpacity).getRGB();
						GlStateManager.pushMatrix();

						RenderUtil.drawRoundedRect(mouseX+8, mouseY-11, Fonts.roboto_small.getStringWidth(m.getDesc())+5, 10, 3, bg);
						Fonts.roboto_small.drawString(m.getDesc(), mouseX+10, mouseY-7, text);
						GlStateManager.popMatrix();
					}
				} else {
					m.guiDescOpacity = 0;
				}
				offset+=17f+m.settingOffset;
			}
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton, double x, double y, Category category) {
		int width = 110;
		int height = 250;
		
		int offset = 0;
		for(Module m : Axyl.ins.modManager.unfilteredModules) {
			if(m.getCategory().equals(category)) {
				if(mouseX >= x-0.5f && mouseY > y+13.5f+offset && mouseX <= x-0.5f+width+1 && mouseY <= y+13.5f+offset+17) {
					if(mouseButton == 2) {
						ClickGui.bind = m;
					}
					if(mouseButton == 0) {
						m.toggle();
					}
					if(mouseButton == 1) {
						if(m.equals(ClickGui.setting)) {
							ClickGui.setting = null;
						} else {
							ClickGui.setting = m;
							ClickGui.composition.clear();
					        int settingOffset = 0;
					        if (Axyl.ins.settingManager.getSettingsByMod(ClickGui.setting) != null) {
						        for (Setting setting : Axyl.ins.settingManager.getSettingsByMod(ClickGui.setting)) {
									float y2 = 25;
						        	if (setting.isCombo()) {
						        		ClickGui.composition.add(new Combo(-75, settingOffset+y2+10, new CUI(), ClickGui.setting, setting));
						        		settingOffset+=setting.additionalOffsetComp;
						        		settingOffset += 20f;
						        	}
						            if (setting.isCheck()) {
						            	ClickGui.composition.add(new CheckBox(-75, settingOffset+y2, new CUI(), ClickGui.setting, setting));
						            	settingOffset += 10f;
						        		if(setting.additionalSpace)
						        			settingOffset+=15;
						            }
						            if (setting.isSlider()) {
						            	ClickGui.composition.add(new Slider(-75, settingOffset+y2, new CUI(), ClickGui.setting, setting));
						            	settingOffset += 10f;
						        		if(setting.additionalSpace)
						        			settingOffset+=5;
						            }
						            if (setting.isColor()) {
						            	ClickGui.composition.add(new ColorBox(-75, settingOffset+y2, new CUI(), ClickGui.setting, setting));
						            	settingOffset += 32f;
						            }
						        }
						        m.settingOffset = settingOffset+10;
					        }
					        
						}
					}
				}
				offset+=17f + m.settingOffset;
			}
		}
	}
}
