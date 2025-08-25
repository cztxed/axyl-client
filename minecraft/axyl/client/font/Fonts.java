package axyl.client.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class Fonts {
	
	public static MCFontRenderer roboto_medium;
	public static MCFontRenderer roboto_medium2;
	public static MCFontRenderer roboto_small;
	public static MCFontRenderer roboto_small2;
	public static MCFontRenderer roboto_micro;
	public static MCFontRenderer roboto_bold;
	public static MCFontRenderer tahoma_bold;
	public static MCFontRenderer no_AA_arial_small;
    public static MCFontRenderer no_AA_arial_Normal;
    public static MCFontRenderer arial_small;
    public static MCFontRenderer arial_normal;
    public static MCFontRenderer icons;
    public static MCFontRenderer icon_works_webfont;
	public static MCFontRenderer esp_font_tahoma;
    public static MCFontRenderer poppins;
	public static MCFontRenderer opensans;
	public static MCFontRenderer opensansbold;
    public static MCFontRenderer worksans;

    public static void loadFonts() {
    	poppins = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Poppins-Regular.ttf"),17,0), true, true);
    	opensans = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/OpenSans-Regular.ttf"),16,0), true, true);
    	opensansbold = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/OpenSans-Bold.ttf"),45,0), true, true);
    	worksans = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/WorkSans-Regular.ttf"),19,0), true, true);
    	icon_works_webfont = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/icon-works-webfont.ttf"), 45, 0), true, true);
    	icons = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Icons.ttf"),15,0), true, true);
    	roboto_medium = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Medium.ttf"),17,0), true, true);
    	roboto_small = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Medium.ttf"),12,0), true, true);
    	roboto_medium2 = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Medium.ttf"),20,0), true, true);
    	roboto_small2 = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Medium.ttf"),15,0), true, true);
    	roboto_micro = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Medium.ttf"),8,0), true, true);
    	roboto_bold = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/Roboto-Bold.ttf"),17,0), true, true);
    	tahoma_bold = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/TahomaBold.ttf"),9,0), false, true);
    	esp_font_tahoma = new MCFontRenderer(fontFromTTF(new ResourceLocation("textures/client/TahomaBold.ttf"),9,0), false, true);
    	no_AA_arial_small = new MCFontRenderer(new Font("Arial", Font.PLAIN,11),false,true);
    	no_AA_arial_Normal = new MCFontRenderer(new Font("Arial", Font.PLAIN,20),false,true);
    	arial_small = new MCFontRenderer(new Font("Arial", Font.PLAIN,11),true,true);
    	arial_normal = new MCFontRenderer(new Font("Arial", Font.PLAIN,18),true,true);
    }
    private static Font fontFromTTF(ResourceLocation fontLocation, float fontSize, int fontType) {
        Font output = null;
        try {
            output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
