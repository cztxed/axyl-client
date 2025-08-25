package axyl.client.modules.features.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;
import com.ibm.icu.util.CharsTrie;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.*;
import axyl.client.events.render.*;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.movement.MovementCorrection;
import axyl.client.modules.features.other.RotationsHelper;
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.player.MovementUtil;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;
import axyl.client.util.world.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;

public class KillAura extends Module {

	public static Entity target;
	
	public Timer cpsTimer = new Timer();
	public Timer cpsTimerPre = new Timer();
	public Timer postRots = new Timer();
	
	public double GLSCALE;
	public double TARGETSCALE;
	
	public double tx,ty,tz;
	public double healthAnimation;
	public static boolean isBlocking;
	public boolean shouldUnBlock;
	public boolean canAttackThePlayer;
	private int ticksAfterLastAttack;
	
	public float BASE_YAW;
	public float BASE_PITCH;

	public float REAL_BASE_YAW;
	public float REAL_BASE_PITCH;
	
	public boolean x;

	public static float YAW;
	public static float PITCH;
	public float lastYAW;
	public float lastPITCH;
	
	public int VECTOR_RANDOMIZER_Y;
	public int VECTOR_RANDOMIZER_P;
	
	public float TARGET_SMOOTHING;
	public float SMOOTHING;

	public float DIFFY;
	public float DIFFP;

	public double PRE_AIM_RANGE;
	public double RANGE;
	
	public static String abMode;

	public String abEvent;

	private int[] kList = {1,-1,0,-2,-1,1,2,-2,1,-1,0,-1,0,-1,2,-2,1,1,0,-1,-2};
	public int kurtois;
	private final Queue<Long> cpsList = new LinkedList<>();
	private final ArrayList<Integer> avg = new ArrayList<>();
	private final Timer xCooldown = new Timer();
	private final Timer avgTimer = new Timer();
	private final Timer timer = new Timer();
	public int lastAvg;
	public int RANDOM_CPS;
	
	public static Setting autoBlockMode;
	private Setting attackInvisibles;
	private Setting teams;
	private Setting reachVal;
	private Setting preAimVal;
	private Setting rotationSpeed;
	private Setting keepSprint;
	private Setting autoBlock;
	private Setting rayTrace;
	private Setting eventBlockMode;
	private Setting prediction;

	private Setting cps;
	
	public KillAura() {
		super("KillAura", "", Keyboard.KEY_NONE, Category.Combat);
	}
	
	@Override
	public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		ArrayList<String> events = new ArrayList<>();
		
		options.add("Vanilla");
		options.add("Legit");
		options.add("NCP");

		events.add("Update");
		events.add("Pre");
		events.add("Post");
		events.add("Tick");
		
		Axyl.ins.settingManager.createSetting(autoBlockMode = new Setting("AutoBlock Mode", this, "Vanilla", options));
		Axyl.ins.settingManager.createSetting(eventBlockMode = new Setting("AutoBlock Event", this, "Pre", events));
		
