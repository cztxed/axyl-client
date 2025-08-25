package axyl.client.modules.features.player;

import net.minecraft.entity.player.EntityPlayer;    
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.events.player.EventUpdateRotation;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.player.MovementUtil;
import axyl.client.util.time.Timer;

public class ChestStealer extends Module
{
	public ContainerChest chest;
	public int pattern[];
    public Timer delay = new Timer();
    public int tickDelay;
    public int slot;
    public int d;
    
    public ChestStealer() {
        super("ChestStealer", "Automatically grabs items from the chest", Keyboard.KEY_NONE, Category.Player);
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
        if (mc.currentScreen instanceof GuiChest) {
        	chest = (ContainerChest)mc.thePlayer.openContainer;
        	if(tickDelay >= pattern[d]) {
            	if(!(chest.getLowerChestInventory().getStackInSlot(this.slot) == null)) {	
            		if(!MovementUtil.chestStealerIsBad(chest.getLowerChestInventory().getStackInSlot(this.slot))) {
                   		mc.playerController.windowClick(chest.windowId, this.slot, 0, 1, mc.thePlayer);
                        if(d > pattern.length) {
                        	d = 0;
                        } else {
                        	d++;
                        }
                   		tickDelay = -1;
                    }
                    this.slot+=1;
             	}
        	} else {
        		tickDelay++;
        	}
        } else {
        	tickDelay = -3;
        	slot = -10;
        	d = 0;
        }
    }
    
    @Subscribe
    public void eventUpdateRotation(EventUpdateRotation event) {
    	if(chest != null) {
            if (this.slot >= chest.getLowerChestInventory().getSizeInventory()) {
                this.slot = 0;
            }
            if (mc.currentScreen instanceof GuiChest) {
            	if(chest.getLowerChestInventory().getStackInSlot(this.slot) == null) {
            		this.slot+=1;
            	}
            }
    	}
    }
    
    @Override
    public void onEnable() {
    	pattern = new int[]{2, 0, 1, 2, 2, 1, 2, 1, 3, 2, 0, 2, 1, 1, 3, 2, 1, 3, 3, 2, 2, 3, 0, 2, 1, 2, 1};
    	d = 0;
    	tickDelay = -4;
    	slot = 0;
    	super.onEnable();
    }
}
