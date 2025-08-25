package axyl.client;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ibm.icu.text.DisplayContext.Type;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import axyl.client.events.input.InputKeyEvent;
import axyl.client.events.player.EventPlayerPostUpdate;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventPrePreUpdate;
import axyl.client.events.player.EventTick;
import axyl.client.events.render.EventInClickGui;
import axyl.client.events.render.EventRender2D;
import axyl.client.font.Fonts;
import axyl.client.gui.changes.ChangeLogManager;
import axyl.client.gui.clickgui.CUI;
import axyl.client.gui.clickgui.settings.SettingManager;
import axyl.client.gui.notifications.NotiType;
import axyl.client.gui.notifications.Notification;
import axyl.client.gui.notifications.NotificationManager;
import axyl.client.modules.Module;
import axyl.client.modules.ModuleManager;
import axyl.client.modules.config.Config;
import axyl.client.modules.config.Configuration;
import axyl.client.modules.config.ConfigurationAPI;
import axyl.client.modules.features.combat.KillAura;
import axyl.client.modules.features.movement.MovementCorrection;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.ItemUtil;
import axyl.client.util.render.RenderUtil;
import de.florianmichael.viamcp.ViaMCP;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

public class Axyl{
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Axyl ins;
	static {
		ins = new Axyl();
	}
	
	public Configuration clientConfiguration;
	public File configFolder = new File("Axyl");
	public static String configToLoad = "Axyl.bin";
	
	public static boolean onDisableFix = false;
	
	public EventBus eventManager;
	public ModuleManager modManager;
	public SettingManager settingManager;
	public ChangeLogManager changeLogManager;
	public NotificationManager notificationManager;
	public Config configurationManager;
	

	public void loadClient() throws IOException {
		if(!configFolder.exists()) {
			configFolder.mkdirs();
		}
		try {
			Configuration createCfg = null;
			File cfg = new File("Axyl/Configuration.client");
			File cfg2 = new File("Axyl/"+configToLoad);
			if(!cfg.exists()) {
				clientConfiguration = ConfigurationAPI.newConfiguration(new File("Axyl/Configuration.client"));
				clientConfiguration.save();
			}
			if(!cfg2.exists()) {
				createCfg = ConfigurationAPI.newConfiguration(new File("Axyl/"+configToLoad));
				createCfg.save();
			}
		} catch (IOException e) {
			
		}
		clientConfiguration = ConfigurationAPI.loadExistingConfiguration(new File("Axyl/Configuration.client"));
		configToLoad = (String) clientConfiguration.get("config");
		
		this.eventManager = new EventBus();
		this.configurationManager = new Config();
		this.configurationManager.loadModConfig(configToLoad);
		this.settingManager = new SettingManager();
		this.modManager = new ModuleManager();
		this.notificationManager = new NotificationManager();
		this.changeLogManager = new ChangeLogManager();
		
		changeLogManager.addChanges();
		Fonts.loadFonts();
		try {
		    ViaMCP.create();
		    ViaMCP.INSTANCE.initAsyncSlider();
		    ViaMCP.INSTANCE.initAsyncSlider(5, 5, 110, 20);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		eventManager.register(this);
	}
	
	public void unLoadClient() {
		configurationManager.saveModConf(configToLoad);
		{
			clientConfiguration.set("config", configToLoad);
			try {
				clientConfiguration.save();
			} catch (IOException e) {}
		}
		eventManager.unregister(this);
	}
	
	@Subscribe
	public void inputKeyEvent(InputKeyEvent event) {
		for(Module m : modManager.getModules()) {
			if(m.getKey() == event.key) {
				m.toggle();
			}
		}
	}

	@Subscribe
	public void eventPlayerUpdate(EventPlayerUpdate event) {
		if(onDisableFix) {
			if(KillAura.autoBlockMode.getValString().equalsIgnoreCase("Legit")) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			} else {
				KillAura.unBlock();
			}
			onDisableFix = false;
		}
	}
	
	@Subscribe
	public void eventRender2D(EventRender2D event) {
		//mc.displayGuiScreen(new GuiIngameMenu());
		
		try {
			int off = 30;
			ScaledResolution sr = new ScaledResolution(mc);
			for(Notification n : Axyl.ins.notificationManager.getNotifications()) {
				off-=n.offset;
				n.drawNotification(sr.getScaledWidth()+Math.random(), sr.getScaledHeight()+off);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void sendMessage(String string) {
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(string));
	}
}
