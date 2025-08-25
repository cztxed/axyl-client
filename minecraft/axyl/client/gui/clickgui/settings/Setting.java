package axyl.client.gui.clickgui.settings;

import java.util.ArrayList;

import axyl.client.Axyl;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.colors.RainbowUtil;

public class Setting
{
    private String name;
    private Module settingModule;
    private String settingMode;
    private String stringValue;
    private ArrayList<String> options;
    private boolean booleanValue;
    private double doubleValue;
	private float colorValueBrightness;
	private float colorValueSaturation;
    private int colorValueIndex;
    private int colorValue;
    public int index;
    private double min;
    private double max;
    public int rounding;
    public int additionalOffset = 0;
    public int additionalOffsetComp = 0;
    public boolean additionalSpace;
    public Setting(final String name, final Module settingModule, final String stringValue, final ArrayList<String> options) {
        this.settingMode = "combo";
        this.name = name;
        this.settingModule = settingModule;
        this.options = options;
        this.stringValue = stringValue;
		try {
			this.index = (int) Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"String");
		} catch (NullPointerException e){
			this.index = 0;
		}
		setValString(options.get(index));
    }
    
    public Setting(final String name, final Module settingModule, final boolean booleanValue, boolean additionalSpace) {
        this.settingMode = "check";
        this.name = name;
        this.settingModule = settingModule;
        this.additionalSpace = additionalSpace;
		try {
			this.booleanValue = Boolean.valueOf(Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"Boolean").toString());
		} catch (NullPointerException e){
			this.booleanValue = booleanValue;
		}
    }
    
    public Setting(final String name, final Module settingModule, final double doubleValue, final double min, final double max, int rounding, boolean additionalSpace) {
        this.settingMode = "slider";
    	this.rounding = rounding;
        this.name = name;
        this.settingModule = settingModule;
        this.min = min;
        this.max = max;
        this.additionalSpace = additionalSpace;
		try {
			this.doubleValue = Double.valueOf(Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"Double").toString());
		} catch (NullPointerException e){
			this.doubleValue = doubleValue;
		}
    }
    
    public Setting(final String name, final Module settingModule, final int colorValueIndex, final float colorValueBrightness, final float colorValueSaturation) {
        this.settingMode = "color";
        this.name = name;
        this.settingModule = settingModule;
		try {
			this.colorValueIndex = (int) Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"ColorIndex");
			this.colorValueBrightness = (int) Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"ColorBrightness");
			this.colorValueSaturation = (int) Axyl.ins.configurationManager.config.get(settingModule.getName().toLowerCase() + name.toLowerCase()+"ColorSaturation");
		} catch (NullPointerException e){
	        this.colorValueIndex = colorValueIndex;
	        this.colorValueBrightness = colorValueBrightness;
	        this.colorValueSaturation = colorValueSaturation;
		}
        int finalC = RainbowUtil.getStaticRainbow(1, this.colorValueSaturation/100, this.colorValueBrightness/100, (int)this.colorValueIndex*6);
		this.colorValue = finalC;
    }
    
    public Module getSettingModule() {
        return this.settingModule;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<String> getOptions() {
        return this.options;
    }
    
    public String getValString() {
        return this.stringValue;
    }
    
    
    public boolean getValBoolean() {
        return this.booleanValue;
    }
    
    public double getValDouble() {
        return MathUtils.roundToPlace(doubleValue, rounding);
    }
    
    public int getValColorIndex() {
        return this.colorValueIndex;
    }
    
    public float getValColorBrightness() {
        return this.colorValueBrightness;
    }
    
    public float getValColorSaturation() {
        return this.colorValueSaturation;
    }
    
    public int getValColor() {
        return this.colorValue;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public void setValString(final String in) {
        this.stringValue = in;
    }
    
    public void setValBoolean(final boolean in) {
        this.booleanValue = in;
    }
    
    public void setValDouble(final double in) {
        this.doubleValue = in;
    }
    
    public void setValColorIndex(final int in) {
        this.colorValueIndex = in;
    }
    
    public void setValColorBrightness(final float in) {
        this.colorValueBrightness = in;
    }
    
    public void setValColorSaturation(final float in) {
        this.colorValueSaturation = in;
    }
    
    public void setColor(final int in) {
        this.colorValue = in;
    }
    
    public boolean isCombo() {
        return this.settingMode.equalsIgnoreCase("Combo");
    }
    
    public boolean isCheck() {
        return this.settingMode.equalsIgnoreCase("Check");
    }
    
    public boolean isSlider() {
        return this.settingMode.equalsIgnoreCase("Slider");
    }
    
    public boolean isColor() {
        return this.settingMode.equalsIgnoreCase("Color");
    }
}
