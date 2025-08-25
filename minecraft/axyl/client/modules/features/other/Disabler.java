package axyl.client.modules.features.other;

import java.util.ArrayList;  
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.EventPacketType;
import axyl.client.events.network.EventType;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Module {
    
	public ArrayList<Packet<?>> hazelold = new ArrayList<>();
	public ArrayList<C0FPacketConfirmTransaction> c0fs = new ArrayList<>();
	public ArrayList<C00PacketKeepAlive> c00s = new ArrayList<>();
	public ArrayList<C03PacketPlayer> c03s = new ArrayList<>();
	public int c00Counter;
	public int windowID;
	public int vulcanTicks;
	public boolean cancelSprint;
	public Setting bmcReach;
	public static Setting disablerMode;

	public Disabler() {
		super("Disabler", "", Keyboard.KEY_NONE, Category.Other);
	}
	
	@Override
	public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("HazelMC-OLD");
        options.add("Watchdog");
        options.add("Watchdog Old");
        options.add("NCVE");
        options.add("Sprint");
        options.add("Vulcan");
        options.add("Transaction");
        options.add("Spectate");

        Axyl.ins.settingManager.createSetting(disablerMode = new Setting("Mode", this, "Sprint", options));
        Axyl.ins.settingManager.createSetting(bmcReach = new Setting("NCVE reach", this, false, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = disablerMode.getValString();
		if(mode.equalsIgnoreCase("Transaction")) {

		}
		if(mode.equalsIgnoreCase("Vulcan")) {
			vulcanTicks++;
			if(vulcanTicks >= 30) {
				mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
				vulcanTicks = 0;
			} else {
				if(vulcanTicks == 1) {
					mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
				}
			}
		}
	}
	
	@Subscribe
	public void eventPacketType(EventPacketType event) {
		String mode = disablerMode.getValString();
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		String mode = disablerMode.getValString();
		this.suffix = mode;
		if(mc.theWorld == null) {
			return;
		}

		if(event.getPacketDirection().equals(PacketDir.IN)) {
			if(!hazelold.isEmpty()) {
				if(c00Counter > 4) {
					hazelold.forEach(PacketUtil::sendPacketNoEvent);
					hazelold.clear();
					c00Counter = 0;
				}
			}
			if(mode.equalsIgnoreCase("Watchdog Old")) {
				if(c00s.size() != 1)
				if(!c0fs.isEmpty())
				if(c03s.size() > 35)
				if(c0fs.size() > 50) {
					PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(windowID, c0fs.get(0).getUid(), c0fs.get(0).accepted));
					c0fs.remove(c0fs.get(0));
				}
				if(!c00s.isEmpty())
				if(c00s.size() > 2) {
					for(C00PacketKeepAlive c : c00s) {
						if(c.timer.hasReached(400)) {
							c00Counter++;
							if(c00Counter % 2 == 0) {
								c03s.clear();
							}
							PacketUtil.sendPacketNoEvent(c);
							c00s.remove(c);
						}
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("Vulcan")) {
	        if(event.getPacket() instanceof C0BPacketEntityAction) {
	        	C0BPacketEntityAction p = (C0BPacketEntityAction)event.getPacket();
	        	if(p.getAction().equals(p.getAction().START_SPRINTING)) {
	        		if(!cancelSprint) {
	        			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	        			cancelSprint = true;
	        		}
	        		event.setCancelled(true);
	        	}
            	if(p.getAction().equals(p.getAction().STOP_SPRINTING)) {
            		event.setCancelled(true);
            	}
	        	if(p.getAction().equals(C0BPacketEntityAction.Action.START_SNEAKING) || p.getAction().equals(C0BPacketEntityAction.Action.STOP_SNEAKING)) {
					event.setCancelled(true);
				}
	        }
		}
		if(mode.equalsIgnoreCase("Watchdog Old")) {
			if(mc.thePlayer.ticksExisted < 5) {
				if(!c03s.isEmpty())
				c03s.clear();
			}
			if(event.getPacket() instanceof C0FPacketConfirmTransaction) {
				C0FPacketConfirmTransaction p = (C0FPacketConfirmTransaction)event.getPacket();
				p.timer.reset();
				c0fs.add(p);
				windowID = p.getWindowId();
				event.setCancelled(true);
			} else {
				windowID = 0;
			}
			if(event.getPacket() instanceof C03PacketPlayer) {
				C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
				c03s.add(p);
			}
			if(event.getPacket() instanceof C00PacketKeepAlive) {
				C00PacketKeepAlive p = (C00PacketKeepAlive)event.getPacket();
				p.timer.reset();
				c00s.add(p);
				event.setCancelled(true);
			}
			if(event.getPacket() instanceof S00PacketKeepAlive) {
				S00PacketKeepAlive p = (S00PacketKeepAlive)event.getPacket();
				C00PacketKeepAlive p1 = new C00PacketKeepAlive(p.id);
				p1.timer.reset();
				c00s.add(p1);
				event.setCancelled(true);
			}
	        if(event.getPacket() instanceof C0BPacketEntityAction) {
	        	C0BPacketEntityAction p = (C0BPacketEntityAction)event.getPacket();
	        	if(p.getAction().equals(p.getAction().START_SPRINTING)) {
        			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	        		event.setCancelled(true);
	        	}
            	if(p.getAction().equals(p.getAction().STOP_SPRINTING)) {
            		event.setCancelled(true);
            	}
	        }
		}
		if(mode.equalsIgnoreCase("NCVE")) {
			if(bmcReach.getValBoolean()) {
				if(mc.thePlayer.ticksExisted > 100) {
					if(event.getPacket() instanceof C0FPacketConfirmTransaction) {
						C0FPacketConfirmTransaction p = (C0FPacketConfirmTransaction)event.getPacket();
						if(c00Counter % 2 == 0) {
							PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(p.getWindowId(), (short)-1, false));
						} else {
							PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(p.getWindowId(), (short)1, false));
						}
						c00Counter++;
						event.setCancelled(true);
					}
				}
			}
	        if(event.getPacket() instanceof C0BPacketEntityAction) {
	        	C0BPacketEntityAction p = (C0BPacketEntityAction)event.getPacket();
				if(p.getAction().equals(C0BPacketEntityAction.Action.OPEN_INVENTORY)) {
					event.setCancelled(true);
				}
	        	if(p.getAction().equals(p.getAction().START_SPRINTING)) {
        			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	        		event.setCancelled(true);
	        	}
            	if(p.getAction().equals(p.getAction().STOP_SPRINTING)) {
            		event.setCancelled(true);
            	}
               	if(p.getAction().equals(p.getAction().OPEN_INVENTORY)) {
            		event.setCancelled(true);
            	}
	        }
		}
		if(mode.equalsIgnoreCase("Transaction")) {
			if(mc.thePlayer.ticksExisted < 10) {
				vulcanTicks = 0;
			}
			if(event.getPacket() instanceof C0FPacketConfirmTransaction) {
				C0FPacketConfirmTransaction p = (C0FPacketConfirmTransaction)event.getPacket();
				PacketUtil.sendPacketNoEvent(new C00PacketKeepAlive(vulcanTicks));
				vulcanTicks++;
			}
			if(event.getPacket() instanceof C00PacketKeepAlive) {
				event.setCancelled(true);
			}
			if(event.getPacket() instanceof S00PacketKeepAlive) {
				event.setCancelled(true);
			}
	        if(event.getPacket() instanceof C0BPacketEntityAction) {
	        	C0BPacketEntityAction p = (C0BPacketEntityAction)event.getPacket();
	        	if(p.getAction().equals(p.getAction().START_SPRINTING)) {
        			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	        		event.setCancelled(true);
	        	} else {
	            	if(p.getAction().equals(p.getAction().STOP_SPRINTING)) {
	            		event.setCancelled(true);
	            	}
	        	}
	        }
		}
		if(mode.equalsIgnoreCase("Spectate")) {
			if(event.getPacket() instanceof C03PacketPlayer) {
				PacketUtil.sendPacketNoEvent(new C18PacketSpectate(UUID.randomUUID()));
			}
		}
		if(mode.equalsIgnoreCase("HazelMC-OLD")) {
			if(event.getPacket() instanceof C17PacketCustomPayload) {
				C17PacketCustomPayload p = (C17PacketCustomPayload)event.getPacket();
	            p.channel = null;
	            p.data = null;
			}
			if(event.getPacket() instanceof C03PacketPlayer) {
				if(!cancelSprint) {
					c00Counter++;
					PacketUtil.sendPacketNoEvent(new C00PacketKeepAlive(c00Counter));
				} else {
					cancelSprint = false;
				}
			}
			if(event.getPacket() instanceof C00PacketKeepAlive) {
				event.setCancelled(true);
			}
			if(event.getPacket() instanceof S00PacketKeepAlive) {
				this.cancelSprint = true;
				event.setCancelled(true);
			}
		} else {
			c00Counter = 11;
		}
		if(mode.equalsIgnoreCase("Sprint")) {
	        if(event.getPacket() instanceof C0BPacketEntityAction) {
	        	C0BPacketEntityAction p = (C0BPacketEntityAction)event.getPacket();
	        	if(p.getAction().equals(p.getAction().START_SPRINTING)) {
        			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	        		event.setCancelled(true);
	        	} else {
	            	if(p.getAction().equals(p.getAction().STOP_SPRINTING)) {
	            		event.setCancelled(true);
	            	}
	        	}
	        }
		}
	}
    
    public PacketBuffer customBuffer(String data, boolean wrap) {
        if (wrap) return new PacketBuffer(Unpooled.buffer()).writeString(data);
		return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
    }
    
    @Override
    public void onEnable() {
    	c00Counter = 0;
    	vulcanTicks = 0;
    	cancelSprint = false;
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	String mode = disablerMode.getValString();
    	if(!hazelold.isEmpty()) {
    		hazelold.forEach(PacketUtil::sendPacketNoEvent);
			hazelold.clear();
    	}
    	if(!c00s.isEmpty()) {
    		c00s.forEach(PacketUtil::sendPacketNoEvent);
    		c00s.clear();
    	}
    	if(!c0fs.isEmpty()) {
    		c0fs.forEach(PacketUtil::sendPacketNoEvent);
    		c0fs.clear();
    	}
		if(!mode.equalsIgnoreCase("Watchdog Old")) {
	    	if(!c03s.isEmpty()) {
	    		c03s.forEach(PacketUtil::sendPacketNoEvent);
	    		c03s.clear();
	    	}
		}
		if(mode.equalsIgnoreCase("Vulcan")) {
			if(vulcanTicks < 60) {
				PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
			}
		}
    	super.onDisable();
    }
}
