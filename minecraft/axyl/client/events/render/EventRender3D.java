package axyl.client.events.render;

import axyl.client.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender3D extends Event {
	
    public static float partialTicks;
    
    public EventRender3D(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
    
    public static float getPartialTicks() {
        return partialTicks;
    }
}
