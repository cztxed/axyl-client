package axyl.client.util.render.colors;

import java.awt.Color;

public class RainbowUtil
{
	public static int getRainbow(float seconds,float saturation,float brightness,long index) {
		float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1500)) / (float)(seconds * 1500);
	    int color = Color.HSBtoRGB(hue, saturation, brightness);
	    return color;
	}
	
	public static int getStaticRainbow(float seconds,float saturation,float brightness,long index) {
		float hue = ((index) % (int)(seconds * 1500)) / (float)(seconds * 1500);
	    int color = Color.HSBtoRGB(hue, saturation, brightness);
	    return color;
	}
	
    public static int rainbow(long index) {
        double rainbowState = Math.ceil((double)((System.currentTimeMillis() + index) /20L));
        rainbowState %= 100.0;
        return Color.getHSBColor((float)(rainbowState / 100.0), 0.24f, 1.9f).getRGB();
    }
    
    public static int rainbow2() {
        double rainbowState = Math.ceil((double)((System.currentTimeMillis() + 0L) /28L));
        rainbowState %= 100.0;
        return Color.getHSBColor((float)(rainbowState / 100.0), 0.19f, 1.9f).getRGB();
    }
}
