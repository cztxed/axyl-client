package axyl.client.util.player;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.MutableClassToInstanceMap;

import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;

import java.util.ArrayList;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.Minecraft;
 
public class MovementUtil
{
    protected static Minecraft mc;
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    public static void setSpeed(double moveSpeed, double playerStrafe, double playerForward) {
        double forward = playerForward;
        double strafe = playerStrafe;
        
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
        double offsetX = Math.cos(Math.toRadians(yaw + 90.0f));
        double offsetZ = Math.sin(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = forward * moveSpeed * offsetX + strafe * moveSpeed * offsetZ;
        mc.thePlayer.motionZ = forward * moveSpeed * offsetZ - strafe * moveSpeed * offsetX;
    }
    
    public static void setSpeed(double moveSpeed, double playerStrafe, double playerForward, float y) {
        double forward = playerForward;
        double strafe = playerStrafe;
        
        float yaw = y;
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
        double offsetX = Math.cos(Math.toRadians(yaw + 90.0f));
        double offsetZ = Math.sin(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = forward * moveSpeed * offsetX + strafe * moveSpeed * offsetZ;
        mc.thePlayer.motionZ = forward * moveSpeed * offsetZ - strafe * moveSpeed * offsetX;
    }
    
    public static void setSpeed(double moveSpeed) {
        MovementInput movementInput = Minecraft.getMinecraft().thePlayer.movementInput;
        double playerStrafe = MovementInput.moveStrafe;
        MovementInput movementInput2 = Minecraft.getMinecraft().thePlayer.movementInput;
        
        setSpeed(moveSpeed, playerStrafe, MovementInput.moveForward);
    }
    
    public static void setSpeed(double moveSpeed, float yaw) {
        MovementInput movementInput = Minecraft.getMinecraft().thePlayer.movementInput;
        double playerStrafe = MovementInput.moveStrafe;
        MovementInput movementInput2 = Minecraft.getMinecraft().thePlayer.movementInput;
        
        setSpeed(moveSpeed, playerStrafe, MovementInput.moveForward, yaw);
    }
    
    public static float getSpeed() {
        return (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static double getJumpHeight(double motionY) {
        return mc.thePlayer.isPotionActive(Potion.jump) ? (motionY + 0.1 * (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1)) : motionY;
    }
    
    public static double getSpeedEffect(double speed) {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
        	speed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return speed;
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static double jumpHeight() {
        return 0.419999986886978;
    }
    
    public static void sendRotPacket(BlockPos pos, float yaw, float pitch, boolean onGround) {
    	if(mc.thePlayer.isMoving()) {
    		PacketUtil.sendPacketNoEvent(new C06PacketPlayerPosLook(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch, onGround));
    	} else {
    		PacketUtil.sendPacketNoEvent(new C05PacketPlayerLook(yaw, pitch, onGround));
    	}
    }
    
    public static void damagePlayer() {
    	mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.41999998688697815, mc.thePlayer.posZ, true));
    }
    
    public static void hypixelDamage() {
    	PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.419999986886978, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.7531999805212015, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.001335979112147, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.166109260938214, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.170787077218802, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.0155550727022, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.785027703789236, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.480710876331692, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.104080378093037, mc.thePlayer.posZ, false));

		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.419999986886978, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.7531999805212015, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.001335979112147, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.166109260938214, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.170787077218802, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.0155550727022, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.785027703789236, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.480710876331692, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.104080378093037, mc.thePlayer.posZ, false));
		
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.419999986886978, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+.7531999805212015, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.001335979112147, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.166109260938214, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.249187078744681, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.170787077218802, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.0155550727022, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.785027703789236, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.480710876331692, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.104080378093037, mc.thePlayer.posZ, false));
		PacketUtil.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }
	
	public static float getDir(float y) {
		float yaw = y;
        final float forward = mc.thePlayer.moveForward;
        final float strafe = mc.thePlayer.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        
		float f = yaw * 0.017453292F;
		return f;
	}
    
    public static boolean lookingAtBlock(final BlockPos blockFace, final float yaw, final float pitch, final EnumFacing enumFacing, final boolean strict) {
        final MovingObjectPosition obj = mc.thePlayer.rayTraceCustom(mc.playerController.getBlockReachDistance(), mc.timer.renderPartialTicks, yaw, pitch);
        if (obj == null) return false;
        final Vec3 hitVec = obj.hitVec;
        if (hitVec == null) return false;
        if ((hitVec.xCoord - blockFace.getX()) > 1.0 || (hitVec.xCoord - blockFace.getX()) < 0.0) return false;
        if ((hitVec.yCoord - blockFace.getY()) > 1.0 || (hitVec.yCoord - blockFace.getY()) < 0.0) return false;
        if(enumFacing.equals(EnumFacing.UP)) {
        	return !((hitVec.zCoord - blockFace.getZ()) > 1.0) && !((hitVec.zCoord - blockFace.getZ()) < 0.0) && (obj.sideHit == enumFacing || !strict);
        } else {
        	return !((hitVec.zCoord - blockFace.getZ()) > 1.125) && !((hitVec.zCoord - blockFace.getZ()) < -0.125) && (obj.sideHit == enumFacing || !strict);
        }
    }
    
    public static float[] getRotations(final double posX, final double posY, final double posZ) {
        final EntityPlayerSP player = mc.thePlayer;
        final double x = posX - player.posX;
        final double y = posY - (player.posY + (double) player.getEyeHeight());
        final double z = posZ - player.posZ;
        final double dist = MathHelper.sqrt_double(x * x + z * z);
        final float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }
    
	public static float[] getConstantRotations(float[] rot1, float[] rot2) {
        float[] angle = new float[2];
     
        angle[1] = (rot2[1] - rot1[1]);
        angle[0] = (rot2[0] - rot1[0]);
        angle = MathUtils.constrainAngle(angle);

    	angle[0] = (float) (rot2[0] - angle[0]);
    	angle[1] = (float) (rot2[1] - angle[1]);
        return angle;
    }
    
	public static float getConstantRotation(float current, float target) {
        float angle;
        angle = (current - target);
        angle = MathUtils.constrainAngle(new float[]{angle, -999})[0];
    	angle = (float) (current - angle*0.35f);
        return angle;
    }
	
    public static boolean isBad(final ItemStack item) {
        return item != null
        && (item.getItem().getUnlocalizedName().contains("tnt")
        || (item.getItem().getUnlocalizedName().contains("stick"))
        || item.getItem() instanceof ItemEgg 
        || (item.getItem().getUnlocalizedName().contains("string"))
        || (item.getItem().getUnlocalizedName().contains("flint"))
        || (item.getItem().getUnlocalizedName().contains("feather"))
        || (item.getItem().getUnlocalizedName().contains("bucket"))
        || (item.getItem().getUnlocalizedName().equalsIgnoreCase("chest")
        && !item.getDisplayName().toLowerCase().contains("collect"))
        || (item.getItem().getUnlocalizedName().contains("snow"))
        || (item.getItem().getUnlocalizedName().contains("enchant"))
        || (item.getItem().getUnlocalizedName().contains("exp"))
        || (item.getItem().getUnlocalizedName().contains("shears"))
        || (item.getItem().getUnlocalizedName().contains("anvil"))
        || (item.getItem().getUnlocalizedName().contains("torch"))
        || (item.getItem().getUnlocalizedName().contains("skull"))
        || (item.getItem().getUnlocalizedName().contains("seeds"))
        || (item.getItem().getUnlocalizedName().contains("leather"))
        || (item.getItem().getUnlocalizedName().contains("boat"))
        || (item.getItem().getUnlocalizedName().contains("fishing"))
        || (item.getItem().getUnlocalizedName().contains("wheat"))
        || (item.getItem().getUnlocalizedName().contains("flower"))
        || (item.getItem().getUnlocalizedName().contains("record"))
        || (item.getItem().getUnlocalizedName().contains("note"))
        || (item.getItem().getUnlocalizedName().contains("sugar"))
        || (item.getItem().getUnlocalizedName().contains("wire"))
        || (item.getItem().getUnlocalizedName().contains("trip"))
        || (item.getItem().getUnlocalizedName().contains("web"))
        || (item.getItem() instanceof ItemArmor && !getBestItem().contains(item))
        || (item.getItem() instanceof ItemSword && item != getBetterSword())
        || (item.getItem() instanceof ItemPickaxe && item != getBetterPickaxe())
        || (item.getItem() instanceof ItemAxe && item != getBetterAxe())
        || (item.getItem() instanceof ItemSpade && item != getBetterSpade())
        || (item.getItem() instanceof ItemHoe && item != getBetterHoe())
        || (item.getItem() instanceof ItemBow && item != getBetterBow())
        || (item.getItem().getUnlocalizedName().contains("piston"))
        || (item.getItem().getUnlocalizedName().contains("potion") && (isPotionBad(item))));
    }
    
    public static boolean chestStealerIsBad(final ItemStack item) {
        return item != null
        && (item.getItem().getUnlocalizedName().contains("tnt")
        || (item.getItem().getUnlocalizedName().contains("stick"))
        || item.getItem() instanceof ItemEgg 
        || (item.getItem().getUnlocalizedName().contains("string"))
        || (item.getItem().getUnlocalizedName().contains("flint"))
        || (item.getItem().getUnlocalizedName().contains("feather"))
        || (item.getItem().getUnlocalizedName().contains("bucket"))
        || (item.getItem().getUnlocalizedName().equalsIgnoreCase("chest")
        && !item.getDisplayName().toLowerCase().contains("collect"))
        || (item.getItem().getUnlocalizedName().contains("snow"))
        || (item.getItem().getUnlocalizedName().contains("enchant"))
        || (item.getItem().getUnlocalizedName().contains("exp"))
        || (item.getItem().getUnlocalizedName().contains("shears"))
        || (item.getItem().getUnlocalizedName().contains("anvil"))
        || (item.getItem().getUnlocalizedName().contains("torch"))
        || (item.getItem().getUnlocalizedName().contains("skull"))
        || (item.getItem().getUnlocalizedName().contains("seeds"))
        || (item.getItem().getUnlocalizedName().contains("leather"))
        || (item.getItem().getUnlocalizedName().contains("boat"))
        || (item.getItem().getUnlocalizedName().contains("fishing"))
        || (item.getItem().getUnlocalizedName().contains("wheat"))
        || (item.getItem().getUnlocalizedName().contains("flower"))
        || (item.getItem().getUnlocalizedName().contains("record"))
        || (item.getItem().getUnlocalizedName().contains("note"))
        || (item.getItem().getUnlocalizedName().contains("sugar"))
        || (item.getItem().getUnlocalizedName().contains("wire"))
        || (item.getItem().getUnlocalizedName().contains("trip"))
        || (item.getItem().getUnlocalizedName().contains("web"))
        || (item.getItem() instanceof ItemArmor && getBestItem().contains(item))
        || (item.getItem() instanceof ItemSword && item == getBetterSword())
        || (item.getItem() instanceof ItemPickaxe && item == getBetterPickaxe())
        || (item.getItem() instanceof ItemAxe && item == getBetterAxe())
        || (item.getItem() instanceof ItemSpade && item == getBetterSpade())
        || (item.getItem() instanceof ItemHoe && item == getBetterHoe())
        || (item.getItem() instanceof ItemBow && item == getBetterBow())
        || (item.getItem().getUnlocalizedName().contains("piston"))
        || (item.getItem().getUnlocalizedName().contains("potion") && (isPotionBad(item))));
    }
    
    public static ItemStack getBetterSword() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float iDamage = is.getDamage()*2;
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, is)*33;
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, is) / 10;
                    iDamage +=((ItemSword) is.getItem()).getDamageVsEntity();
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    public static ItemStack getBetterBow() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBow) {
                    float iDamage = is.getDamage();
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, is);
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, is) / 10;
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    public static ItemStack getBetterPickaxe() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPickaxe) {
                    float iDamage = is.getDamage();
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                    iDamage += is.getMaxDamage() / 20;
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    public static ItemStack getBetterAxe() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemAxe) {
                    float iDamage = is.getDamage();
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                    iDamage += is.getMaxDamage() / 20;
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    public static ItemStack getBetterSpade() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSpade) {
                    float iDamage = is.getDamage();
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                    iDamage += is.getMaxDamage() / 20;
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    public static ItemStack getBetterHoe() {
        ItemStack getBestItem = null;
        float itemStackDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemHoe) {
                    float iDamage = is.getDamage();
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                    iDamage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                    iDamage += is.getMaxDamage() / 20;
                    if (iDamage > itemStackDamage) {
                    	itemStackDamage = iDamage;
                        getBestItem = is;
                    }
                }
            }
        }
        return getBestItem;
    }
    
    
    public static List<ItemStack> getBestItem() {
        List<ItemStack> getBestItem = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            ItemStack armorStack = null;
            for (ItemStack itemStack : mc.thePlayer.inventory.armorInventory) {
                if (itemStack == null || !(itemStack.getItem() instanceof ItemArmor)) 
                	continue;
                ItemArmor stackArmor = (ItemArmor) itemStack.getItem();
                if (stackArmor.armorType != i) 
                	continue;
                armorStack = itemStack;
            }
            final double reduction = armorStack == null ? -1 : getArmorEnchant(armorStack);
            ItemStack slotStack = getBestArmor(i);
            if (slotStack != null && getArmorEnchant(slotStack) <= reduction) {
                slotStack = armorStack;
            }
            if (slotStack == null) 
            	continue;
            getBestItem.add(slotStack);
        }
        return getBestItem;
    }

    public static ItemStack getBestArmor(final int itemSlot) {
        ItemStack i = null;
        double maxReduction = 0;
        for (int slot = 0; slot < 36; ++slot) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            if (itemStack == null)
            	continue;
            
            double reduction = getArmorEnchant(itemStack);
            if (reduction == -1)
            	continue;
            
            ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
            if (itemArmor.armorType != itemSlot)
            	continue;
            
            if (reduction < maxReduction)
            	continue;
            
            maxReduction = reduction;
            i = itemStack;
        }
        return i;
    }
    
    public static double getArmorEnchant(final ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemArmor)) return -1;
        float damageReduction = ((ItemArmor) itemStack.getItem()).damageReduceAmount;
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if (enchantments.containsKey(Enchantment.protection.effectId)) {
            int level = enchantments.get(Enchantment.protection.effectId);
            damageReduction += Enchantment.protection.calcModifierDamage(level, DamageSource.generic);
        }
        return damageReduction;
    }
    
    public static boolean isPotionBad(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final PotionEffect o : potion.getEffects(stack)) {
                    final PotionEffect effect = o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    } 
}
