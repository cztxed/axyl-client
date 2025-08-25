package axyl.client.gui.notifications;

import java.util.ArrayList;

public class NotificationManager {

	public static ArrayList<Notification> notifications = new ArrayList<>();
	
	public void addNotification(int duration, NotiType type, String text) {
		notifications.add(new Notification(duration, type, text));
	}
	
	public ArrayList<Notification> getNotifications() {
		return notifications;
	}
}
