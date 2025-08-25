package axyl.client.events.player;

import axyl.client.events.Event;

public class EventJump extends Event {
	
    private float yaw;

    public EventJump(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
