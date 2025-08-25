package axyl.client.modules.features.movement;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class LongJump extends Module {

	public double x,y,z;
	public int ticksVerusL;
	public int offGroundTicks;
	public int onGroundTicks;
	
	public double yPortCounter;
	public double currentSpeed;
	public double lastPosY;
	
	private Setting lgMode;

	public LongJump() {
		super("LongJump", "", Keyboard.KEY_NONE, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Vanilla");
		options.add("SlowFall");
		options.add("WatchdogOLD");
		options.add("WatchdogOLD2");
		options.add("NoRules");
		options.add("VulcanH");
		options.add("Verus");
		
		Axyl.ins.settingManager.createSetting(this.lgMode = new Setting("Mode", this, "Vanilla", options));
		super.moduleSetup();
	}

	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		String mode = lgMode.getValString();
		this.suffix = mode;
		if(mode.equalsIgnoreCase("Vanilla")) {
			if(ticksExisted == 1) {
				MovementUtil.setSpeed(0);
			} else if(ticksExisted == 2){
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
					MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*3.5);
				}
			} else {
				if(mc.thePlayer.onGround) {
					this.toggle();
				}
			}
		}
		if(mode.equalsIgnoreCase("SlowFall")) {
			if(ticksExisted <= 37) {
				mc.thePlayer.motionY = 0;
				mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
				if(ticksExisted == 37) {
					Axyl.sendMessage("dmg");
					MovementUtil.hypixelDamage();
				}
			} else {
				if(mc.thePlayer.fallDistance > 0) { 
					mc.thePlayer.motionY += 0.02;
				}
			}
		}
		if(mode.equalsIgnoreCase("WatchdogOLD")) {
			float f = MovementUtil.getDir(mc.thePlayer.rotationYaw);
			if(ticksExisted <= 37) {
				mc.thePlayer.motionY = 0;
				mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
			} else {
				if(ticksExisted == 38) {
					MovementUtil.hypixelDamage();
				} else if(ticksExisted == 39) {
					if(mc.thePlayer.onGround) {
						MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
						mc.thePlayer.motionY = MovementUtil.jumpHeight();
					}
				}
				if(ticksExisted > 40) {
					if(mc.thePlayer.hurtTime > 0) {
			            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.06357f);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.06357f);
			            if(mc.thePlayer.ticksExisted % 2 == 0)
			            	mc.thePlayer.motionY += Math.abs((double)(MathHelper.sin(f) * 0.06357f));
					}
					if(ticksExisted == 43) {
						mc.thePlayer.motionY = MovementUtil.jumpHeight();
					}
					if(mc.thePlayer.fallDistance == 0) {
						if(mc.thePlayer.ticksExisted % 2 == 0)
						mc.thePlayer.motionY+=0.02;
					}
					if(mc.thePlayer.onGround) {
						this.toggle();
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("WatchdogOLD2")) {
			if(ticksExisted == 1) {
				if(mc.thePlayer.onGround) {
					MovementUtil.hypixelDamage();
				} else {
					this.toggle();
				}
			} else if(ticksExisted == 2) {
				if(mc.thePlayer.onGround) {
					PlayerCapabilities c = new PlayerCapabilities();
					for(int i = 0; i < 10; i++) {
						c.setFlySpeed(RandomUtils.nextFloat(0, 9));
						c.allowFlying = true;
						c.isFlying = true;
						PacketUtil.sendPacketNoEvent(new C13PacketPlayerAbilities(c));
					}
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
					currentSpeed = MovementUtil.getBaseMoveSpeed()*5;
				}
			} else if(ticksExisted > 3) {
				if(ticksExisted < 120) {
					MovementUtil.setSpeed((currentSpeed > 0.3 ? currentSpeed*=0.999 : 0.42));
				} else {
					MovementUtil.setSpeed((currentSpeed > 0.3 ? currentSpeed*=0.995 : 0.42));
				}
				mc.thePlayer.motionY = 0;
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY-RandomUtils.nextDouble(0.0, 0.0001), mc.thePlayer.posZ);
			}
		}
		if(mode.equalsIgnoreCase("VulcanH")) {
			if(ticksExisted == 1) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
				} else {
					this.toggle();
				}
			} else {
				if(mc.thePlayer.fallDistance > 0.75) {
					MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*1.21);
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+6, mc.thePlayer.posZ);
					mc.thePlayer.motionY = 1;
					this.toggle();
				}
			}
		}
		if(mode.equalsIgnoreCase("Verus")) {
			if(ticksExisted <= 8) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.setPosition(x, y, z);
					if(ticksExisted == 8) {
						MovementUtil.damagePlayer();
					}
				} else {
					this.toggle();
				}
			} else {
				if(ticksExisted == 9) {
					mc.thePlayer.motionY = 0.6;
				} else if(ticksExisted > 12) {
					mc.thePlayer.motionY = -0.0784000015258789;
					MovementUtil.setSpeed(9);
				}
				if(ticksExisted > 32) {
					MovementUtil.setSpeed(0);
					this.toggle();
				}
			}
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		String mode = lgMode.getValString();
		if(mode.equalsIgnoreCase("SlowFall")) {
			if(ticksExisted <= 37) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					event.setCancelled(true);
				}
			} else {
				if(event.getPacket() instanceof S12PacketEntityVelocity) {
					S12PacketEntityVelocity p = (S12PacketEntityVelocity)event.getPacket();
					if(ticksVerusL == 0) {
						double valX = (100 - (Math.abs(mc.thePlayer.motionX) / (Math.abs(mc.thePlayer.motionX) + Math.abs(p.getMotionX()/8000)))*100)/100+1;
						double valZ = (100 - (Math.abs(mc.thePlayer.motionZ) / (Math.abs(mc.thePlayer.motionZ) + Math.abs(p.getMotionZ()/8000)))*100)/100+1;
						Axyl.sendMessage(""+valX);
						Axyl.sendMessage(""+valZ);
						MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*2.6-Math.random()/10);
						mc.thePlayer.motionY += MovementUtil.jumpHeight() + 0.248125;
						ticksVerusL++;
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("WatchdogOLD")) {
			if(event.getPacket() instanceof C03PacketPlayer) {
				C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
				if(ticksExisted <= 37) {
					event.setCancelled(true);
				}
				if(ticksExisted > 40) {
					if(mc.thePlayer.fallDistance == 0) {
						if(mc.thePlayer.ticksExisted % 2 == 0)
						p.onGround = true;
					}
				}
			}
		}
		if(mode.equalsIgnoreCase("Verus")) {
			if(event.getPacket() instanceof C03PacketPlayer) {
				if(ticksExisted <= 11) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public void onEnable() {
		String mode = lgMode.getValString();
		x = mc.thePlayer.posX;
		y = mc.thePlayer.posY;
		z = mc.thePlayer.posZ;
		ticksVerusL = 0;
		mc.timer.timerSpeed = 1f;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
