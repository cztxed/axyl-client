package axyl.client.gui.clickgui.composition;

import axyl.client.events.render.EventInClickGui;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Module;

public class Composition {

    public double x, y, x2, y2;
    public CUI parent;
    public Module module;
    public Setting setting;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void drawScreen(int mouseX, int mouseY) {
    	EventInClickGui clickGui = new EventInClickGui();
    	clickGui.hook(clickGui);
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public void keyTyped(char typedChar, int keyCode) {

    }

}
