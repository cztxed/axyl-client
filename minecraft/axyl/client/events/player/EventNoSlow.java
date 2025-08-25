package axyl.client.events.player;

import axyl.client.events.Event;

public class EventNoSlow extends Event {
	
	public float moveStrafe;
    public float moveForward;
    public int sprintToggleTimer;

    public EventNoSlow(float moveStrafe, float moveForward, int sprintToggleTimer) {
        this.moveStrafe = moveStrafe;
        this.moveForward = moveForward;
        this.sprintToggleTimer = sprintToggleTimer;
    }

	public float getMoveStrafe() {
		return moveStrafe;
	}

	public void setMoveStrafe(float moveStrafe) {
		this.moveStrafe = moveStrafe;
	}

	public float getMoveForward() {
		return moveForward;
	}

	public void setMoveForward(float moveForward) {
		this.moveForward = moveForward;
	}

	public int getSprintToggleTimer() {
		return sprintToggleTimer;
	}

	public void setSprintToggleTimer(int sprintToggleTimer) {
		this.sprintToggleTimer = sprintToggleTimer;
	}
}

