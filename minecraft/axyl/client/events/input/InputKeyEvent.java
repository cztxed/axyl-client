package axyl.client.events.input;

import axyl.client.events.Event;

public class InputKeyEvent extends Event {
	
	public int key;
	
	public InputKeyEvent(int key) {
		this.key = key;
	}
	
	public int getKey() {
		return this.key;
	}
}
