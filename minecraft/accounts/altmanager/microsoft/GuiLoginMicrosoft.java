package accounts.altmanager.microsoft;

import net.minecraft.client.gui.*; 
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import accounts.altmanager.AltManagerGui;
import accounts.altmanager.SessionChanger;
import axyl.client.font.Fonts;
import axyl.client.util.render.RenderUtil;

import java.awt.Color;
import java.io.IOException;

public class GuiLoginMicrosoft extends GuiScreen {
    private GuiTextField username, password;

    @Override
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 0) {
            if(this.username.getText().equals("")) {
                this.mc.displayGuiScreen(new GuiLoginMicrosoft());
            } else {
                SessionChanger.getInstance().setUserMicrosoft(this.username.getText(), this.password.getText());
            }
        }
        if (button.id == 1) {
        	mc.displayGuiScreen(new AltManagerGui());
        }
    }

    @Override
    public void drawScreen(final int x2, final int y2, final float z2) {
        final ScaledResolution sr = new ScaledResolution(this.mc);
        RenderUtil.drawRect(0, 0, width, height, new Color(0, 0, 9, 230).getRGB());      this.username.drawTextBox();
        this.password.drawObfuscatedTextBox();
        Fonts.tahoma_bold.drawCenteredString("Email & Password", (int)(this.width / 2), (int)(sr.getScaledHeight() / 2 - 65), new Color(49, 49, 69, 250).getRGB());
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {
        final ScaledResolution sr = new ScaledResolution(this.mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 50 - 12, this.height / 2-4, 120, 20, I18n.format("Login Microsoft", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50 - 12, this.height / 2+17, 120, 20, "Go back"));
        (this.username = new GuiTextField(100, this.fontRendererObj, this.width / 2 - 50 - 10, sr.getScaledHeight() / 2 - 50, 117, 20)).setFocused(true);
        (this.password = new GuiTextField(100, this.fontRendererObj, this.width / 2 - 50 - 10, sr.getScaledHeight() / 2 - 30, 117, 20)).setFocused(false);
        Keyboard.enableRepeatEvents(true);
    }


    @Override
    protected void keyTyped(final char character, final int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t' && !this.username.isFocused()) {
            this.username.setFocused(true);
            this.password.setFocused(false);
        }
        if (character == '\t' && !this.password.isFocused()) {
            this.password.setFocused(true);
            this.username.setFocused(false);
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(final int x2, final int y2, final int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.loadEntityShader(null);
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}
