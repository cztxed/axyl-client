package axyl.client.events.player;

import axyl.client.events.Event;

public class EventStrafe extends Event
{
    public float yaw;
    public float strafe;

    public EventStrafe(float yaw, float strafe) {
    	this.yaw = yaw;
    	this.strafe = strafe;
    }
    
    public float getStrafe() {
		return strafe;
	}

	public void setStrafe(float strafe) {
		this.strafe = strafe;
	}

	public void setYaw(float yaw) {
    	this.yaw = yaw;
    }
    
    public float getYaw() {
    	return yaw;
    }
}
