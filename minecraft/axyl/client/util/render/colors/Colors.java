package axyl.client.util.render.colors;

import java.awt.Color;

public class Colors
{
	public static int getRainbow(float seconds,float saturation,float brightness,long index) {
		float hue = ((System.currentTimeMillis()*2 + index) % (int)(seconds * 1500)) / (float)(seconds * 1500);
	    int color = Color.HSBtoRGB(hue, saturation, brightness);
	    return color;
	}
	
	public static int getStaticRainbow(float seconds,float saturation,float brightness,long index) {
		float hue = ((index) % (int)(seconds * 1500)) / (float)(seconds * 1500);
	    int color = Color.HSBtoRGB(hue, saturation, brightness);
	    return color;
	}
	
	public static int getAstolfoColor(final int var2, final float bright, final float st) {
        double v1 = Math.ceil((double)(System.currentTimeMillis() + var2 * 120)) / 13.0;
        return Color.getHSBColor(((float)((v1 %= 360.0) / 360.0) < 0.5) ? (-(float)(v1 / 360.0)) : ((float)(v1 / 360.0)), st, bright).getRGB();
    }
	
    public static int getMixedColor(int colorOffset, Color color1, Color color2, float time) {
        float colorOffsetMultiplier = 1f;

        colorOffset *= colorOffsetMultiplier;

        final double timer = (System.currentTimeMillis() / 1E+8 * time) * 4E+5;

        final double factor = (Math.sin(timer + colorOffset * 0.55f) + 1) * 0.5f;
        
        return mixColors(color1, color2, factor).getRGB();
    }
    
    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        double p = 1.0 - percent;
        int red = (int) (color1.getRed() * percent + color2.getRed() * p);
        int green = (int) (color1.getGreen() * percent + color2.getGreen() * p);
        int blue = (int) (color1.getBlue() * percent + color2.getBlue() * p);
        return new Color(red, green, blue);
    }
}
