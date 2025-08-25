package axyl.client.gui.changes;

import java.util.ArrayList;

public class ChangeLogManager {

	public ArrayList<ChangeLog> changes = new ArrayList<>();
	
	public void addChanges() {
		changes.add(new ChangeLog("Added RayTrace option - KillAura", ChangeType.Added));
		changes.add(new ChangeLog("Improved rotations - KillAura", ChangeType.Neutral));
		changes.add(new ChangeLog("More autoblock options - KillAura", ChangeType.Added));
		changes.add(new ChangeLog("Added Safewalk option - Scaffold", ChangeType.Added));
		changes.add(new ChangeLog("Removed useless settings - Scaffold", ChangeType.Removed));
		changes.add(new ChangeLog("Fixed settings - ClickGui", ChangeType.Neutral));
		changes.add(new ChangeLog("Added QuickStop", ChangeType.Added));
		changes.add(new ChangeLog("Added AntiVoid", ChangeType.Added));
		changes.add(new ChangeLog("Added Config Manager", ChangeType.Added));
		changes.add(new ChangeLog("Added ItemSize", ChangeType.Added));
		changes.add(new ChangeLog("Added Timer", ChangeType.Added));
		changes.add(new ChangeLog("Added FastBreak", ChangeType.Added));
		changes.add(new ChangeLog("Added VClip", ChangeType.Added));
		changes.add(new ChangeLog("Added BedFucker", ChangeType.Added));
	}
}