		Axyl.ins.settingManager.createSetting(cps = new Setting("Click Per Second", this, 12, 1, 20, 0, false));
		Axyl.ins.settingManager.createSetting(reachVal = new Setting("Range", this, 3, 3, 6, 1, false));
		Axyl.ins.settingManager.createSetting(preAimVal = new Setting("Pre aim range", this, 2, 0, 4, 0, false));
		Axyl.ins.settingManager.createSetting(rotationSpeed = new Setting("Rotation Speed", this, 100, 25, 100, 0, false));
		Axyl.ins.settingManager.createSetting(rayTrace = new Setting("RayTrace", this, false, false));
		Axyl.ins.settingManager.createSetting(prediction = new Setting("Prediction", this, false, false));
		Axyl.ins.settingManager.createSetting(keepSprint = new Setting("KeepSprint", this, false, false));
		Axyl.ins.settingManager.createSetting(autoBlock = new Setting("AutoBlock", this, false, false));
		Axyl.ins.settingManager.createSetting(teams = new Setting("Teams", this, true, false));
		Axyl.ins.settingManager.createSetting(attackInvisibles = new Setting("Invisibles", this, true, false));
		super.moduleSetup();
	}

	@Subscribe
	public void eventStrafe(EventStrafe event) {
		if(MovementCorrection.killAura.getValBoolean()
		&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
			event.setYaw(YAW);
			if(mc.thePlayer.ticksExisted % 20 == 0) {
				event.setStrafe(0);
			}
		}
	}
	
	@Subscribe
	public void eventJump(EventJump event) {
		if(MovementCorrection.killAura.getValBoolean()
		&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled())
		event.setYaw(YAW);
	}

	@Subscribe
	public void eventRayTrace(EventRayTrace event) {
		if(postRots.getTimePassed() <= 750) {
			event.setYaw(YAW);
			event.setPitch(PITCH);
		}
	}
	
	@Subscribe
	public void eventTick(EventTick event) {
		abMode = autoBlockMode.getValString();
		abEvent = eventBlockMode.getValString();
		ticksAfterLastAttack++;
		this.PRE_AIM_RANGE = preAimVal.getValDouble();
		this.RANGE = reachVal.getValDouble();
		
		if(target != null)
		if( (rayTrace.getValBoolean() && EntityUtil.getMouseOver(YAW, PITCH, 0, this.RANGE) != null) 
		|| (!rayTrace.getValBoolean() && mc.thePlayer.getDistanceToEntity(this.target) <= this.RANGE+0.09)) {
			canAttackThePlayer = true;
		}
		
		if(kurtois >= kList.length)
			kurtois = 0;
		{
			if(!cpsList.isEmpty())
			while (cpsList.peek() < System.currentTimeMillis()) {
				cpsList.remove();
			}
		}
		
		float a = 0;
		for(int i = 0; i < avg.size(); i++) {
			a+=avg.get(i);
		}
		
		a = a/avg.size();
		if(avg.size() >= 5) {
			avg.remove(0);
		}
		
		if(avgTimer.getTimePassed() > 1000) {
			if(Math.abs(a-lastAvg) > 0.25f) {
				lastAvg = (int) a;
				avgTimer.reset();
			}
		}
		
        int l = 50; 
        int kurtoisFix = kList[kurtois];
        
        if(cpsList.size() >= RANDOM_CPS) {
            l = (int) (Math.round(RandomUtils.nextInt(150, 200) / 50) * 50)-kurtoisFix;
        } else {
            l = (int) (Math.round(RandomUtils.nextInt(0, 50) / 50) * 50)+kurtoisFix;
        }
        
        if(!avgTimer.hasReached(Math.round(RandomUtils.nextInt(325, 550) / 50) * 50)) {
        	l+=Math.round(RandomUtils.nextInt(0, 150) / 50) * 50;
        }
        
    	if(mc.thePlayer.hurtTime == 0) {
    		x = false;
    	}
    	
        if(!keepSprint.getValBoolean()) {
        	if(mc.thePlayer.hurtTime == 10) {
        		if(xCooldown.hasReached(250)) {
        			x = true;
        			xCooldown.reset();
        		}
        	}
        }
        
		if(target != null) {
			boolean block = this.target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
			postRots.reset();
			if(canAttackThePlayer) {
				ticksAfterLastAttack = 0;
				if(cpsTimer.hasReached(l) || x) {
					{
						cpsList.add(System.currentTimeMillis() + 1000L);
		            	RANDOM_CPS = (int) (cps.getValDouble()+(RandomUtils.nextInt(0, 5)-RandomUtils.nextInt(0, 3)));
		    			kurtois++;
		    			avg.add(cpsList.size());
					}
					TARGET_SMOOTHING = RandomUtils.nextFloat(0, 1)-RandomUtils.nextFloat(0, 1);
					if(Axyl.ins.modManager.getModuleByName("Criticals").isToggled()) {
						Criticals.doCrits = true;
					} else {
						Criticals.doCrits = true;
					}
			        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
					if(!abMode.equalsIgnoreCase("Legit") || (abMode.equalsIgnoreCase("Legit"))) {
						mc.thePlayer.swingItem();
						if(keepSprint.getValBoolean()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
						} else {
							mc.playerController.attackEntity(mc.thePlayer, this.target);
						}
					}
					x = false;
					cpsTimer.reset();
				} else {
			    	if (mc.thePlayer.moveForward > 0.0f && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
		    			mc.thePlayer.setSprinting(true);
			    	}
			    	if(block)
					if(autoBlock.getValBoolean())
					if(abMode.equalsIgnoreCase("Legit"))
				    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				}
				EventHitEntity eventHitEntity = new EventHitEntity(target);
				eventHitEntity.hook(eventHitEntity);
				cpsTimerPre.reset();
			} else {
				{
			     	RANDOM_CPS = (int) (cps.getValDouble()+(RandomUtils.nextInt(0, 2)-RandomUtils.nextInt(0, 2)));
		        	cpsList.clear();
					avg.clear();
					avgTimer.reset();
				}
				if(postRots.getTimePassed() <= 750)
				if(autoBlock.getValBoolean())
				if(abMode.equalsIgnoreCase("Legit"))
		        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
				if(cpsTimerPre.hasReached(l)) {
					if(!mc.thePlayer.isBlocking()) {
						mc.thePlayer.swingItem();
					}
					cpsTimerPre.reset();
				}
			}
		}
		if(mc.thePlayer.isBlocking()) {
			boolean block = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
			if(block)
			if(autoBlock.getValBoolean())
			if(abMode.equalsIgnoreCase("Legit"))
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		if(abEvent.equalsIgnoreCase("Tick")) {
			autoBlock();
		}
	}
	
	@Subscribe
	public void eventPacket(EventPacket event) {
		if(abMode.equalsIgnoreCase("Legit")) {
			isBlocking = false;
			shouldUnBlock = false;
		}
		if(isBlocking) {
			if(event.getPacket() instanceof C08PacketPlayerBlockPlacement
			|| event.getPacket() instanceof C07PacketPlayerDigging) {
				event.setCancelled(true);
			}
		}
	}
	
	@Subscribe
	public void eventUpdate(EventPlayerUpdate event) {
		if(abEvent.equalsIgnoreCase("Update")) {
			autoBlock();
		}
		this.suffix = "Legit";
	}
	
	@Subscribe
	public void eventPreUpdate(EventPlayerPreUpdate event) {
		if(abEvent.equalsIgnoreCase("Pre")) {
			autoBlock();
		}
		if(Axyl.ins.modManager.getModuleByName("Scaffold").isToggled())
			return;
	
		event.setYaw(YAW);
		event.setPitch((float) MathUtils.clamp(PITCH, -90, 90));
		mc.thePlayer.rotationPitchHead = PITCH;
	}

	@Subscribe
	public void eventPostUpdate(EventPlayerPostUpdate event) {
		if(abEvent.equalsIgnoreCase("Post")) {
			autoBlock();
		}
	}
	
	@Subscribe
	public void eventRender2D(EventRender2D event) {
		if(target != null) {
			if(target instanceof EntityPlayer) {
				ScaledResolution sr = new ScaledResolution(mc);
				AbstractClientPlayer target = (AbstractClientPlayer) this.target;
				int healthColor = 0;
				{
			        float f = target.getHealth();
			        float f1 = target.getMaxHealth();
			        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
			        healthColor = Color.HSBtoRGB(f2 / 3.0F, 1.0F, 0.75F) | 0xFF000000;
				}
				int width = (int) Fonts.roboto_small.getStringWidth(target.getName())+63;
				width = (int) MathUtils.clamp(width, 90, width);
				int height = 35;
				int x = sr.getScaledWidth()/2-170;
				int y = sr.getScaledHeight()/2+20;
				
				if(healthAnimation != target.getHealth()) {
					healthAnimation += (target.getHealth()-healthAnimation)*0.05;
				}
				GL11.glPushMatrix();
				GL11.glScaled(GLSCALE, GLSCALE, 0);
				if(GLSCALE != 1) {
					GLSCALE+=(1-GLSCALE)*0.1;
				}
				double sc = GLSCALE;
				if(GLSCALE > 0.9) {
			        GL11.glTranslated((x)*(1-sc), (y)*(1-sc), 0);
			        GL11.glScaled(sc, sc, 0);
					healthAnimation = MathUtils.clamp(healthAnimation, 0, 20);
					RenderUtil.drawBorderedRoundedRect(x, y+10, width, height, 0, 2, new Color(22, 22, 28, 40).getRGB(), new Color(22, 22, 28, 100).getRGB());
					RenderUtil.drawRect(x, y+10, height, height, new Color(22, 22, 28, 100).getRGB());
					double b = width-41;
					double w = (b) * (healthAnimation - 0) / (20 - 0);
					RenderUtil.drawBorderedRoundedRect(x+38, y+39, b, 4, 2, 2, 0, healthColor+0x8f000000);
					RenderUtil.drawBorderedRoundedRect(x+38, y+40, w, 2, 2, 2, healthColor, healthColor);
					Fonts.roboto_bold.drawString(target.getName(), x+38, y+15, -1);
					Fonts.roboto_small.drawString("Health: "+MathUtils.roundToPlace(mc.thePlayer.getHealth(), 1), x+38, y+25, -1);
	    			NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
	    			int p = 0;
	    			if(networkPlayerInfo != null) {
	                    final int ms = networkPlayerInfo.getResponseTime();
	    				p = ms;
	                }
	                Fonts.roboto_small.drawString("Ping: " + p + "ms", x+38, y+32f, -1);
					try {
			            ResourceLocation skin = target.getLocationSkin();
			            mc.getTextureManager().bindTexture(skin);
			            Gui.drawScaledCustomSizeModalRect(x+3, y+13, 8, 8, 8, 8, 29, 29, 64, 64);
			        } catch (Exception ignored) {
			        }
					GL11.glScaled(0.5, 0.5, 0);
				}
				GL11.glPopMatrix();
			}
		} else {
			GLSCALE = 0;
		}
	}
	
	@Subscribe
	public void eventUpdateRot(EventUpdateRotation event) {
		if(mc.theWorld == null)
			return;

		target = getClosestTarget(this.RANGE + PRE_AIM_RANGE);
		
		{
			if(target != null) {
				if(!(rayTrace.getValBoolean() && EntityUtil.getMouseOver(YAW, PITCH, 0, this.RANGE) != null) 
				|| !(!rayTrace.getValBoolean() && mc.thePlayer.getDistanceToEntity(this.target) <= this.RANGE+0.09)) {
					canAttackThePlayer = false;
				}
			} else {
				canAttackThePlayer = false;
			}
		}

		float sens = (float) (Math.abs(mc.gameSettings.mouseSensitivity-MathUtils.roundToPlace(mc.gameSettings.mouseSensitivity, 1))+MathUtils.roundToPlace((rotationSpeed.getValDouble()/100), 2));
		if(target != null) {
			tx = target.posX;
			ty = target.posY;
			tz = target.posZ;
			{
				/* Setting base yaw/pitch */
				float[] rots = getRotations(target);
				{	
			    	final float[] firstAngle = {rots[0], rots[1]};
			        final float[] secondAngle = new float[]{BASE_YAW, BASE_PITCH};
			        float[] angles = MovementUtil.getConstantRotations(firstAngle, secondAngle);
			        REAL_BASE_YAW = angles[0];
			        REAL_BASE_PITCH = angles[1];
				}
				{
			    	final float[] firstAngle = {rots[0], rots[1]};
			        final float[] secondAngle = new float[]{BASE_YAW, BASE_PITCH};
			        float[] angles = getSmoothedAngles(firstAngle, secondAngle);
			        BASE_YAW = angles[0];
			        BASE_PITCH = angles[1];
				}
			}
			int dY = Math.abs((int) (REAL_BASE_YAW-YAW));
			int dP = Math.abs((int) (REAL_BASE_PITCH-PITCH));
			int d = dY + dP;
			if(d > 5) {
				dP = (int) MathUtils.clamp(dP, -90, 90);
				dY = (int) MathUtils.clamp(dY, -90, 90);
				VECTOR_RANDOMIZER_Y = (int) (-dP*RandomUtils.nextDouble(0.35, 0.75));
				VECTOR_RANDOMIZER_P = (int) (dY*RandomUtils.nextDouble(0.35, 0.75));
			} else{
				VECTOR_RANDOMIZER_Y = 0;
				VECTOR_RANDOMIZER_P = 0;
			}
			{
				int diff = 0;
				if(EntityUtil.getMouseOver(YAW, PITCH, -RandomUtils.nextDouble(0.19, 0.27), this.RANGE) == null
				|| !EntityUtil.getMouseOver(YAW, PITCH, -0.1, this.RANGE).equals(target)) {
					diff = (int) (REAL_BASE_YAW-YAW);
				} else {
					diff = 0;
				}
				int r2 = RandomUtils.nextInt(8, 13);
				diff = Math.round(diff / r2) * r2;
				int r = RandomUtils.nextInt(2, 5);
				if(diff > 0 && diff < r) {
					diff = r;
				}
				if(diff > -r && diff < 0) {
					diff = -r;
				}
				diff = (int) MathUtils.clamp(diff, -rotationSpeed.getValDouble()*1.5-RandomUtils.nextInt(0, 4), rotationSpeed.getValDouble()*1.5+RandomUtils.nextInt(0, 4));
				DIFFY+=(diff-DIFFY)*0.2;
				if(Math.abs(DIFFY) < 1) {
					DIFFY = 0;
				}
				float s = sens;
				if(Math.abs(REAL_BASE_YAW-YAW) > 40) {
					s = s*0.75f;
				}
				float f1 = s * s * s * 8.0F;
				float fin = (int)DIFFY * f1;
				YAW = (float) (YAW + fin * 0.15D);
			}
			{
				int diff;
				if(EntityUtil.getMouseOver(YAW, PITCH, -RandomUtils.nextDouble(0.19, 0.27), this.RANGE) == null
				|| !EntityUtil.getMouseOver(YAW, PITCH, 0, this.RANGE).equals(target)) {
					if(Math.abs((int) (REAL_BASE_PITCH-PITCH)) > 1) {
						diff = (int) (REAL_BASE_PITCH-PITCH);
					} else {
						diff = 0;
					}
				} else {
					diff = 0;
				}
				diff+=(int)(VECTOR_RANDOMIZER_P*RandomUtils.nextDouble(0.75, 1.2));
				int r2 = (int) (RandomUtils.nextInt(1, 4));
				diff = Math.round(diff / r2) * r2;
				int r = RandomUtils.nextInt(2, 5);
				if(diff > 0 && diff < r) {
					diff = r;
				}
				if(diff > -r && diff < 0) {
					diff = -r;
				}
				if(Math.abs(lastYAW - YAW) == 0)
					diff+=RandomUtils.nextInt(0, 8)-RandomUtils.nextInt(0, 9);

				diff = (int) MathUtils.clamp(diff, -rotationSpeed.getValDouble()*1.5-RandomUtils.nextInt(0, 15), rotationSpeed.getValDouble()*1.5+RandomUtils.nextInt(0, 15));
				DIFFP+=(diff-DIFFP)*0.2;
				if(Math.abs(DIFFP) <= 2) {
					DIFFP = 0;
				}
				float f1 = sens * sens * sens * 8.0F;
				float fin = (int)DIFFP * f1;
				PITCH = (float) (PITCH + fin * 0.15D);
			}
		} else {
			if(postRots.getTimePassed() <= 750) {
		    	sens = mc.gameSettings.mouseSensitivity+0.1f;
				final float[] firstAngle = {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
		        final float[] secondAngle = new float[]{YAW, PITCH};
		        float[] angles = MovementUtil.getConstantRotations(secondAngle, firstAngle);
				{
					int diff = (int) (mc.thePlayer.rotationYaw-angles[0]);
					diff = (int) MathUtils.clamp(diff, -10*(sens*10)-RandomUtils.nextInt(0, 11), 10*(sens*10)+RandomUtils.nextInt(0, 11));
					float f1 = sens * sens * sens * 8.0F;
					float fin = diff+(RandomUtils.nextInt(0, 3)-RandomUtils.nextInt(0, 3)) * f1;
					YAW = (float) (YAW + fin * 0.15D);
				}
			} else {
				YAW = mc.thePlayer.rotationYaw;
			}
			PITCH = mc.thePlayer.rotationPitch;
			BASE_YAW = mc.thePlayer.rotationYaw;
			BASE_PITCH = mc.thePlayer.rotationPitch;
		}
		PITCH = (float) MathUtils.clamp(PITCH, -90, 90);
		lastYAW = YAW;
		lastPITCH = PITCH;
	}
	
	public void autoBlock() {
		if(autoBlock.getValBoolean()) {
			boolean block = this.target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
			if(block && !Axyl.onDisableFix && mc.thePlayer.getDistanceToEntity(this.target) < this.RANGE
			&& autoBlock.getValBoolean()) {
				if(abMode.equalsIgnoreCase("Vanilla")) {
					PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
				}
				if(abMode.equalsIgnoreCase("NCP")) {
					unBlock();
					PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
					//PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
				}
				isBlocking = true;
			}
		} else {
			isBlocking = false;
		}
	} 
	
	public static void unBlock() {
		if(!abMode.equalsIgnoreCase("Legit")) {
			PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, mc.thePlayer.getPosition(), EnumFacing.DOWN));
			isBlocking = false;
		}
	}
	
    private boolean canAttack(final EntityLivingBase player) {
    	if (!(player instanceof EntityPlayer)) {
    		return false;
    	}
    	if (player instanceof EntityPlayer) {
            if(AntiBot.getBots().contains(player)) {
            	return false;
            }
        }
        return (!player.isOnSameTeam(mc.thePlayer) || !teams.getValBoolean()) && (!player.isInvisible() || attackInvisibles.getValBoolean()) && player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= (RANGE + PRE_AIM_RANGE);
    }
    
	private float[] getSmoothedAngles(float[] rot1, float[] rot2) {
        float[] smoothedAngle = new float[2];
        smoothedAngle[1] = (rot2[1] - rot1[1]);
        smoothedAngle[0] = (rot2[0] - rot1[0]);

        smoothedAngle = MathUtils.constrainAngle(smoothedAngle);

    	smoothedAngle[0] = (float) (rot2[0] - smoothedAngle[0] * 1f);
    	smoothedAngle[1] = (float) (rot2[1] - smoothedAngle[1] * 1f);
        return smoothedAngle;
    }
	
    private EntityLivingBase getClosestTarget(final double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (final Object object : mc.theWorld.loadedEntityList) {
            final Entity entity = (Entity)object;
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase player = (EntityLivingBase)entity;
                if (!this.canAttack(player)) {
                    continue;
                }
                final double currentDist = mc.thePlayer.getDistanceToEntity(player);
                if (currentDist > dist) {
                    continue;
                }
                dist = currentDist;
                target = player;
            }
        }
        if(target != this.target || target == null) {
        	if(autoBlock.getValBoolean()) {
	        	if(abMode.equalsIgnoreCase("Legit")) {
	        		if(ticksAfterLastAttack < 10)
	        		Axyl.onDisableFix = true;
	        	} else {
					if(isBlocking) {
						Axyl.onDisableFix = true;
					}
	        	}
        	}
        	cpsTimer.reset();
        }
        return target;
    }

    private float[] getRotations(Entity entity) {
    	Minecraft mc = Minecraft.getMinecraft();
        if (entity == null) {
            return null;
        }

    	double diffY;
        
        double diffX = 0;
        double diffZ = 0;
        
		double mx = 0.0;
		double mz = 0.0;

		if(prediction.getValBoolean()) {
			mx = entity.motionX;
			mz = entity.motionZ;
		}
        if(mc.thePlayer.getDistanceToEntity(entity) <= 0.75) {
        	diffX = entity.posX - mc.thePlayer.posX+0.075;
        	diffZ = entity.posZ - mc.thePlayer.posZ+0.075;
        } else {
        	diffX = (entity.posX) + mx - mc.thePlayer.posX + (mc.thePlayer.posX-(int)mc.thePlayer.posX)*0.15;
        	diffZ = (entity.posZ) + mz - mc.thePlayer.posZ + (mc.thePlayer.posZ-(int)mc.thePlayer.posZ)*0.15;
        }
        
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
        	diffY = (MathUtils.roundToPlace(entity.posY, 1) + (double)entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()+0.21f));
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()) - (double) 1.2f;
        }
        
        double dist = MathUtils.clamp(MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ), 0.25, RANGE+PRE_AIM_RANGE) - SMOOTHING*0.1+0.15;
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI)-90;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        return new float[]{yaw, Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }
    
	@Override
	public void onEnable() {
		SMOOTHING = (float) RandomUtils.nextDouble(0, 2);
		target = null;
		YAW = mc.thePlayer.rotationYaw;
		PITCH = mc.thePlayer.rotationPitch;
		BASE_YAW = mc.thePlayer.rotationYaw;
		BASE_PITCH = mc.thePlayer.rotationPitch;
		RANGE = reachVal.getValDouble();
		GLSCALE = 0;
		PRE_AIM_RANGE = 2;
		isBlocking = false;
		ticksAfterLastAttack = 999;
		x = false;
		target = null;
		tx = ty = tz = -999;
     	RANDOM_CPS = (int) (cps.getValDouble()+(RandomUtils.nextInt(0, 2)-RandomUtils.nextInt(0, 2)));
    	cpsList.clear();
		avg.clear();
		avgTimer.reset();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(autoBlock.getValBoolean()) {
			if(abMode.equalsIgnoreCase("Legit")) {
				Axyl.onDisableFix = true;
			} else {
				if(isBlocking) {
					Axyl.onDisableFix = true;
				}
			}
		}
		if(!Axyl.ins.modManager.getModuleByName("Scaffold").isToggled()) {
			RotationsHelper.SYNC_ROTS = true;
			RotationsHelper.ROTS_TO_SYNC = new float[]{YAW,PITCH};
		}
		target = null;
		super.onDisable();
	}
}
