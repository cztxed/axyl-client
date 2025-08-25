package axyl.client.modules.features.movement;

import java.util.ArrayList; 

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventStrafe;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class Speed extends Module {

	public int offGroundTicks;
	public int onGroundTicks;
	
	public double yPortCounter;
	public double currentSpeed;
	public double lastSpeedBeforeDamage;
	public double lastPosY;
	
	public double dmgBoost;
	
	public boolean safeStart;
	public float smoothyaw;
	
	public float lastNCPYaw;
	public float lastStrafe;
	
	public double lastMotionX;
	public double lastMotionY;
	public double lastMotionZ;
	
	private Setting speedMode;
	private Setting boostMode;
	private Setting boost;
	private Setting boostTicks;

	public Speed() {
		super("Speed", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
				
		options.add("Vanilla");
		options.add("Ground Strafe");
		options.add("Hypixel Old");
		options.add("MatrixHop");
		options.add("NCP");
		options.add("YPort");
		options.add("Strafe");
		options.add("LegitStrafe");
		options.add("VulcanYPort");
		options.add("VerusLowHop");
		options.add("NoFriction");
		options.add("Smooth");
		options.add("Test");
		options.add("Test2");
		
		ArrayList<String> boost = new ArrayList<>();
		
		boost.add("Strafe");
		boost.add("Static");
		boost.add("Dynamic");

		Axyl.ins.settingManager.createSetting(speedMode = new Setting("Mode", this, "Vanilla", options));
		Axyl.ins.settingManager.createSetting(boostMode = new Setting("Damage boost", this, "Strafe", boost));
		Axyl.ins.settingManager.createSetting(this.boost = new Setting("Boost", this, false, false));
		Axyl.ins.settingManager.createSetting(boostTicks = new Setting("HurtTime", this, 5, 1, 10, 0, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void updateEvent(EventPlayerUpdate event) {
		String mode = speedMode.getValString();
		this.suffix = mode;
		
		float f = MovementUtil.getDir(mc.thePlayer.rotationYaw);
		double baseSpeed = MovementUtil.getBaseMoveSpeed();
		
		if(mc.thePlayer.onGround) {
			lastPosY = mc.thePlayer.posY;
			offGroundTicks = 0;
			onGroundTicks++;
		} else {
			offGroundTicks++;
			onGroundTicks = 0;
		}

		if(mode.equalsIgnoreCase("NCP")) {
			if(mc.thePlayer.isMoving()) {
				MovementUtil.setSpeed(MovementUtil.getSpeed());
				if(mc.thePlayer.onGround) {
					if(safeStart) {
						if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-0.1, mc.thePlayer.posZ)).getBlock() instanceof BlockIce) {
							MovementUtil.setSpeed(baseSpeed*1.5);
						} else {
							MovementUtil.setSpeed(baseSpeed*1.65);
						}
						safeStart = false;
					} else {
						if(mc.thePlayer.moveStrafing == 0) {
							MovementUtil.setSpeed(baseSpeed*1.75-0.02);
						} else {
							MovementUtil.setSpeed(baseSpeed*1.75-0.03);
						}
					}
					if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-0.1, mc.thePlayer.posZ)).getBlock() instanceof BlockIce) {
						MovementUtil.setSpeed(baseSpeed*1.5);
					}
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				}
			} else {
				safeStart = true;
			}
		}
		if (mode.equalsIgnoreCase("Strafe")) {
        	if(mc.thePlayer.isMoving()) {
    			if(mc.thePlayer.onGround) {
    				lastPosY = mc.thePlayer.posY;
    				mc.thePlayer.motionY = MovementUtil.jumpHeight();
    				MovementUtil.setSpeed(baseSpeed*1.5);
    			} else {
    				//Swing.sendMessage(""+lastPosY);
           			if(mc.thePlayer.posY >= lastPosY + 1.01 && mc.thePlayer.fallDistance == 0) {
        				if(!mc.thePlayer.isCollidedHorizontally) {
        					mc.thePlayer.motionY -= 0.12178784000015258789;
        				}
        			}
    			}
        	}
        }
		if(mode.equalsIgnoreCase("Vanilla")) {
			if(mc.thePlayer.isMoving()) {
				double groundSpeed = MovementUtil.getBaseMoveSpeed() * 1.5;
				if(mc.thePlayer.onGround) {	
					if(MovementUtil.getSpeed() <= groundSpeed)
					MovementUtil.setSpeed(groundSpeed);
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				}
			}
		}
		if(mode.equalsIgnoreCase("Smooth")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					smoothStrafe(baseSpeed*1.1-Math.random()*0.075);
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {

				}
			}
		}
		if(mode.equalsIgnoreCase("MatrixHop")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					MovementUtil.setSpeed(MovementUtil.getSpeed()*1.5+0.05);
					mc.thePlayer.motionX *= 1.021;
					mc.thePlayer.motionZ *= 1.021;
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				}
			}
		}
		if(mode.equalsIgnoreCase("YPort")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					if(mc.thePlayer.moveStrafing == 0) {
						MovementUtil.setSpeed(baseSpeed*1.74-0.02);
					} else {
						MovementUtil.setSpeed(baseSpeed*1.74-0.03);
					}
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {
					if(!mc.thePlayer.isCollidedHorizontally)
					if(mc.thePlayer.posY > lastPosY+0.5 && mc.thePlayer.fallDistance == 0) {
						mc.thePlayer.motionY = -0.0784000015258789;
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("VulcanYPort")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					yPortCounter++;
					MovementUtil.setSpeed(baseSpeed*RandomUtils.nextDouble(1.475, 1.5));
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {
	
				}
				if(!mc.thePlayer.isCollidedHorizontally) {
					if(yPortCounter % 2 == 0) {
						if(mc.thePlayer.posY > lastPosY && mc.thePlayer.fallDistance == 0) {
							MovementUtil.setSpeed(baseSpeed);
							mc.thePlayer.motionY = -0.0784000015258789;
						}
					} else {
						if(mc.thePlayer.posY > lastPosY + 1.01 && mc.thePlayer.fallDistance == 0) {
							MovementUtil.setSpeed(baseSpeed);
							mc.thePlayer.motionY = -0.0784000015258789;
						}
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("Hypixel Old")) {
			if(mc.thePlayer.isMoving()) {
				MovementUtil.setSpeed(MovementUtil.getSpeed()-0.001);
				if(mc.thePlayer.onGround) {
					//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.005D, mc.thePlayer.posZ);
					if(onGroundTicks >= 1) {
						MovementUtil.setSpeed(baseSpeed*1.75-0.02);
						mc.thePlayer.motionY = MovementUtil.jumpHeight();
					}
				} else {
					if(MovementUtil.getSpeed() < 0.24)
						MovementUtil.setSpeed(0.24);
				}
			}
		}
		if(mode.equalsIgnoreCase("NoFriction")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					if(onGroundTicks >= 1) {
						mc.thePlayer.jump();
					}
				} else {
					MovementUtil.setSpeed(MovementUtil.getSpeed()*1.02);
				}
			}
		}
		if(mode.equalsIgnoreCase("Ground Strafe")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
		            if(MovementUtil.getSpeed() <= (baseSpeed*1.5-0.02) && mc.thePlayer.hurtTime < 2) {
		            	MovementUtil.setSpeed(baseSpeed*1.5-0.02);
		            }
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				}
			}
		}
		if(mode.equalsIgnoreCase("LegitStrafe")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
		            MovementUtil.setSpeed(baseSpeed*1.5-0.02);
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				}
			}
		}
		if(boost.getValBoolean()) {
			String bmode = boostMode.getValString();
			if(mc.thePlayer.hurtTime > (int)boostTicks.getValDouble()-1) {
				if(bmode.equalsIgnoreCase("Strafe")) {
					if(dmgBoost > 0) {
						MovementUtil.setSpeed(Math.abs(dmgBoost));
						dmgBoost = 0;
					} else {
						MovementUtil.setSpeed(MovementUtil.getSpeed()*0.98);
					}
				}
				if(bmode.equalsIgnoreCase("Static")) {
					MovementUtil.setSpeed(lastSpeedBeforeDamage*1.25);
				}
			}
			if(bmode.equalsIgnoreCase("Dynamic")) {
				if(mc.thePlayer.hurtTime > 0) {
					if(mc.thePlayer.hurtTime != 10) {
						if(Math.abs(lastMotionX) <= Math.abs(mc.thePlayer.motionX)) {
							mc.thePlayer.motionX *=1.1+Math.abs(lastMotionX);
						}
						if(Math.abs(lastMotionZ) <= Math.abs(mc.thePlayer.motionZ)) {
							mc.thePlayer.motionZ *=1.1+Math.abs(lastMotionZ);
						}
					}
				}
			}
			if(mc.thePlayer.hurtTime == 0) {
				lastSpeedBeforeDamage = MovementUtil.getSpeed();
			}
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		double baseSpeed = MovementUtil.getBaseMoveSpeed();
		String mode = speedMode.getValString();
		if(event.getPacket() instanceof S12PacketEntityVelocity) {
			S12PacketEntityVelocity p = (S12PacketEntityVelocity)event.getPacket();
			
			double motionX = p.getMotionX()/8000; 
			double motionZ = p.getMotionZ()/8000;
			if(Math.sqrt(motionX * motionX + motionZ * motionZ) > MovementUtil.getSpeed())
			dmgBoost = Math.sqrt(motionX * motionX + motionZ * motionZ);
			
			lastMotionX = p.getMotionX()/8000;
			lastMotionY = p.getMotionY()/8000;
			lastMotionZ = p.getMotionZ()/8000;
		}
		if(mode.equalsIgnoreCase("Hypixel Old")) {
			if(event.getPacket() instanceof S08PacketPlayerPosLook) {
				currentSpeed = 0;
			}
		}
		if(mode.equalsIgnoreCase("Smooth")) {
        	if(event.getPacket() instanceof C03PacketPlayer) {
        		C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
        		if(mc.thePlayer.isMoving()) {
        			if(offGroundTicks == 3) {
        				//mc.thePlayer.motionY = -0.01;
        			}
        			if(mc.thePlayer.posY >= lastPosY) {
        				if(getDistanceToGround(mc.thePlayer) <= 1.14) {
        					smoothStrafe(MovementUtil.getSpeed());
			        		if(mc.thePlayer.posY < lastPosY + 0.8 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 1) {
			        			p.onGround = true;
			        		}
        				}
		        		if(mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.fallDistance == 0) {
		        			smoothStrafe(MovementUtil.getSpeed());
		        		} else {
			        		if(mc.thePlayer.ticksExisted % 4 == 0 && mc.thePlayer.fallDistance > 0) {
			        			smoothStrafe(MovementUtil.getSpeed());
			        		}
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.8 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.5) {
		        			smoothStrafe(MovementUtil.getSpeed());
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.9 &&  mc.thePlayer.fallDistance == 0) {
		        			smoothStrafe(MovementUtil.getSpeed());
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.5) {
		        			smoothStrafe(MovementUtil.getSpeed());
		        		}
		        		if((mc.thePlayer.posY < lastPosY + 0.4 &&  mc.thePlayer.fallDistance == 0) || (mc.thePlayer.posY > lastPosY + 1.05 && mc.thePlayer.fallDistance == 0) || (mc.thePlayer.posY < lastPosY + 0.5 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.5)) {
		        			smoothStrafe(MovementUtil.getSpeed());
		        			p.onGround = true;
		        		}
        			}
        		}
        	}
		}
		if(mode.equalsIgnoreCase("VulcanYPort")) {
        	if(event.getPacket() instanceof C03PacketPlayer) {
        		C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
				if(mc.thePlayer.isMoving()) {
					if(!mc.thePlayer.isCollidedHorizontally) {
						if(yPortCounter % 2 != 0) {
							if(mc.thePlayer.posY > lastPosY + 1.01 && mc.thePlayer.fallDistance == 0) {
								p.onGround = true;
							}
						}
					}
				}
        	}
		}
		if(mode.equalsIgnoreCase("Strafe")) {
        	if(event.getPacket() instanceof C03PacketPlayer) {
        		C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
        		if(mc.thePlayer.isMoving()) {
        			if(mc.thePlayer.posY >= lastPosY) {
        				if(getDistanceToGround(mc.thePlayer) <= 1.14) {
        					strafe();
			        		if(mc.thePlayer.posY < lastPosY + 0.8 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 1) {
			        			p.onGround = true;
			        		}
        				}
		        		if(mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.fallDistance == 0) {
		        			strafe();
		        		} else {
			        		if(mc.thePlayer.ticksExisted % 4 == 0 && mc.thePlayer.fallDistance > 0) {
			        			strafe();
			        		}
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.8 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.5) {
		        			strafe();
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.9 &&  mc.thePlayer.fallDistance == 0) {
		        			strafe();
		        		}
		        		if(mc.thePlayer.posY < lastPosY + 0.5) {
		        			strafe();
		        		}
		        		if((mc.thePlayer.posY < lastPosY + 0.4 &&  mc.thePlayer.fallDistance == 0) || (mc.thePlayer.posY > lastPosY + 1.05 && mc.thePlayer.fallDistance == 0) || (mc.thePlayer.posY < lastPosY + 0.5 && mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.5)) {
		                    strafe();
		        			p.onGround = true;
		        		}
        			}
        		}
        	}
        }
		if(mode.equalsIgnoreCase("VerusLowHop")) {
			if(mc.thePlayer.isMoving())
			if(Math.abs(mc.thePlayer.posY - lastPosY) < 0.5 && mc.thePlayer.posY >= lastPosY) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					if(mc.thePlayer.ticksExisted % 2 == 0) {
				    	if (mc.thePlayer.moveForward > 0.0f && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
				    		MovementUtil.setSpeed(baseSpeed*1.85-0.06);
				    	} else {
				    		MovementUtil.setSpeed(baseSpeed*1.25-0.06);
				    	}
				 		mc.thePlayer.motionY = -0.0784000015258789;
						p.onGround = true;
					} else {
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionY = MovementUtil.jumpHeight();
	 					}
						p.onGround = false;
					}
				}
			}
		}
	}
	
	@Subscribe
	public void eventStrafe(EventStrafe event) {
		String mode = speedMode.getValString();
		float y = Math.round(event.getYaw() / 45) * 45;
		if(mode.equalsIgnoreCase("Test2")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					this.currentSpeed = 0.35;
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {
					if(mc.thePlayer.isCollidedHorizontally) {
						currentSpeed*=0.94;
					}
					currentSpeed*=0.98;
					MovementUtil.setSpeed(currentSpeed);
				}
			}
		}
		if(mode.equalsIgnoreCase("Test")) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					MovementUtil.setSpeed(0.45);
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {
					if(lastStrafe != event.getStrafe()) {
						MovementUtil.setSpeed(MovementUtil.getSpeed()*0.73);
					}
					if(MovementUtil.getSpeed() <= 0.2) {
						MovementUtil.setSpeed(0.22);
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("LegitStrafe")) {
			if(mc.thePlayer.isMoving()) {
				if(safeStart) {
					lastStrafe = event.getStrafe();
					safeStart = false;
				}
				if(event.getStrafe() != 0)
				if(event.getStrafe() != lastStrafe) {
					if(!mc.thePlayer.onGround)
					MovementUtil.setSpeed(0.16-Math.random()/20);
				}
			}
		}
		lastStrafe = event.getStrafe();
		smoothyaw = mc.thePlayer.rotationYaw;
	}
	
	
	@Subscribe
	public void eventJump(EventJump event) {
		String mode = speedMode.getValString();
		if(mode.equalsIgnoreCase("LegitStrafe")) {
			if(mc.thePlayer.isMoving()) {
		        double forward = mc.thePlayer.moveForward;
		        double strafe = mc.thePlayer.moveStrafing;
		        
		        float yaw = mc.thePlayer.rotationYaw;
		        if (forward != 0.0) {
		            if (strafe > 0.0) {
		                yaw += ((forward > 0.0) ? -45 : 45);
		            }
		            else if (strafe < 0.0) {
		                yaw += ((forward > 0.0) ? 45 : -45);
		            }
		            strafe = 0.0;
		            if (forward > 0.0) {
		                forward = 1.0;
		            }
		            else if (forward < 0.0) {
		                forward = -1.0;
		            }
		        }
		        if (strafe > 0.0) {
		            strafe = 1.0;
		        }
		        else if (strafe < 0.0) {
		            strafe = -1.0;
		        }
		        event.setYaw(yaw);
			}
		}
	}
	
	public static float getDistanceToGround(Entity e) {
        if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            return 0.0f;
        } else {
            float y = (float)e.posY;
            while(y > 0.0f) {
                int[] stairsIDS = { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
                int[] exemptIDS = { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177 };
                Block block = mc.theWorld.getBlockState(new BlockPos(e.posX, y - 1.0f, e.posZ)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (Block.getIdFromBlock(block) == 44 || Block.getIdFromBlock(block) == 126) {
                        return ((float)(e.posY - y - 0.5) < 0.0f) ? 0.0f : ((float)(e.posY - y - 0.5));
                    }
                    int[] arrayOfInt1;
                    for (int j = (arrayOfInt1 = stairsIDS).length, i = 0; i < j; ++i) {
                        final int id = arrayOfInt1[i];
                        if (Block.getIdFromBlock(block) == id) {
                            return ((float)(e.posY - y - 1.0) < 0.0f) ? 0.0f : ((float)(e.posY - y - 1.0));
                        }
                    }
                    for (int j = (arrayOfInt1 = exemptIDS).length, i = 0; i < j; ++i) {
                        final int id = arrayOfInt1[i];
                        if (Block.getIdFromBlock(block) == id) {
                            return ((float)(e.posY - y) < 0.0f) ? 0.0f : ((float)(e.posY - y));
                        }
                    }
                    return (float)(e.posY - y + block.getBlockBoundsMaxY() - 1.0);
                } else {
                    --y;
                }
            }
        }
        return 0.0f;
    }
	
	public void smoothStrafe(double sp) {
        double forward = mc.thePlayer.moveForward;
        double strafe = mc.thePlayer.moveStrafing;
        
        float yaw = mc.thePlayer.rotationYaw;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            }
            else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            }
            else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        }
        else if (strafe < 0.0) {
            strafe = -1.0;
        }
        
        smoothyaw = Math.round(MovementUtil.getConstantRotation(smoothyaw, yaw) * 45)/45;
        yaw = smoothyaw;
        
        double offsetX = Math.cos(Math.toRadians(yaw + 90.0f));
        double offsetZ = Math.sin(Math.toRadians(yaw + 90.0f));
        
        double speed = sp;
        
        mc.thePlayer.motionX = forward * speed * offsetX + strafe * speed * offsetZ;
        mc.thePlayer.motionZ = forward * speed * offsetZ - strafe * speed * offsetX;
	}
	
	public void strafe() {
		MovementUtil.setSpeed(MovementUtil.getSpeed());
	}
	
	@Override
	public void onEnable() {
		lastMotionX = mc.thePlayer.motionX;
		lastMotionY = mc.thePlayer.motionY;
		lastMotionZ = mc.thePlayer.motionZ;
		safeStart = true;
		smoothyaw = mc.thePlayer.rotationYaw;
		yPortCounter = 0;
		lastPosY = mc.thePlayer.posY;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
		super.onDisable();
	}
}
