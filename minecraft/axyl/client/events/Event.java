package axyl.client.events;

import axyl.client.Axyl;

public class Event {
	
	public boolean cancelled;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void hook(Event event) {
		try {
			Axyl.ins.eventManager.post(event);
		} catch (Exception e) {

		}
	}
}
