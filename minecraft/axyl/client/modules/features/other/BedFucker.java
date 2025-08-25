package axyl.client.modules.features.other;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.AMDSamplePositions;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.player.EventJump;
import axyl.client.events.player.EventPlayerPreUpdate;
import axyl.client.events.player.EventPrePreUpdate;
import axyl.client.events.player.EventStrafe;
import axyl.client.events.player.EventTick;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.events.render.EventRender3D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.movement.MovementCorrection;
import axyl.client.util.math.MathUtils;
import axyl.client.util.player.MovementUtil;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.world.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BedFucker extends Module
{
    public BlockPos blockPos = null;
	public float damage;
	public float YAW, PITCH;
    public int lastSlot = -1;
    public boolean isBreaking = false;
	
	private Setting miningMode;
	private Setting checkForBlocks;
	private double decenterRotation;
	private boolean brokeBlock;

    public BedFucker() {
        super("BedFucker", "", 0, Category.Other);
    }

    @Override
    public void moduleSetup() {
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Normal");
		options.add("GrimFIX");

		Axyl.ins.settingManager.createSetting(this.miningMode = new Setting("Mode", this, "Normal", options));
		Axyl.ins.settingManager.createSetting(this.checkForBlocks = new Setting("Check for blocks around", this, false, false));
    	super.moduleSetup();
    }
    
    @Subscribe
    public void eventTick(EventTick event) {
    	itemSwap();
    	double r = 3;
    	if(blockPos == null) {
    		this.YAW = mc.thePlayer.rotationYaw;
    		this.PITCH = mc.thePlayer.rotationPitch;
        	ArrayList<BlockPos> positions = new ArrayList<>();
            for (double x = -r; x <= r; x+=1) {
                for (double y = r; y >= -r; y-=1) {
                    for (double z = -r; z <= r; z+=1) {
                        final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        if(mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockBed) {
                        	positions.add(blockPos);
                        } else {
                        	this.blockPos = null;
                        }
                    }
                }
            }
            try {
            	 if(positions != null) {
                 	positions.sort(Comparator.comparingDouble(m -> (mc.thePlayer.getDistanceSq(m.down(1)))));
                 	blockPos = positions.get(0);
                 	this.damage = 0;
                 }
			} catch (Exception e) {
				
			}
    	} else {
      		if(mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= mc.playerController.getBlockReachDistance()) {
        		this.blockPos = null;
        	}
    		BlockPos blockPos = this.blockPos;
    		if(checkForBlocks.getValBoolean())
    		if(blocksAround(blockPos)) {
    			blockPos = blockPos.up(1);
    		}
           	if (damage == 0) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                if (mc.theWorld.getBlockState(blockPos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) >= 1) {
                	mc.thePlayer.swingItem();
                   	if(!miningMode.getValString().equalsIgnoreCase("GrimFIX"))
                    mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                }
            }
            //mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), blockPos, (int) (damage * 10) - 1);
            if (damage > 1) {
            	mc.thePlayer.swingItem();
                mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            	if(!miningMode.getValString().equalsIgnoreCase("GrimFIX"))
                mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                damage = 0;
            	this.blockPos = null;
            }
            damage += mc.theWorld.getBlockState(blockPos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos);
    	}
    }
    
    @Subscribe
    public void eventPreUpdate(EventPlayerPreUpdate event) {
    	if(blockPos != null) {
    		event.setYaw(YAW);
    		event.setPitch(PITCH);
    		mc.thePlayer.rotationPitchHead = PITCH;
    	}
    }
    
    @Subscribe
    public void eventStrafe(EventStrafe event) {
    	if(MovementCorrection.bedFucker.getValBoolean()
    	&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
        	if(blockPos != null) {
        		event.setYaw(YAW);
        	}
    	}
    }
    
    @Subscribe
    public void eventJump(EventJump event) {
    	if(MovementCorrection.bedFucker.getValBoolean()
    	&& Axyl.ins.modManager.getModuleByName("MovementCorrection").isToggled()) {
        	if(blockPos != null) {
        		event.setYaw(YAW);
        	}
    	}
    }
    
    @Subscribe
    public void eventUpdateRotation(EventUpdateRotation event) {
    	if(blockPos != null) {
    		float sens = (float) (1);
    		BlockPos blockPos = this.blockPos;
    		if(checkForBlocks.getValBoolean())
    		if(blocksAround(blockPos)) {
    			blockPos = blockPos.up(1);
    		}
    		float rots[] = MovementUtil.getRotations(blockPos.getX()+decenterRotation, blockPos.getY()+decenterRotation/2, blockPos.getZ()+decenterRotation);
    		{
    			int diff = (int) (rots[0]-YAW);
    			diff = (int) MathUtils.clamp(diff, -45-RandomUtils.nextInt(0, 5), 45+RandomUtils.nextInt(0, 5));
    			diff+=diff*RandomUtils.nextFloat(0.15f, 0.35f);
    			float f1 = sens * sens * sens * 8.0F;
    			float fin = (int)diff * f1;
    			YAW = (float) (YAW + fin * 0.15D);
    		}
    		{
    			rots[1] = (float) MathUtils.clamp(rots[1], -90, 90);
    			int diff = (int) (rots[1]-PITCH);
    			diff = (int) MathUtils.clamp(diff, -45-RandomUtils.nextInt(0, 5), 45+RandomUtils.nextInt(0, 5));
    			diff+=diff*RandomUtils.nextFloat(0.15f, 0.35f);
    			float f1 = sens * sens * sens * 8.0F;
    			float fin = (int)diff * f1;
    			PITCH = (float) (PITCH + fin * 0.15D);
    		}
    	} else {
    		decenterRotation = RandomUtils.nextDouble(0.15, 0.45)-RandomUtils.nextDouble(0.15, 0.45)+0.5;
    	}
    }
    
    @Subscribe
    public void eventRender3D(EventRender3D event) {
	   	if(blockPos != null) {
    		BlockPos blockPos = this.blockPos;
    		if(checkForBlocks.getValBoolean())
    		if(blocksAround(blockPos)) {
    			blockPos = blockPos.up(1);
    		}
	   		RenderUtil.drawFilledBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, damage, 0xff00ff50);
	   		RenderUtil.drawFilledBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ(), damage, 1, 0xffff0020);
	 	}
    }
    
    public boolean blocksAround(BlockPos blockPos) {
    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()+1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockAir)
    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()-1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockAir)
    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()+1)).getBlock() instanceof BlockAir)
    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()-1)).getBlock() instanceof BlockAir)
    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY()+1, blockPos.getZ())).getBlock() instanceof BlockAir)) { 
	    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()+1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockAir)
	        && !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()+1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockBed)) {
	    		return true;
	    	}
	    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()-1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockAir)
	    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX()-1, blockPos.getY(), blockPos.getZ())).getBlock() instanceof BlockBed)) {
	    		return true;
	    	}
	    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()+1)).getBlock() instanceof BlockAir)
	        && !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()+1)).getBlock() instanceof BlockBed)) {
	    		return true;              		
	        }
	    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()-1)).getBlock() instanceof BlockAir)
	        && !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()-1)).getBlock() instanceof BlockBed)) {
	    		return true;
	    	}
	    	if(!(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY()+1, blockPos.getZ())).getBlock() instanceof BlockAir)
	    	&& !(mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY()+1, blockPos.getZ())).getBlock() instanceof BlockBed)) {
	    		return true;
	    	}
    	}
    	return false;
    }
    
    public void itemSwap() {
    	if (blockPos != null) {
            float bestSpeed = 1.0F;
            int betterSlot = -1;
    		BlockPos blockPos = this.blockPos;
    		if(checkForBlocks.getValBoolean())
    		if(blocksAround(blockPos)) {
    			blockPos = blockPos.up(1);
    		}
            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            for (int k = 0; k < 9; k++) {
                ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
                if (item != null) {
                    float speed = item.getStrVsBlock(block);
                    if (speed > bestSpeed) {
                        bestSpeed = speed;
                        betterSlot = k;
                    }
                }
            }
            if (betterSlot != -1 && mc.thePlayer.inventory.currentItem != betterSlot) {
                mc.thePlayer.inventory.currentItem = betterSlot;
                this.isBreaking = true;
            } else if (betterSlot == -1) {
                if (this.isBreaking) {
                    mc.thePlayer.inventory.currentItem = this.lastSlot;
                    this.isBreaking = false;
                    
                }
                this.lastSlot = mc.thePlayer.inventory.currentItem;
            }
        } else {
            if (this.isBreaking) {
                mc.thePlayer.inventory.currentItem = this.lastSlot;
                this.isBreaking = false;
            }
            this.lastSlot = mc.thePlayer.inventory.currentItem;
        }
    }
    
    @Override
    public void onEnable() {
    	decenterRotation = RandomUtils.nextDouble(0.335, 0.735);
    	blockPos = null;
    	damage = 0;
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	super.onDisable();
    }
}
