package axyl.client.events.player;

import axyl.client.events.Event;

public class EventPlayerSprint extends Event {
	
	public boolean sprintState;
	
	public EventPlayerSprint(boolean sprintState) {
		this.sprintState = sprintState;
	}
	
	public void setSprintState(boolean sprintState) {
		this.sprintState = sprintState;
	}
	
	public boolean sprintState() {
		return this.sprintState;
	}
}
