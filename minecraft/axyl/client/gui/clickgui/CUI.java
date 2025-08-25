package axyl.client.gui.clickgui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Ordering;

import axyl.client.Axyl;
import axyl.client.events.render.EventInClickGui;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.composition.Composition;
import axyl.client.gui.clickgui.composition.elements.CheckBox;
import axyl.client.gui.clickgui.composition.elements.Combo;
import axyl.client.gui.clickgui.composition.elements.Slider;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.gui.notifications.NotiType;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.config.Configuration;
import axyl.client.modules.config.ConfigurationAPI;
import axyl.client.modules.features.combat.KillAura;
import axyl.client.modules.features.visuals.ClickGui;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.render.colors.RainbowUtil;
import axyl.client.util.time.Timer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextFieldCui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

public class CUI extends GuiScreen {
	
	public Category draggedCategory = null;
	public GuiTextFieldCui configName = null;
	
	public int lastX;
	public int lastY;
	
	public double x;
	public double y;
	
	public double w;
	public double h;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		EventInClickGui clickGui = new EventInClickGui();
    	clickGui.hook(clickGui);
    	
    	ScaledResolution sr = new ScaledResolution(mc);
    	RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(15, 15, 25, 255).getRGB()+0x2f000000);
    	
		x = 20;
		y = 200;
		w = 200;
		h = 200;
        { // Category //
			int offset = 0;
	        Category[] values;
            Frame frame = new Frame();
	        for (int length = (values = Category.values()).length, i = 0; i < length; ++i) {
	            final Category c = values[i];
	            if(c.equals(draggedCategory)) {
	            	c.x = mouseX - lastX;
	              	c.y = mouseY - lastY;
	            }
	            if(ClickGui.setClickGui) {
	            	c.x = 220+offset;
	            	c.y = 100;
	            }
	            frame.drawFrame(mouseX, mouseY, c.x, c.y, c);
	            offset+=130;
	        }
        }
		for(Composition c : ClickGui.composition) {
			c.parent.x = c.module.settingX+150;
			c.parent.y = c.module.settingY-5;
			c.drawScreen(mouseX, mouseY);
		}
		configName.drawTextBox();
		int main_color = new Color(15, 15, 25, 255).getRGB();
		int main_color2 = new Color(35, 35, 45, 255).getRGB();
		{
	    	String buttons[] = {
	        	"Load", "Create", "Save", "Delete"
	        };
	        int offset = 0;
	        for(String s : buttons) {
	        	RenderUtil.drawBorderedRoundedRect(112+offset, 3, Fonts.roboto_medium.getStringWidth(s)+10, 17f, 6, 1, main_color+0x9f000000, main_color+0x9f000000);
	        	if(mouseX >= 112+offset && mouseY >= 3 && mouseX < 112+offset+Fonts.roboto_medium.getStringWidth(s)+10 && mouseY <= 3+17)
	            	RenderUtil.drawBorderedRoundedRect(112+offset, 3, Fonts.roboto_medium.getStringWidth(s)+10, 17f, 6, 1, main_color+0x5f000000, main_color+0x5f000000);
	        	Fonts.roboto_medium.drawString(s, 111+offset+6f, 9.5f, -1);
	        	offset+=Fonts.roboto_medium.getStringWidth(s)+13f;
	        }
		}
		{
	    	String checkBoxes[] = {
	        	"Keybinds", "Visuals"
	    	};
	    	int offset = 0;
	        for(String s : checkBoxes) {
	        	RenderUtil.drawBorderedRoundedRect(111, 23+offset, 35+21, 15.5, 6, 1, main_color+0x9f000000, main_color+0x9f000000);
	        	RenderUtil.drawBorderedRoundedRect(120+35, 26+offset, 10, 10, 6, 1, main_color+0x9f000000, main_color+0x9f000000);
	        	boolean h = mouseX >= 120+35 && mouseY >= 26+offset && mouseX < 120+35+10 && mouseY <= 36+offset;
	        	if(h) {
	        		RenderUtil.drawBorderedRoundedRect(120+35, 26+offset, 10, 10, 6, 1, -1+0x5f000000, -1+0x1f000000);
	        	}
	       		if(s.equalsIgnoreCase("Keybinds")) {
        			if(ClickGui.loadKeybinds) {
    	        		RenderUtil.drawBorderedRoundedRect(120+35, 26+offset, 10, 10, 6, 1, -1+0x5f000000, -1+0x4f000000);
        			}
        		}
	    		if(s.equalsIgnoreCase("Visuals")) {
        			if(ClickGui.loadVisuals) {
    	        		RenderUtil.drawBorderedRoundedRect(120+35, 26+offset, 10, 10, 6, 1, -1+0x5f000000, -1+0x4f000000);
        			}
        		}
	        	Fonts.roboto_small.drawString(s, 117f, 29.5f+offset, -1);
	        	offset+=18f;
	        }
		}
        List<String> files;
        int offset2 = 0;
		try {
			files = findFiles("Axyl", "bin");
       		RenderUtil.drawBorderedRoundedRect(3, 23, 105, 5f + (10*files.size()), 6, 2, main_color+0x9f000000, main_color+0x9f000000);
	        for (String file : files) {
	    		if(file.equals(Axyl.configToLoad)) {
	    			Fonts.roboto_small.drawString(file + " (Selected)", 10, 29.5f+offset2, -1);
	    		} else {
	    			Fonts.roboto_small.drawString(file, 10, 29.5f+offset2, -1);
	    		}
	        	offset2+=10;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*{
	        int o = 0;
	        if(GuiPlayerTabOverlay.scoreboardEntityList != null) 
	        for (NetworkPlayerInfo networkplayerinfo : GuiPlayerTabOverlay.scoreboardEntityList) {
				
	        	Fonts.roboto_small.drawStringWithShadow(networkplayerinfo.getDisplayName().getUnformattedText(), 5, 70+o, -1);
				o+=10;
	        }
	        
		}*/
    	ClickGui.setClickGui = false;
	}
	
	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		configName = new GuiTextFieldCui(5, 5, 5, 100, 13);
		super.initGui();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		ScaledResolution sr = new ScaledResolution(mc);
		configName.mouseClicked(mouseX, mouseY, mouseButton);
		for(Composition c : ClickGui.composition) {
			c.mouseClicked(mouseX, mouseY, mouseButton);
		}
		{	
			{
		    	String buttons[] = {
			        "Load", "Create", "Save", "Delete"
			    };
		    	int offset = 0;
		    	for(String s : buttons) {
		    		if(mouseButton == 0)
		    		if(mouseX >= 112+offset && mouseY >= 3 && mouseX < 112+offset+Fonts.roboto_medium.getStringWidth(s)+10 && mouseY <= 3+17) {
		    			if(s.equals("Delete")) {
		    		        try {
			    				String file = "Axyl/"+Axyl.configToLoad;
		    		            Files.delete(Paths.get(file));
		    		            Axyl.ins.notificationManager.addNotification(3000, NotiType.Neutral, "Deleted " + Axyl.configToLoad);
		    		        } catch (IOException e) {
		    		            e.printStackTrace();
		    		        }
		    			}
		    			if(s.equals("Load")) {
		    				Axyl.ins.notificationManager.addNotification(3000, NotiType.Neutral, "Loaded " + Axyl.configToLoad);
		    				Axyl.ins.configurationManager.config = ConfigurationAPI.loadExistingConfiguration(new File("Axyl/"+Axyl.configToLoad));;
		    				for(Module m : Axyl.ins.modManager.unfilteredModules) {
		    					if(ClickGui.loadVisuals || (!ClickGui.loadVisuals && !m.getCategory().equals(Category.Visuals)))
		    					if(!m.getName().equals("ClickGui")) {
			    					if(m.isToggled())
										m.toggle();
		 							
			    					if(ClickGui.loadKeybinds) {
			    						m.key = 0;
			    					}
									try {
			 							if((boolean) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + " toggled")) {
											m.toggle();
										}
			 							if(ClickGui.loadKeybinds) {
				    						m.key = (int) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + " keybind");
				    					}
										for(Setting set : Axyl.ins.settingManager.getSettings()) {
											if(set.getSettingModule().equals(m)) {
												if(set.isCombo()) {
													set.index = (int) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + set.getName().toLowerCase()+"String");
													set.setValString(set.getOptions().get(set.index));
												}
												if(set.isCheck()) {
													set.setValBoolean(Boolean.valueOf(Axyl.ins.configurationManager.config.get(set.getSettingModule().getName().toLowerCase() + set.getName().toLowerCase()+"Boolean").toString()));
												}
												if(set.isSlider()) {
													set.setValDouble(Double.valueOf(Axyl.ins.configurationManager.config.get(set.getSettingModule().getName().toLowerCase() + set.getName().toLowerCase()+"Double").toString()));
												}
												if(set.isColor()) {
													set.setValColorIndex((int) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + set.getName().toLowerCase()+"ColorIndex"));
													set.setValColorIndex((int) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + set.getName().toLowerCase()+"ColorBrightness"));
													set.setValColorIndex((int) Axyl.ins.configurationManager.config.get(m.getName().toLowerCase() + set.getName().toLowerCase()+"ColorSaturation"));
												}
											}
										}
									} catch (NullPointerException e){}
		    					}
		    				}
		    			}
		    			if(s.equals("Create")) {
	    					Axyl.ins.configurationManager.saveModConf(configName.getText()+".bin");
	    					Axyl.ins.notificationManager.addNotification(3000, NotiType.Neutral, "Created " + (configName.getText()+".bin"));
		    			}
		    			if(s.equals("Save")) {
		    				Axyl.ins.configurationManager.saveModConf(Axyl.configToLoad);
	    					Axyl.ins.notificationManager.addNotification(3000, NotiType.Neutral, "Saved " + Axyl.configToLoad);
		    			}
		    		}
		    		offset+=Fonts.roboto_medium.getStringWidth(s)+13f;
		    	}
			}
			{
		    	String checkBoxes[] = {
		        	"Keybinds", "Visuals"
		    	};
		    	int offset = 0;
		        for(String s : checkBoxes) {
		        	boolean h = mouseX >= 120+35 && mouseY >= 26+offset && mouseX < 120+35+10 && mouseY <= 36+offset;
		        	if(mouseButton == 0) {
			        	if(h) {
				       		if(s.equalsIgnoreCase("Keybinds")) {
			        			ClickGui.loadKeybinds = !ClickGui.loadKeybinds;
			        		}
				    		if(s.equalsIgnoreCase("Visuals")) {
			        			ClickGui.loadVisuals = !ClickGui.loadVisuals;
			        		}
			        	}
		        	}
		        	offset+=18f;
		        }
			}
	        List<String> files;
	        int offset2 = 0;
			try {
				files = findFiles("Axyl", "bin");
		        for (String file : files) {
		        	if(mouseX >= 10 && mouseY >= 25.5f+offset2 && mouseX < 115 && mouseY <= 29.5f+offset2+Fonts.roboto_small.getHeight()+1) {
		        		if(mouseButton == 0)
		        		Axyl.configToLoad = file;
		        	}
		        	offset2+=10;
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		{ // Category //
			int offset = 0;
	        Category[] values;
            Frame frame = new Frame();
	        for (int length = (values = Category.values()).length, i = 0; i < length; ++i) {
	            final Category c = values[i];
	            if(mouseX > c.x && mouseY > c.y && mouseX < c.x+110 && mouseY < c.y +12) {
		    		if(mouseButton == 0){
		    			draggedCategory = c;
		    			lastX = mouseX - c.x;
		    			lastY = mouseY - c.y;;
		    		}
	            }
	            frame.mouseClicked(mouseX, mouseY, mouseButton, c.x, c.y, c);	
	            offset+=110;
	        }
        }
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(state == 0) {
			draggedCategory = null;
		}
		for(Composition c : ClickGui.composition) {
			c.mouseReleased(mouseX, mouseY, state);
		}
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(ClickGui.bind != null) {
			for(Module m : Axyl.ins.modManager.unfilteredModules) {
				if(m.equals(ClickGui.bind)) {
					if(keyCode == Keyboard.KEY_ESCAPE) {
						m.key = Keyboard.KEY_NONE;
					} else {
						m.key = keyCode;
					}
					ClickGui.bind = null;
				}
			}
		} else {
			configName.textboxKeyTyped(typedChar, keyCode);
			super.keyTyped(typedChar, keyCode);
		}
	}
	
    public List<String> findFiles(String folderPath, String extension) throws IOException {
        Path path = Paths.get(folderPath);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Invalid direction");
        }
        List<String> r = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(path, 1)) {
        	String ex[] = {extension};
            r = walk
            .filter(p -> !Files.isDirectory(p))
            .map(p -> p.getFileName().toString().toLowerCase())
            .filter(f -> Arrays.stream(ex).anyMatch(f::endsWith))     
            .collect(Collectors.toList());
        }
        return r;
    }
}
