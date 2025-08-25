package axyl.client.modules;

import java.util.ArrayList;

import axyl.client.modules.features.combat.*;
import axyl.client.modules.features.movement.*;
import axyl.client.modules.features.other.*;
import axyl.client.modules.features.player.*;
import axyl.client.modules.features.visuals.*;
 
public class ModuleManager {

	public ArrayList<Module> modules = new ArrayList<>();
	public ArrayList<Module> unfilteredModules = new ArrayList<>();
	
	public ModuleManager() {
		addModules();
	}
	
	public void addModules() {
		
		// Other
		modules.add(new Disabler());
		modules.add(new AutoReport());
		modules.add(new StaffDetector());
		modules.add(new RotationsHelper());
		modules.add(new SecurityFeatures());
		modules.add(new ClientSpoofer());
		modules.add(new AntiGuiClose());
		modules.add(new PingSpoof());
		modules.add(new Blink());
		
		// Combat
		modules.add(new AntiBot());
		modules.add(new AutoArmor());
		modules.add(new KillAura());
		modules.add(new Criticals());
		modules.add(new AimAssist());
		modules.add(new AutoClicker());
		modules.add(new Reach());
		modules.add(new WTap());
		modules.add(new Velocity());
		
		// Movement
		modules.add(new MovementCorrection());
		modules.add(new Sprint());
		modules.add(new InvMove());
		modules.add(new NoSlowDown());
		modules.add(new QuickStop());
		modules.add(new NoWeb());
		modules.add(new LongJump());
		modules.add(new Flight());
		modules.add(new Speed());
		modules.add(new KeepDirection());
		modules.add(new Scaffold());
		modules.add(new LegitScaffold());
		
		//Player
		modules.add(new NoRotate());
		modules.add(new VClip());
		modules.add(new NoFall());
		modules.add(new Timer());
		modules.add(new FastUse());
		modules.add(new Regen());
		modules.add(new FastBreak());
		modules.add(new BedFucker());
		modules.add(new AntiVoid());
		modules.add(new AutoTool());
		modules.add(new InvManager());
		modules.add(new ChestStealer());
		modules.add(new InvCleaner());
		modules.add(new FastPlace());

		// Visuals
		modules.add(new Ambiance());
		modules.add(new NoEffects());
		modules.add(new NoWeather());
		modules.add(new ViewClip());
		modules.add(new HurtCam());
		modules.add(new SessionInfo());
		modules.add(new Crosshair());
		modules.add(new ChestESP());
		modules.add(new ItemSize());
		modules.add(new Glint());
		modules.add(new ESP());
		modules.add(new Interface());
		modules.add(new ClickGui());
		
		if(!modules.isEmpty())
		for(int i = 0; i < modules.size(); i++) {
			unfilteredModules.add(modules.get(i));
		}
	}
	
	public ArrayList<Module> getModules() {
		return modules;
	}
	
	public Module getModuleByName(String name) {
		return modules.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}
}
