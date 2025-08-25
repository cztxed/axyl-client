package axyl.client.modules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import axyl.client.Axyl;
import axyl.client.gui.notifications.NotiType;
import axyl.client.util.time.Timer;
import net.minecraft.client.Minecraft;

public class Module {

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public Category category;
	public String name;
	public String desc;
	public String suffix;
	public int key;
	
	public boolean toggled = false;
	public boolean enabled;
	
	// ArrayList stuff
	public Timer timer;
	public float xMultipler;
	public float yMultipler;
	
	// ClickGui stuff
	public int guiOpacity;
	public int guiDescOpacity;
	public boolean expanded;
	
	public double settingX;
	public double settingY;
	public double settingOffset;

	public int ticksExisted;
	
	public Module(String name, String desc, int key, Category category) {
		this.name = name;
		this.desc = desc;
		this.key = key;
		this.category = category;
		this.guiOpacity = 26;
		this.guiDescOpacity = 26;
		this.xMultipler = 0;
		this.yMultipler = 0;
		this.timer = new Timer();
		this.moduleSetup();
		this.suffix = "";
		this.ticksExisted = 0;
		try {
			this.key = (int) Axyl.ins.configurationManager.config.get(name.toLowerCase() + " keybind");
			if((boolean) Axyl.ins.configurationManager.config.get(name.toLowerCase() + " toggled")) {
				Axyl.ins.eventManager.register(this);
				this.toggle();
			}
		} catch (NullPointerException e){

		}
	}
	
	public Category getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		if(this.suffix == "") {
			return this.name;
		} else {
			return this.name + "§7 " + this.suffix;
		}
	}
	
	public String getDisplayNameWithSpaces() {
		Pattern pattern = Pattern.compile("(?<=[a-z])(?=[A-Z])");
        Matcher matcher = pattern.matcher(getDisplayName());
        return matcher.replaceAll(" ");
	}

	public String getDesc() {
		return desc;
	}

	public int getKey() {
		return key;
	}
    
    public void onEnable() {
    	enabled = true;
		if(!getName().equals("ClickGui"))
		if(mc.theWorld != null)
		if(Axyl.ins.notificationManager != null)
		Axyl.ins.notificationManager.addNotification(2250, NotiType.Neutral, "Enabled " + getName());
        Axyl.ins.eventManager.register(this);
    }
    
    public void onDisable() {
        enabled = false;
        Axyl.ins.eventManager.unregister(this);
    }
    
	public void toggle() {
		if(toggled) {
			toggled = false;
			this.onDisable();
		} else {
			toggled = true;
			this.onEnable();
		}
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;;
		if(toggled) {
			if(!this.toggled) {
				toggle();
			}
		} else {
			if(this.toggled) {
				toggle();
			}
		}
	}
	
	public boolean isToggled() {
		return toggled;
	}
	
	public void moduleSetup() {
		
	}
}
