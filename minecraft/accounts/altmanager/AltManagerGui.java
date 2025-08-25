package accounts.altmanager;

import net.minecraft.client.gui.Gui; 
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import accounts.altmanager.microsoft.GuiLoginMicrosoft;
import accounts.altmanager.mojang.GuiLoginMojang;
import axyl.client.font.Fonts;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.render.colors.RainbowUtil;

import java.awt.*;

public class AltManagerGui extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        RenderUtil.drawRect(0, 0, width, height, new Color(0, 0, 9, 230).getRGB());
        Fonts.tahoma_bold.drawCenteredStringWithShadow("Choose method", (width/2), (height - 140) / 2f + 10, new Color(49, 49, 69, 250).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 + 4 + 50, height/2 - 24, 100, 20, "Cancel"));
        this.buttonList.add(new GuiButton(1, width / 2 + 4 + 50, height/2 - 48, 100, 20, "Use Cracked"));
        this.buttonList.add(new GuiButton(2, width / 2 - 50, height/2 - 48, 100, 20, "Use Microsoft"));
        this.buttonList.add(new GuiButton(3, width / 2 - 150 - 4, height/2 - 48, 100, 20, "Use Mojang"));
        this.buttonList.add(new GuiButton(4, width / 2 - 50, height/2 - 24, 100, 20, "Coming Soon..."));
        this.buttonList.add(new GuiButton(5, width / 2 - 150 - 4, height/2 - 24, 100, 20, "Coming Soon..."));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiMainMenu());
        }
        if(button.id == 1){
            mc.displayGuiScreen(new GuiLogin());
        }
        if(button.id == 2){
            mc.displayGuiScreen(new GuiLoginMicrosoft());
        }
        if(button.id == 3){
            mc.displayGuiScreen(new GuiLoginMojang());
        }
    }
}
