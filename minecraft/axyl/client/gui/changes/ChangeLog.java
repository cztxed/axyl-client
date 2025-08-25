package axyl.client.gui.changes;

public class ChangeLog {

	public String change;
	public ChangeType type;
	
	public ChangeLog(String change, ChangeType type) {
		this.change = change;
		this.type = type;
	}
}
