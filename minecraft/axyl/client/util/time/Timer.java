package axyl.client.util.time;

import axyl.client.util.Utility;

public class Timer extends Utility {
	
    private long prevMS;
    public long lastMS;
    
    public Timer() {
        this.prevMS = 0L;
    }

    public boolean hasTimeElapsed(final long time, final boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }
    
    public boolean hasReached(final double d) {
        if (this.getTimePassed() >= d) {
            return true;
        }
        return false;
    }
    
    public void reset() {
        this.prevMS = this.getTime();
    }
    
    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
    
    public long getTimePassed() {
        return this.getTime() - this.getPrevMS();
    }
    
    public long getPrevMS() {
        return this.prevMS;
    }
}
