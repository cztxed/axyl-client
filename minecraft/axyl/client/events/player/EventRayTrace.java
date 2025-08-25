package axyl.client.events.player;

import axyl.client.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

public class EventRayTrace extends Event {
	
	public MovingObjectPosition object;
	public Entity entity;
	public float yaw, pitch;
	
	public EventRayTrace(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void setObject(MovingObjectPosition object) {
		this.object = object;
	}
	
	public MovingObjectPosition getObject() {
		return this.object;
	}
	
	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}

