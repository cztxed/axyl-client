package axyl.client.events.player;

import org.apache.commons.lang3.RandomUtils;

import axyl.client.events.Event;
import net.minecraft.client.Minecraft;
import axyl.client.util.math.MathUtils;

public class EventPlayerPreUpdate extends Event
{
    public double x, y, z;
    private float yaw, pitch;
	private boolean onGround;
	
    public EventPlayerPreUpdate(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }
    
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

    public void setYaw(final float yaw) {
    	this.yaw = yaw;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
    }

	public float getPitch() {
		return (float) MathUtils.clamp(pitch, -90, 90);
	}

	public void setPitch(float pitch) {
        this.pitch = (float) MathUtils.clamp(pitch, -90, 90);
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
}
