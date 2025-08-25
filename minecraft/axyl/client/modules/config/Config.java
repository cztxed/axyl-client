package axyl.client.modules.config;

import java.io.File; 
import java.io.IOException;

import axyl.client.Axyl;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;

public class Config {

	public File configFolder = new File("Axyl");
	public Configuration config = ConfigurationAPI.newConfiguration(new File("Axyl/Axyl.bin"));
	public Configuration configToSave;
	
	public void saveModConf(String fileToSave) {
		if(!configFolder.exists()) {
			configFolder.mkdirs();
		}
		configToSave = ConfigurationAPI.newConfiguration(new File("Axyl/"+fileToSave));
		
		for(Module m : Axyl.ins.modManager.unfilteredModules) {
			if(!m.getName().equals("ClickGui")) {
				configToSave.set(m.getName().toLowerCase()+" toggled", m.isToggled());
				
				configToSave.set(m.getName().toLowerCase()+" keybind", m.getKey());
			}
			for(Setting s : Axyl.ins.settingManager.getSettings()) {
				if(s.getSettingModule().getName().equals(s.getSettingModule().getName())) {
					if(s.isSlider()) {
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"Double", s.getValDouble());
					}
					if(s.isCheck()) {
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"Boolean", s.getValBoolean());
					}
					if(s.isCombo()) {
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"String", s.index);
					}
					if(s.isColor()) {
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"ColorIndex", s.getValColorIndex());
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"ColorBrightness", s.getValColorBrightness());
						configToSave.set(s.getSettingModule().getName().toLowerCase() + s.getName().toLowerCase()+"ColorSaturation", s.getValColorSaturation());
					}
				}
			}
		}
		try {
			configToSave.save();
		} catch (IOException e) {}
	}
	
	public void loadModConfig(String file) {
		try {
			config = ConfigurationAPI.loadExistingConfiguration(new File("Axyl/"+file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
