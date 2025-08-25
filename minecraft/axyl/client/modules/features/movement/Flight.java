package axyl.client.modules.features.movement;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPlayerSprint;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventStrafe;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;

public class Flight extends Module {

	public double posx, posy, posz;
	public double currentSpeed;
	public int level;
	public int ticksVerusL;
	public boolean threwFireball;
	public boolean VerusLed;
	private Setting flyMode;
	public Flight() {
		super("Flight", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Vanilla");
		options.add("Verus");
		options.add("VerusL");
		options.add("Spoof");
		options.add("Freecam");
		options.add("FireBall");
		options.add("FunCraft");
		options.add("ChunkLoad");
		
		Axyl.ins.settingManager.createSetting(this.flyMode = new Setting("Mode", this, "Vanilla", options));
		super.moduleSetup();
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = flyMode.getValString();
		this.suffix = mode;
		if(mode.equalsIgnoreCase("Verus")) {
	    	mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
			mc.thePlayer.motionY = 0;
		}
		if(mode.equalsIgnoreCase("Vanilla")) {
			mc.thePlayer.capabilities.isFlying = true;
		}
		if(mode.equalsIgnoreCase("Spoof")) {
			mc.thePlayer.motionY = 0;
			MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()-0.03);
		}
		if(mode.equalsIgnoreCase("ChunkLoad")) {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.098F, mc.thePlayer.posZ);
		}
		if(mode.equalsIgnoreCase("Freecam")) {
			mc.thePlayer.motionY = (mc.gameSettings.keyBindSneak.pressed ? -1.5 : mc.gameSettings.keyBindJump.pressed ? 1.5 : 0);
			MovementUtil.setSpeed(1.5);
		}
		if (mode.equalsIgnoreCase("FunCraft")) {
			double speed = 1.45;
            if (mc.thePlayer.isCollidedHorizontally) {
            	if (mc.thePlayer.isMoving()) {
	   	            if (this.level > 0) {
	 	               this.currentSpeed = 0.25D;
	 	               this.level = 2;
	 	            }
            	}
	         }

	         if (this.level == 0) {
	            if (mc.thePlayer.isMoving() || !mc.thePlayer.onGround) {
	               mc.thePlayer.jump();
	               this.currentSpeed = speed;
	               ++this.level;
	            }
	         } else {
	            if (this.level == 1) {
	               mc.timer.timerSpeed = 1.5F;
	               this.level++;
	            }

	            if (mc.timer.timerSpeed >= 1.0F) {
	            	mc.timer.timerSpeed -= 0.01F;
	            }

	            mc.thePlayer.motionY = 0.0D;
	            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001+RandomUtils.nextDouble(1.86245E-10D, 1.86245E-6D), mc.thePlayer.posZ);
	            if (this.currentSpeed > 0.3D) {
	               this.currentSpeed -= (double)MovementUtil.getSpeed() / 159.0D;
	            } else if (this.currentSpeed >= 0.27D) {
	               this.currentSpeed -= this.currentSpeed * 0.010892523940891D;
	            }

	            MovementUtil.setSpeed(this.currentSpeed);
	         }
		}
		if(mode.equalsIgnoreCase("FireBall")) {

		}
	}
	
	@Subscribe
	public void eventSprint(EventPlayerSprint event) {
		String mode = flyMode.getValString();
		if(mode.equalsIgnoreCase("FireBall")) {
	    	if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFireball) {

	    	}
		}
	}
	
	@Subscribe
	public void eventPreUpdate(EventPlayerPreUpdate event) {
		String mode = flyMode.getValString();
		if(mode.equalsIgnoreCase("FireBall")) {
	    	if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFireball) {
	    		if(ticksExisted <= 3) {
	    			event.setPitch(90);
	    		}
	    		if(ticksExisted == 2) {
		    		mc.thePlayer.swingItem();
		    		PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
					if(mc.thePlayer.onGround) {
		        		mc.thePlayer.jump();
					}
					this.threwFireball = true;
	    		}
	    	}
	    	if(threwFireball)
	    	if(mc.thePlayer.hurtTime == 10) {
	    		if(currentSpeed > 0) {
					MovementUtil.setSpeed(Math.abs(currentSpeed));
					currentSpeed = 0;
				} 
	    		this.toggle();
	    	}
		}
	}
	
	@Subscribe
	public void eventStrafe(EventStrafe event) {
		String mode = flyMode.getValString();
		if(mode.equalsIgnoreCase("FireBall")) {
	    	if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFireball) {

	    	}
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		String mode = flyMode.getValString();
        if(mode.equalsIgnoreCase("FireBall")) {
			if(event.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity p = (S12PacketEntityVelocity)event.getPacket();
				
				double motionX = p.getMotionX()/8000; 
				double motionZ = p.getMotionZ()/8000;
				
				if(Math.sqrt(motionX * motionX + motionZ * motionZ) > MovementUtil.getSpeed())
				currentSpeed = Math.abs(Math.sqrt(motionX * motionX + motionZ * motionZ));

			}
        }
		if(mc.thePlayer.ticksExisted > 5) {
	        if(mode.equalsIgnoreCase("Freecam")) {
	            if(!(event.getPacket() instanceof S08PacketPlayerPosLook)) {
	                if(event.getPacketDirection().equals(PacketDir.OUT) && !(event.getPacket() instanceof C0FPacketConfirmTransaction) && !(event.getPacket() instanceof C00PacketKeepAlive) && !(event.getPacket() instanceof C0BPacketEntityAction)) {
	                	if(event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity) {
	                		event.setCancelled(true);
	                	}
	                }
	            }
	        }
			if(mode.equalsIgnoreCase("ChunkLoad")) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					p.onGround = false;
				}
			}
			if(mode.equalsIgnoreCase("Spoof")) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					p.onGround = true;
				}
			}
			if(mode.equalsIgnoreCase("FunCraft")) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					if(level != 0) {
						if(mc.thePlayer.ticksExisted % 3 == 0) {
							p.onGround = true;
						} else {
							p.onGround = false;
						}
					}
				}
			}
			if(mode.equalsIgnoreCase("VerusL")) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					if(ticksExisted < 4) {
						MovementUtil.setSpeed(0);
						event.setCancelled(true);
					} else {
						if(ticksExisted == 5) {
							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+1, mc.thePlayer.posZ);
						}
						if(ticksExisted == 6) {
							mc.thePlayer.motionY = 0.72;
							MovementUtil.setSpeed(8);
						}
						if(ticksExisted == 8) {
							mc.thePlayer.motionY = 0.72;
						}
					}
				}
			}
			if(mode.equalsIgnoreCase("Verus")) {
				if(event.getPacket() instanceof C02PacketUseEntity)
					event.setCancelled(true);
				
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					if(mc.thePlayer.ticksExisted % 2 == 0) {
				    	if (mc.thePlayer.moveForward > 0.0f && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
				    		MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*1.85-0.06);
				    	} else {
				    		MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*1.25-0.06);
				    	}
						p.onGround = true;
					} else {
						p.onGround = false;
					}
				}
			}
		}
	}
	
	@Override
	public void onEnable() {
		String mode = flyMode.getValString();
        posx = mc.thePlayer.posX;
        posz = mc.thePlayer.posZ;
        posy = mc.thePlayer.posY;
        threwFireball = false;
        level = 0;
		if(mode.equalsIgnoreCase("VerusL")) {
			MovementUtil.damagePlayer();
		}
    	if(mode.equalsIgnoreCase("Freecam")) {
    		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY-1.13, mc.thePlayer.posZ, false));
    	}
    	if(mode.equalsIgnoreCase("FireBall")) {
    		if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFireball) {

    		} else {
    			this.toggle();
    		}
    	}
    	super.onEnable();
	}
	
	@Override
	public void onDisable() {
		String mode = flyMode.getValString();
        posx = mc.thePlayer.posX;
        posz = mc.thePlayer.posZ;
        posy = mc.thePlayer.posY;
    	mc.thePlayer.capabilities.isFlying = false; 
        if(mode.equalsIgnoreCase("Freecam")) {
        	posy = (int)posy;
        	//PacketUtil.sendPacket(new C06PacketPlayerPosLook((float)(0+Math.random()*0.01f), (float)(0+Math.random()*0.01f), (float)(0+Math.random()*0.01f), (float)(0+Math.random()*0.01f), 0, false));
        	PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.25, mc.thePlayer.posZ+0.1272, false));
        	PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX+0.1272, mc.thePlayer.posY, mc.thePlayer.posZ, false));
    		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY-2.13, mc.thePlayer.posZ, false));
    		for(int i = 0; i < 3; i++) {
        		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.7531999805212)*i, mc.thePlayer.posZ, false));
        		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (1.00133597911214)*i, mc.thePlayer.posZ, true));
        		posy = (int)mc.thePlayer.posY+1*i;
        	}
        	for(int i = 0; i < 11; i++) {
        		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX+0.1*i+Math.random()*0.01f, posy, mc.thePlayer.posZ, true));
        	}
        	mc.thePlayer.motionZ = 0;
        	mc.thePlayer.motionY = 0;
        	mc.thePlayer.motionX = 0;
        }
        mc.timer.timerSpeed = 1f;
		super.onDisable();
	}
}
