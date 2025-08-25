package axyl.client.events.input;

import axyl.client.events.Event;

public class EventMouseDelta extends Event {
	
	public int deltaX;
	public int deltaY;
	
	public EventMouseDelta(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}
}
