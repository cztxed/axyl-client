package axyl.client.events.player;

import axyl.client.events.Event;

public class EventScaffold extends Event {
	
	public boolean isEventTick;
	
	public EventScaffold(boolean isEventTick) {
		this.isEventTick = isEventTick;
	}
}
