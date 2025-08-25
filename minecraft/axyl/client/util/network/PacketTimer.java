package axyl.client.util.network;

import axyl.client.util.time.Timer;
import net.minecraft.network.Packet;

public class PacketTimer {

	private Packet<?> packet;
    private Timer timer;
    private long milliSeconds;

    public PacketTimer(Packet<?> packet) {
        this.milliSeconds = System.currentTimeMillis();
        this.timer = new Timer();
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

	public long getMilliSeconds() {
		return milliSeconds;
	}
	
	public Timer getTime() {
		return timer;
	}
}
