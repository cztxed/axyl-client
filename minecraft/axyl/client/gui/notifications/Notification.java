package axyl.client.gui.notifications;

import java.awt.Color;
import java.sql.SQLException;

import axyl.client.Axyl;
import axyl.client.font.Fonts;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;

public class Notification {

	public double offset;
	public Timer timer = new Timer();
	public int duration;
	public String text;
	private double aX;
	private double aY;
	private NotiType type;
	private Timer a = new Timer();
	
	public Notification(int dur, NotiType type, String text) {
		this.timer.reset();
		this.duration = dur;
		this.type = type;
		this.aX = 200;
		this.aY = 20;
		this.text = text;
		this.offset = 0;
	}
	
	public void drawNotification(double x, double y) {
		this.aX = (int)this.aX;
		x = (int) x;
		
		double wid = 158;
		double hei = 30;
		if(timer.hasReached(duration)) {
			if(a.hasReached(10)) {
				if(this.aX > 10) {
					if(offset > 3) {
						offset+=(3-this.offset)*0.055;
					}
				}
				if(this.aX < 200) {
					aX+=(200-this.aX)*0.075;
				}
				if(this.aY < 20) {
					aY+=(20-this.aY)*0.025;
				}
				a.reset();
			}
		} else {
			if(a.hasReached(10)) {
				if(offset < hei+3) {
					offset+=(hei+3-this.offset)*0.015+2;
				}
				if(this.aX > 0) {
					aX+=(0-this.aX)*0.075;
				}
				if(this.aY > 0) {
					aY+=(0-this.aY)*0.075;
				}
				a.reset();
			}
		}
		
		int c1 = new Color(25, 25, 27, 170).getRGB();
		int c2 = 0;
		int c3 = 0;
		int c4 = new Color(235, 235, 235, 240).getRGB();
		int c5 = new Color(230, 230, 230, 240).getRGB();
		String suffix = "A";
		if(type.equals(NotiType.Neutral)) {
			c2 = new Color(145, 144, 155, 190).getRGB();
			c3 = new Color(235, 234, 235, 190).getRGB();
		} else {
			c2 = new Color(175, 65, 72, 180).getRGB();
			c3 = new Color(195, 65, 72, 215).getRGB();
		}

		int offset = 33;
		RenderUtil.drawRect(x - wid - 3 + hei + this.aX, y - hei - 26 + this.aY, wid-hei, hei, c1);
		RenderUtil.drawRect(x - wid - 3 + this.aX, y - hei - 26 + this.aY, hei, hei, c2);
		Fonts.opensansbold.drawString(suffix, x-wid+4.5f + this.aX, y - hei-offset+9.5f + this.aY, c3);	
		String s = "Notification!";
		Fonts.roboto_medium2.drawString(s, x-wid+hei+3 + this.aX, y-hei-offset+15 + this.aY, c4);
		Fonts.roboto_small2.drawString(text, x-wid+hei+3 + this.aX, y-hei-offset+25 + this.aY, c5);
	    double l = wid;
	    double wd = (l) * (timer.getTimePassed() - 0) / (this.duration - 0);
		RenderUtil.drawRect(x - wid - 3 + this.aX, y - hei + 3 + this.aY, wd, 1, c3);
		//Fonts.roboto_bold.drawString(s, x-wid+hei, y-hei-offset+12, -1);
		if(timer.hasReached(duration+750)) {
			Axyl.ins.notificationManager.getNotifications().remove(this);
		}
	}
}
