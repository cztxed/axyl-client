package axyl.client.modules.features.movement;

import java.awt.Color;
import java.util.ArrayList; 
import java.util.Comparator;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;
import com.viaversion.viaversion.api.minecraft.BlockFace;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerPostUpdate;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPlayerSprint;
import axyl.client.events.player.EventSafewalk;
import axyl.client.events.player.EventScaffold;
import axyl.client.events.player.EventStrafe;
import axyl.client.events.player.EventTick;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.events.render.EventRender2D;
import axyl.client.events.render.EventRender3D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.other.RotationsHelper;
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.ItemUtil;
import axyl.client.util.player.MovementUtil;
import axyl.client.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Scaffold extends Module {

	public BlockPos blockPos;
	public EnumFacing bFace;
	
	public float yaw;
	public float pitch;
	
	public double keepYPos;
	public int tickD;
	
	public boolean canStartSprinting;

	public int sneakTicks;
	public float target_pitch;
	public float target_yaw;
	public float target_yaw_2;
	public boolean canSneak;
	public boolean shouldCancel;
	public boolean placedBlock;
	
	public float direction;
	public int shouldJump;
	
	public int wdTowerTick;
	public int ticksOnGround;
	public int ticksOffGround;
	public double lastOnGroundPosY;
	
	public BlockPos lastPlayerPos;
	
	public Setting yawMode;
	public Setting rayTrace;
	public Setting edge;
	public Setting allowSprinting;
	public Setting strictRayTrace;
	public Setting safewalkMode;
	public Setting towerMode;
	public Setting onTickCorrection;
	public Setting tickDelayVal;
	private Setting keepy;
	private Setting hypixelThingie;
	private Setting pitchMode;
	
	public Scaffold() {
		super("Scaffold", "", Keyboard.KEY_X, Category.Movement);
	}
	
	@Override
	public void moduleSetup() {
		
		ArrayList<String> rotationsYaw = new ArrayList<>();
		rotationsYaw.add("Backwards");
		rotationsYaw.add("Center");
		rotationsYaw.add("Nearest");
		rotationsYaw.add("Motion");
		rotationsYaw.add("Godbridge");
		rotationsYaw.add("None");
		
		ArrayList<String> rotationsPitch = new ArrayList<>();
		rotationsPitch.add("Normal");
		rotationsPitch.add("Locked");
		rotationsPitch.add("Semi-Locked");
		rotationsPitch.add("None");
		
		ArrayList<String> safewalk = new ArrayList<>();
		safewalk.add("Normal");
		safewalk.add("Sneak");
		safewalk.add("None");
		
		ArrayList<String> tower = new ArrayList<>();
		tower.add("Fast");
		tower.add("EarlyJump");
		tower.add("WatchdogOld");
		tower.add("Watchdog01.11.23");
		tower.add("Constant");
		tower.add("None");
		
		Axyl.ins.settingManager.createSetting(yawMode = new Setting("Yaw", this, "Backwards", rotationsYaw));
		Axyl.ins.settingManager.createSetting(pitchMode = new Setting("Pitch", this, "Locked", rotationsPitch));
		Axyl.ins.settingManager.createSetting(towerMode = new Setting("Tower", this, "None", tower));
		Axyl.ins.settingManager.createSetting(safewalkMode = new Setting("Safewalk mode", this, "None", safewalk));
		Axyl.ins.settingManager.createSetting(tickDelayVal = new Setting("Tick delay", this, 0, 0, 10, 0, false));
		Axyl.ins.settingManager.createSetting(allowSprinting = new Setting("Allow Sprinting", this, false, false));
		Axyl.ins.settingManager.createSetting(rayTrace = new Setting("Ray Trace", this, false, false));
		Axyl.ins.settingManager.createSetting(strictRayTrace = new Setting("Strict RayTrace", this, false, false));
		Axyl.ins.settingManager.createSetting(onTickCorrection = new Setting("OnTick correction", this, false, false));
		Axyl.ins.settingManager.createSetting(hypixelThingie = new Setting("Hypixel thing", this, false, false));
		Axyl.ins.settingManager.createSetting(keepy = new Setting("KeepY", this, false, false));
		Axyl.ins.settingManager.createSetting(edge = new Setting("Edge", this, false, false));
		super.moduleSetup();
	}

	@Subscribe
	public void eventPreUpdate(EventPlayerPreUpdate event) {
		if(keepy.getValBoolean()) {
			if(mc.thePlayer.isMoving()) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionY = MovementUtil.jumpHeight();
					MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed()*1.5-0.02);
				}
			}
		}
		if(mc.thePlayer.onGround) {
			lastOnGroundPosY = mc.thePlayer.posY;
			ticksOffGround = 0;
			ticksOnGround++;
		} else {
			ticksOffGround++;
			ticksOnGround = 0;
		}
		tickD++;
		event.setYaw(yaw);
		event.setPitch(pitch);
		mc.thePlayer.rotationPitchHead = event.getPitch();
	}
	
	@Subscribe
	public void eventPost(EventPlayerPostUpdate event) {

	}
	
	@Subscribe
	public void eventSprint(EventPlayerSprint event) {
		if(!allowSprinting.getValBoolean()) {
			event.setSprintState(false);
			event.setCancelled(true);
		}
	}
	
	@Subscribe
	public void eventSafeWalk(EventSafewalk event) {
		if(safewalkMode.getValString().equalsIgnoreCase("Normal")) {
			if(mc.thePlayer.onGround) {
				event.setCancelled(true);
			}
		}
	}
	
	@Subscribe
	public void eventJump(EventJump event) {
		String rot = yawMode.getValString();
		if(!rot.equalsIgnoreCase("None"))
		if(MovementCorrection.scaffold.getValBoolean()
		&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
			if(!rot.equalsIgnoreCase("Godbridge")) {
				event.setYaw(yaw+180);
			} else {
				event.setYaw(yaw+135);
			}
		}
		if(keepy.getValBoolean() || hypixelThingie.getValBoolean())
		event.setCancelled(true);
	}
	
	@Subscribe
	public void eventStrafe(EventStrafe event) {
		String rot = yawMode.getValString();
		if(!rot.equalsIgnoreCase("None"))
		if(MovementCorrection.scaffold.getValBoolean()
		&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
			if(!rot.equalsIgnoreCase("Godbridge")) {
				event.setYaw(yaw+180);
			} else {
				if(mc.thePlayer.isMoving()) {
					event.setYaw(yaw+180);
					float s = 0.98f;
					if(mc.gameSettings.keyBindSneak.isKeyDown()) {
						s = 0.29400003f;
					}
					if(!mc.thePlayer.onGround) {
						s = 0;
					}
					event.setStrafe(s);
				}
			}
		}
	}

	@Subscribe
	public void eventRotUpdate(EventUpdateRotation event) {
		{
			String tower = towerMode.getValString();
			if(blockPos != null) {
				if(!(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) || mc.thePlayer.getCurrentEquippedItem() == null)
					return;
				
				if(mc.gameSettings.keyBindJump.isKeyDown()) {
					if(tower.equalsIgnoreCase("Fast")) {
						if(mc.thePlayer.fallDistance == 0)
						if(mc.thePlayer.posY >= lastOnGroundPosY+0.755) {
							mc.thePlayer.motionY = 0.01D;
							mc.thePlayer.fallDistance = 1;
						}
					}
					if(tower.equalsIgnoreCase("EarlyJump")) {
						if(mc.thePlayer.fallDistance > 0)
						if(mc.thePlayer.posY <= lastOnGroundPosY+1.125) {
							mc.thePlayer.motionY = MovementUtil.jumpHeight();
							mc.thePlayer.fallDistance = 0;
						}
					}
					if(tower.equalsIgnoreCase("Watchdog01.11.23")) {
						if(mc.thePlayer.isMoving()) {
							if(mc.thePlayer.fallDistance > 0)
								if(mc.thePlayer.posY <= lastOnGroundPosY+1.125) {
									Axyl.sendMessage("niger");
									mc.thePlayer.motionY = MovementUtil.jumpHeight();
									mc.thePlayer.fallDistance = 0;
								}
								if(!bFace.equals(EnumFacing.UP)) {
									if(ticksOffGround == 4) {
										Axyl.sendMessage("niger2");
										mc.thePlayer.motionY -= 0.0165D;
									}
									if(ticksOffGround == 3) {
										Axyl.sendMessage("niger243");
										mc.thePlayer.motionY -= 0.00505D;
									}
									if(ticksOffGround == 8) {
										Axyl.sendMessage("degro");
										mc.thePlayer.motionY -= 0.01;
									}
								}
						}
					}
					if(tower.equalsIgnoreCase("WatchdogOld")) {
						if(mc.thePlayer.posY >= lastOnGroundPosY+0.75319998053) {
							if(mc.thePlayer.fallDistance == 0) {
								mc.thePlayer.motionY = 0;
								mc.thePlayer.fallDistance = 1;
							}
						}
					}
					if(tower.equalsIgnoreCase("Constant")) {
						if(bFace.equals(EnumFacing.UP))
						if(mc.thePlayer.posY >= lastOnGroundPosY+1.1) {
							if(mc.thePlayer.ticksExisted % 3 == 0) {
								mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY-0.15D, mc.thePlayer.posZ);
								mc.thePlayer.motionY = MovementUtil.jumpHeight();
							} else {
								if(mc.thePlayer.posY >= lastOnGroundPosY+2.5) {
									mc.thePlayer.motionY = RandomUtils.nextDouble(0.15, 0.22);
								}
							}
						}
					}
				}
			}
		}
		String rot = yawMode.getValString();
		if(!allowSprinting.getValBoolean()) {
			mc.thePlayer.setSprinting(false);
		}
		direction = Math.round(mc.thePlayer.rotationYaw / 45)*45;
		if(rot.equalsIgnoreCase("Backwards")) {
			target_yaw_2 = mc.thePlayer.rotationYaw+180.12f;
		}
		if(rot.equalsIgnoreCase("Motion")) {
			
			double mx = 0.0;
			double mz = 0.0;
			{
				mx = (mc.thePlayer.motionX*100);
				mz = (mc.thePlayer.motionZ*100);
				mx = MathUtils.clamp(mx, -1.32, 1.32);
				mz = MathUtils.clamp(mz, -1.32, 1.32);
			}
			float[] rots = getRotations(mc.thePlayer.posX-mx, mc.thePlayer.posY-1, mc.thePlayer.posZ-mz);
			if(mc.thePlayer.isMoving())
			if(mc.thePlayer.motionX != 0 && mc.thePlayer.motionZ != 0)
			yaw = rots[0];
		}
		
		final float[] dstAngle = {target_yaw_2, 0};
	    final float[] srcAngle = new float[]{target_yaw, 0};
	    float[] angles = MovementUtil.getConstantRotations(dstAngle, srcAngle);
	    target_yaw = angles[0]; 
		float sens = 0.54788734f;

		if(rot.equalsIgnoreCase("Godbridge")) {
			if(mc.thePlayer.onGround) {
				target_yaw = direction+225;
			} else {
				target_yaw = direction+180;
			}
			target_pitch = 77;
		}
		{
			int diff = (int) (target_pitch-pitch);
			diff = (int) MathUtils.clamp(diff, -15-(RandomUtils.nextInt(0, 3)-RandomUtils.nextInt(0, 3)), 15+(RandomUtils.nextInt(0, 3)-RandomUtils.nextInt(0, 3)));
			if(Math.abs(diff) == 1) {
				diff = 0;
			}
			if(diff > 0 && diff < 9) {
				diff = 9;
			}
			if(diff > -9 && diff < 0) {
				diff = -9;
			}
			float s = sens*0.75f;
			float f1 = s * s * s * 8.0F;
			float fin = diff * f1;
			pitch = (float) (pitch + fin * 0.15D);
		}
		{
			if(rot.equalsIgnoreCase("Center")) {
				target_yaw = Math.round(target_yaw / 22.5f) * 22.5f;
			}
			int diff = (int) (target_yaw-yaw)+(RandomUtils.nextInt(0, 1)-RandomUtils.nextInt(0, 1));
			diff = (int) MathUtils.clamp(diff, -19-RandomUtils.nextInt(0, 3), 19+RandomUtils.nextInt(0, 3));
			float f1 = sens * sens * sens * 8.0F;
			float fin = diff * f1;
			yaw = (float) (yaw + fin * 0.15D);
		}
		if(!rot.equalsIgnoreCase("None")) {
			if(blockPos == null) {
				target_yaw = mc.thePlayer.rotationYaw+180;
			}
		} else {
			yaw = mc.thePlayer.rotationYaw;
			pitch = mc.thePlayer.rotationPitch;
		}
		pitch = (float) MathUtils.clamp(pitch, -90, 90);
	}
	
	@Subscribe
	public void event2D(EventRender2D event) {
		String sneakMode = safewalkMode.getValString();
		ScaledResolution sr = new ScaledResolution(mc);
		if(Axyl.ins.modManager.getModuleByName("KillAura").isToggled())
			Axyl.ins.modManager.getModuleByName("KillAura").toggle();
		
		if(canSneak) {
			if(sneakMode.equalsIgnoreCase("Sneak"))
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false);
			
			canSneak = false;
		}
		
		{
			int blockCount = 0;
	        for (int i = 0; i < 45; ++i) {
	            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
	            	continue;
	            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
	            Item item = is.getItem();
	            if (!(is.getItem() instanceof ItemBlock)) continue;
	            blockCount += is.stackSize;
	        }
			float f = blockCount;
	        float f1 = 64;
	        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
	        int blockColor = Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
	        String s = "§r"+blockCount;
	        mc.fontRendererObj.drawStringWithShadow(s, sr.getScaledWidth()/2-mc.fontRendererObj.getStringWidth(s)/2+1, sr.getScaledHeight()/2+15, blockColor);
		}
	}

	@Subscribe
	public void eventTick(EventScaffold event) {
		String sneakMode = safewalkMode.getValString();

		String rot = yawMode.getValString();
		String rot2 = pitchMode.getValString();
		
		double mx = 0.0;
		double mz = 0.0;

		{
			mx = (mc.thePlayer.motionX*Integer.MAX_VALUE);
			mz = (mc.thePlayer.motionZ*Integer.MAX_VALUE);
			mx = MathUtils.clamp(mx, -0.22, 0.22);
			mz = MathUtils.clamp(mz, -0.22, 0.22);
		}
		
		double x = 0.0;
		double z = 0.0;
		
		if(edge.getValBoolean()) {
			x = (mc.thePlayer.motionX*Integer.MAX_VALUE);
			z = (mc.thePlayer.motionZ*Integer.MAX_VALUE);
			x = MathUtils.clamp(x, -0.125, 0.125);
			z = MathUtils.clamp(z, -0.125, 0.125);
		}
		if(hypixelThingie.getValBoolean()) {
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				if(mc.thePlayer.onGround && ticksOnGround <= 1) {
					mc.thePlayer.motionX *= 0.52;
					mc.thePlayer.motionZ *= 0.52;
				} else {
					if(ticksOnGround % 4 == 0) {
						mc.thePlayer.motionX *= 0.98;
						mc.thePlayer.motionZ *= 0.98;
					}
				}
			}
		}
		
		shouldJump++;
		if(shouldJump < 20) {
			if(mc.thePlayer.ticksExisted % RandomUtils.nextInt(3, 7) == 0) {
				mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
			}
		}
		if(rot2.equalsIgnoreCase("Locked")) {
			target_pitch = 83.046906f;
		}
		if(mc.thePlayer.onGround || !keepy.getValBoolean() || mc.gameSettings.keyBindJump.isKeyDown())
			keepYPos = (int)mc.thePlayer.posY;
		
		blockPos = getBlockData(new BlockPos(mc.thePlayer.posX, keepYPos-1, mc.thePlayer.posZ));
		if(event.isEventTick)
		if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, keepYPos-1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir) {
			if(blockPos != null && bFace != null) {
				if(!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock))
					return;
	
				double px = blockPos.getX() + bFace.getFrontOffsetX();
				double py = blockPos.getY() + bFace.getFrontOffsetY();
				double pz = blockPos.getZ() + bFace.getFrontOffsetZ();
				float rots[] = getRotations2(px, py, pz);
				
				if(rot2.equalsIgnoreCase("Normal")) {
					float[] rots2 = getDirectionToBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ(), bFace);
					target_pitch = rots2[1];
				}
				
				if(rot2.equalsIgnoreCase("Semi-Locked")) {
					float[] rots2 = getDirectionToBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ(), bFace);
					float v = 3.046906f;
					target_pitch = Math.round(rots2[1] / v) * v;
				}
					
				if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX-x, keepYPos-1, mc.thePlayer.posZ-z)).getBlock() instanceof BlockAir) {
	 				MovingObjectPosition mov = mc.thePlayer.rayTraceCustom(4.5f, 1f, yaw, pitch);
					if(sneakMode.equalsIgnoreCase("Sneak"))
						KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true);
					
					if((mc.thePlayer.onGround && bFace != EnumFacing.UP) || !mc.thePlayer.onGround) {
						if(rot.equalsIgnoreCase("Center")) {
							rots = getDirectionToBlock(blockPos.getX()-mx, blockPos.getY(), blockPos.getZ()-mz, bFace);
							target_yaw_2 = rots[0];
						}
						if(rot.equalsIgnoreCase("Nearest")) {
							target_yaw_2 = getDirectionToBlock(blockPos.getX()-0.49+Math.random()/5, blockPos.getY(), blockPos.getZ()-0.49+Math.random()/5, bFace)[0];
						}
					}
		 			if(rayTrace.getValBoolean()) {
			 			if(!MovementUtil.lookingAtBlock(blockPos, yaw, pitch, bFace, false)) {
			 				if(!bFace.equals(EnumFacing.UP)) {
								if(rot.equalsIgnoreCase("Center")) {
									target_yaw_2 = rots[0];
								}
								if(rot.equalsIgnoreCase("Nearest")) {
									target_yaw_2 = getDirectionToBlock(blockPos.getX()-0.5, blockPos.getY(), blockPos.getZ()-0.5, bFace)[0];
								}
			 				}
			 				tickD = 0;
			 				return;
			 			}
			 			if(strictRayTrace.getValBoolean())
			 			if(mov.sideHit != bFace) {
			 				tickD = 0;
			 				return;
			 			}
		 			}
		 			//if(mc.thePlayer.getDistance(lastPlayerPos.getX(), lastPlayerPos.getY(), lastPlayerPos.getZ()) > 0.2)
		 			if(tickD > tickDelayVal.getValDouble() || (bFace.equals(EnumFacing.UP) && mc.gameSettings.keyBindJump.isKeyDown())) {
		 				shouldJump = 0;
		 				shouldCancel = true;
		 				mc.thePlayer.swingItem();
		 				//mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
						mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), blockPos, bFace, new Vec3(blockPos.getX()+RandomUtils.nextDouble(0.2, 0.8), blockPos.getY()+RandomUtils.nextDouble(0.125, 0.25), blockPos.getZ()+RandomUtils.nextDouble(0.2, 0.8)));
		 			}
		 			
					if(sneakMode.equalsIgnoreCase("Sneak"))
						canSneak = true;
				}
			}
		} else {
			lastPlayerPos = mc.thePlayer.getPosition();
			tickD = 0;
			if(sneakMode.equalsIgnoreCase("Sneak"))
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false);

		}	
		if(mc.gameSettings.keyBindJump.isKeyDown()) {
			if(mc.thePlayer.onGround)
				mc.thePlayer.motionY = MovementUtil.jumpHeight();
		}
	}
	
	public void silentSneak() {
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		if(hypixelThingie.getValBoolean()) {
			if(mc.thePlayer.onGround && ticksOnGround <= 1) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
					if(mc.thePlayer.isMoving()) {
						p.onGround = false;
						p.y+=0.01;
					}
				}
			}
		}
	}
	
    public float[] getRotations2(final double posX, final double posY, final double posZ) {
        final EntityPlayerSP player = mc.thePlayer;
        final double x = posX - player.posX+0.5;
        final double y = posY - (player.posY-2.2);
        final double z = posZ - player.posZ+0.5;
        final double dist = MathHelper.sqrt_double((int)x * (int)x + (int)z * (int)z);
        final float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }
    
	@Subscribe
	public void eventRender3D(EventRender3D event) {
		if(blockPos == null)
			return;
		
		double x = blockPos.getX();
		double y = blockPos.getY();
		double z = blockPos.getZ();

        double posX = x - mc.getRenderManager().renderPosX;
        double posY = y - mc.getRenderManager().renderPosY;
        double posZ = z - mc.getRenderManager().renderPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(posX, posY, posZ);
        
        Color c = new Color(0xff808080);
        float red = c.getRed();
        float green = c.getGreen();
        float blue = c.getBlue();;
        float alpha = 45;
        float width = 1f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255);
        RenderUtil.drawBoundingBox(bb);
        GL11.glLineWidth(width);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
	}
	
    public float[] getDirectionToBlock(final double x, final double y, final double z, final EnumFacing enumfacing) {
        final EntityEgg var4 = new EntityEgg(mc.theWorld);
        var4.posX = x + 0.5;
        var4.posY = y - 3D;
        var4.posZ = z + 0.5;
        float yaw = getRotations(var4.posX, var4.posY, var4.posZ)[0];
        var4.posX = x + enumfacing.getFrontOffsetX();
        var4.posY = y - 2.9D;
        var4.posZ = z + enumfacing.getFrontOffsetZ();
        float pitch = getRotations(var4.posX, var4.posY, var4.posZ)[1];
        return new float[]{yaw, pitch};
    }
    
    public float[] getRotations(final double posX, final double posY, final double posZ) {
        final EntityPlayerSP player = mc.thePlayer;
        final double x = posX - player.posX;
        final double y = posY - (player.posY + (double) player.getEyeHeight());
        final double z = posZ - player.posZ;
        final double dist = MathHelper.sqrt_double(x * x + z * z);
        final float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }
    
	public EnumFacing getOppositeEnum(EnumFacing e) {
		if(e.equals(EnumFacing.EAST)) {
			return EnumFacing.WEST;
		}
		if(e.equals(EnumFacing.WEST)) {
			return  EnumFacing.EAST;
		}
		if(e.equals(EnumFacing.SOUTH)) {
			return  EnumFacing.NORTH;
		}
		if(e.equals(EnumFacing.NORTH)) {
			return  EnumFacing.SOUTH;
		}
		return null;
	}
	
    public BlockPos getBlockData(BlockPos pos) {
    	if (isBlockValid(pos.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos.add(0, -1, 0));
        }
    	if(!mc.thePlayer.getHorizontalFacing().equals(EnumFacing.WEST))
    	if (isBlockValid(pos.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos.add(-1, 0, 0));
        }
    	if (isBlockValid(pos.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos.add(1, 0, 0));
        }
    	if(!mc.thePlayer.getHorizontalFacing().equals(EnumFacing.NORTH)) {
            if (isBlockValid(pos.add(0, 0, -1))) {
            	bFace = EnumFacing.SOUTH;
            	return new BlockPos(pos.add(0, 0, -1));
            }
    	}
    	if (isBlockValid(pos.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos.add(0, 0, 1));
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
    	if (isBlockValid(pos1.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos1.add(0, -1, 0));
        }
    	if (isBlockValid(pos1.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos1.add(-1, 0, 0));
        }
    	if (isBlockValid(pos1.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos1.add(1, 0, 0));
        }
    	if (isBlockValid(pos1.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos1.add(0, 0, 1));
        }
    	if (isBlockValid(pos1.add(0, 0, -1))) {
    		bFace = EnumFacing.SOUTH;
        	return new BlockPos(pos1.add(0, 0, -1));
        }
        BlockPos pos2 = pos.add(1, 0, 0);
    	if (isBlockValid(pos2.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos2.add(0, -1, 0));
        }
    	if (isBlockValid(pos2.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos2.add(-1, 0, 0));
        }
    	if (isBlockValid(pos2.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos2.add(1, 0, 0));
        }
    	if (isBlockValid(pos2.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos2.add(0, 0, 1));
        }
    	if (isBlockValid(pos2.add(0, 0, -1))) {
    		bFace = EnumFacing.SOUTH;
        	return new BlockPos(pos2.add(0, 0, -1));
        }
        BlockPos pos3 = pos.add(0, 0, 1);
    	if (isBlockValid(pos3.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos3.add(0, -1, 0));
        }
    	if (isBlockValid(pos3.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos3.add(-1, 0, 0));
        }
    	if (isBlockValid(pos3.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos3.add(1, 0, 0));
        }
    	if (isBlockValid(pos3.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos3.add(0, 0, 1));
        }
    	if (isBlockValid(pos3.add(0, 0, -1))) {
    		bFace = EnumFacing.SOUTH;
        	return new BlockPos(pos3.add(0, 0, -1));
        }
        BlockPos pos4 = pos.add(0, 0, -1);
    	if (isBlockValid(pos4.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos4.add(0, -1, 0));
        }
    	if (isBlockValid(pos4.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos4.add(-1, 0, 0));
        }
    	if (isBlockValid(pos4.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos4.add(1, 0, 0));
        }
    	if (isBlockValid(pos4.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos4.add(0, 0, 1));
        }
    	if (isBlockValid(pos4.add(0, 0, -1))) {
    		bFace = EnumFacing.SOUTH;
        	return new BlockPos(pos4.add(0, 0, -1));
        }
        BlockPos pos5 = pos.add(0, -1, 0);
    	if (isBlockValid(pos5.add(0, -1, 0))) {
    		bFace = EnumFacing.UP;
        	return new BlockPos(pos5.add(0, -1, 0));
        }
    	if (isBlockValid(pos5.add(-1, 0, 0))) {
    		bFace = EnumFacing.EAST;
        	return new BlockPos(pos5.add(-1, 0, 0));
        }
    	if (isBlockValid(pos5.add(1, 0, 0))) {
    		bFace = EnumFacing.WEST;
        	return new BlockPos(pos5.add(1, 0, 0));
        }
    	if (isBlockValid(pos5.add(0, 0, 1))) {
    		bFace = EnumFacing.NORTH;
        	return new BlockPos(pos5.add(0, 0, 1));
        }
    	if (isBlockValid(pos5.add(0, 0, -1))) {
    		bFace = EnumFacing.SOUTH;
        	return new BlockPos(pos5.add(0, 0, -1));
        }
        return null;
    }
    
    public boolean isBlockValid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }
    
	@Override
	public void onEnable() {
		ticksOnGround = 0;
		keepYPos = (int)mc.thePlayer.posY;
		blockPos = null;
		mc.gameSettings.keyBindSprint.isKeyDown();
		canStartSprinting = false;
		if(hypixelThingie.getValBoolean()) {
			mc.thePlayer.setSprinting(false);
		}
		if(!allowSprinting.getValBoolean()) {
			mc.thePlayer.setSprinting(false);
		}
		canSneak = false;
		yaw = mc.thePlayer.rotationYaw;
		target_yaw = mc.thePlayer.rotationYaw+180;
		target_yaw_2 = mc.thePlayer.rotationYaw+180;
		target_pitch = 80.046906f;
		pitch = mc.thePlayer.rotationPitch;
		shouldCancel = false;
		direction = Math.round(mc.thePlayer.rotationYaw / 45)*45;
		tickD = 120;
		lastPlayerPos = mc.thePlayer.getPosition();
		sneakTicks = 0;
		wdTowerTick = 0;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(safewalkMode.getValString().equalsIgnoreCase("Sneak"))
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false);
		
		if(sneakTicks == 2) {
			PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING));
		}
		RotationsHelper.SYNC_ROTS = true;
		RotationsHelper.ROTS_TO_SYNC = new float[]{yaw,pitch};
		super.onDisable();
	}
}
