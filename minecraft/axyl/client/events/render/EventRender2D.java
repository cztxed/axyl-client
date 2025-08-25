package axyl.client.events.render;

import axyl.client.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D extends Event {
	
	public ScaledResolution sr;
	
	public EventRender2D(ScaledResolution sr) {
		this.sr = sr;
	}
}
