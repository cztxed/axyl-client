package axyl.client.events.player;

import axyl.client.events.Event;
import net.minecraft.entity.Entity;

public class EventHitEntity extends Event {
	
	public Entity ent;
	
	public EventHitEntity(Entity ent) {
		this.ent = ent;
	}
	
	public Entity getEnt() {
		return ent;
	}
	
	public void setEnt(Entity ent) {
		this.ent = ent;
	}
}
