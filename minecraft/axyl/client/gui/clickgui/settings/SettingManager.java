package axyl.client.gui.clickgui.settings;

import java.util.Iterator;

import axyl.client.modules.Module;

import java.util.ArrayList;

public class SettingManager
{
    private ArrayList<Setting> settings;
    
    public SettingManager() {
        this.settings = new ArrayList<Setting>();
    }
    
    public void createSetting(final Setting in) {
        this.settings.add(in);
    }
    
    public ArrayList<Setting> getSettings() {
        return this.settings;
    }
    
    public ArrayList<Setting> getSettingsByMod(Module mod) {
        final ArrayList<Setting> out = new ArrayList<Setting>();
        for (final Setting s : this.getSettings()) {
            if (s.getSettingModule().equals(mod)) {
                out.add(s);
            }
        }
        if (out.isEmpty()) {
            return null;
        }
        return out;
    }
    
    public Setting getSettingByName(final String name) {
        for (final Setting set : this.getSettings()) {
            if (set.getName().equalsIgnoreCase(name)) {
                return set;
            }
        }
        return null;
    }
    
    public Setting getSettingByName(final Module m, final String name) {
        for (final Setting set : this.getSettings()) {
            if (set.getName().equalsIgnoreCase(name) && set.getSettingModule() == m) {
                return set;
            }
        }
        return null;
    }
}
