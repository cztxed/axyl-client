package axyl.client.modules.features.combat;

import java.util.ArrayList;    

import java.util.concurrent.ThreadLocalRandom;

import javax.print.Doc;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.time.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Criticals extends Module
{
    private Timer dmgTimer = new Timer();
    private Timer timer = new Timer();
    public static boolean doCrits;
    public int onGroundTicks = 0;
    public int experimentalTicks = 0;
    public int crit = 0;
	private Setting groundTicks;
	private Setting critMode;
	private Setting dmgOnly;
    
    public Criticals() {
        super("Criticals", "Makes Your hits critically strike", 0, Category.Combat);
    }
    
    @Override
    public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("Experimental");
        options.add("DamageMotion");
        options.add("Packet");
        options.add("Packet2");
        options.add("Packet3");
        options.add("Packet4");
        options.add("OldNCP");
        options.add("Vulcan");

        Axyl.ins.settingManager.createSetting(critMode = new Setting("Criticals mode", this, "Packet", options));
        Axyl.ins.settingManager.createSetting(groundTicks = new Setting("Ground ticks", this, 10.0, 0.0, 20.0, 0, false));
        Axyl.ins.settingManager.createSetting(dmgOnly = new Setting("Damage Only", this, false, false));
        super.moduleSetup();
    }
    
    @Subscribe
    public void eventPacket(EventPacket event) {
    	final String mode = critMode.getValString();
        if (event.getPacket() instanceof C02PacketUseEntity) {
            final C02PacketUseEntity packet = (C02PacketUseEntity)event.getPacket();
        	if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
        		doCrits = true;
        	}
        }
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
    	final String mode = critMode.getValString();
    	if(dmgOnly.getValBoolean()) {
    		if(mc.thePlayer.hurtTime > 0) {
    			dmgTimer.reset();
    		}
    		if(dmgTimer.hasReached(150)) {
    			doCrits = false;
    		}
    	}
    	if(doCrits) {
    		if(mode.equalsIgnoreCase("DamageMotion")) {
    			timer.reset();
    			doCrits = false;
    		}
    	}
		if(mode.equalsIgnoreCase("DamageMotion")) {
			if(!timer.hasReached(100)) {
				if(!mc.thePlayer.onGround)
				if(mc.thePlayer.hurtTime == 10) {
					mc.thePlayer.motionY = Math.abs(mc.thePlayer.motionY)*-1;
				}
			}
		}
    	if(mode.equalsIgnoreCase("Experimental")) {
    		if(doCrits) {
    			if(onGroundTicks == 0)
    			if(mc.thePlayer.onGround) {
        			experimentalTicks++;
        		} else {
        			if(experimentalTicks > 100) {
        				experimentalTicks = 0;
        			}
        		}
    		} else {
    			experimentalTicks = 1;
    		}
    	}
    	if(mc.thePlayer.onGround) {
    		onGroundTicks++;
    	} else {
    		onGroundTicks = 0;
    	}
    }
    
    @Subscribe
    public void eventPacket_2(EventPacket event) {
    	final String mode = critMode.getValString();
        this.suffix = mode;

        double nextDouble = ThreadLocalRandom.current().nextDouble(0.0005, 0.0011);
    	if(dmgOnly.getValBoolean()) {
    		if(mc.thePlayer.hurtTime > 0) {
    			dmgTimer.reset();
    		}
    		if(dmgTimer.hasReached(50)) {
    			doCrits = false;
    		}
    	}
        if(doCrits) {
        	if(mode.equalsIgnoreCase("Experimental")) {
        		if(event.getPacket() instanceof C03PacketPlayer) {
        			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
            		if(onGroundTicks == 0)
        			if(experimentalTicks % 2 == 0) {
        				p.onGround = false;
        				doCrits = false;
        			}
        		}
        	}
            if (mc.thePlayer.onGround && onGroundTicks >= groundTicks.getValDouble()) {
            	if(mode.equalsIgnoreCase("Vulcan")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(549)) {
		            		if(crit == 0) {
		            			PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.031125, mc.thePlayer.posZ, false));
		            			crit++;
		            		} else {
		            			if(crit == 1) {
		            				PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.0155625, mc.thePlayer.posZ, false));
		            				crit = 0;
					    			timer.reset();
					    	        doCrits = false;
		            			}
		            		}
		            		event.setCancelled(true);
		    	        }
		    		}
            	}
            	if(mode.equalsIgnoreCase("OldNCP")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(249)) {
		            		if(crit == 0) {
				    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.170787077218804, mc.thePlayer.posZ, false));
		            			crit++;
		            		} else {
		            			if(crit == 1) {
					    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+ 0.015555072702206, mc.thePlayer.posZ, false));
		            				crit = 0;
					    			timer.reset();
					    	        doCrits = false;
		            			}
		            		}
		            		event.setCancelled(true);
		    	        }
		    		}
            	}
            	if(mode.equalsIgnoreCase("Packet")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(249)) {
		            		if(crit == 0) {
				    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + nextDouble, mc.thePlayer.posZ, false));
		            			crit++;
		            		} else {
		            			if(crit == 1) {
					    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + nextDouble/2, mc.thePlayer.posZ, false));
		            				crit = 0;
					    			timer.reset();
					    	        doCrits = false;
		            			}
		            		}
		            		event.setCancelled(true);
		    	        }
		    		}
            	}
            	if(mode.equalsIgnoreCase("Packet2")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(100)) {
		            		if(crit == 0) {
				    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
				    	        crit++;
		            		} else {
			            		if(crit == 1) {
					    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.015625, mc.thePlayer.posZ, true));
					    	        crit++;
			            		} else {
			            			if(crit >= 2) {
						    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			            				crit = 0;
						    			timer.reset();
						    	        doCrits = false;
			            			}
			            		}
		            		}
		    	        	event.setCancelled(true);
		    	        }
		    		}
            	}
            	if(mode.equalsIgnoreCase("Packet3")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(100)) {
		            		if(crit == 0) {
				    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
				    	        crit++;
		            		} else {
			            		if(crit == 1) {
					    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.015625, mc.thePlayer.posZ, true));
					    	        crit++;
			            		} else {
			            			if(crit >= 2) {
						    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			            				crit = 0;
						    			timer.reset();
						    	        doCrits = false;
			            			}
			            		}
		            		}
		    	        	event.setCancelled(true);
		    	        }
		    		}
            	}
            	if(mode.equalsIgnoreCase("Packet4")) {
		    		if(event.getPacket() instanceof C03PacketPlayer) {
		    			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
		    	        if(timer.hasReached(100)) {
		            		if(crit == 0) {
				    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
				    	        crit++;
		            		} else {
			            		if(crit == 1) {
					    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.07840000152587834, mc.thePlayer.posZ, true));
					    	        crit++;
			            		} else {
			            			if(crit >= 2) {
						    	        PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			            				crit = 0;
						    			timer.reset();
						    	        doCrits = false;
			            			}
			            		}
		            		}
		    	        	event.setCancelled(true);
		    	        }
		    		}
            	}
    		}
    	}
    }

    @Override
    public void onEnable() {
    	crit = 0;
    	doCrits = false;
    	super.onEnable();
    }
}
