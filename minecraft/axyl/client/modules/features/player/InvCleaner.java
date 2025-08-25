package axyl.client.modules.features.player;
 
import net.minecraft.entity.player.EntityPlayer;    
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerPostUpdate;
import axyl.client.events.player.EventTick;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.combat.AutoArmor;
import axyl.client.util.player.MovementUtil;


public class InvCleaner extends Module
{
	public static boolean canSetItems;
	public List<ItemStack> getBestItem;
	public boolean shouldDrop;
	public int tickDelay;
    public int slot;
    public int delay;

    public InvCleaner() {
        super("InvCleaner", "Cleans Your inventory", Keyboard.KEY_G, Category.Player);
    }
    
    @Subscribe
    public void updateEvent(EventTick event) {
    	if(!Axyl.ins.modManager.getModuleByName("AutoArmor").isToggled()) {
    		AutoArmor.canCleanInv = true;
    	}
    	if(shouldDrop) {
    		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
    		shouldDrop = false;
    	}
    	if(AutoArmor.canCleanInv) {
    		if(!shouldDrop)
	        if(mc.currentScreen instanceof GuiInventory) {
		        if (mc.thePlayer != null) {
		        	if(tickDelay >= RandomUtils.nextInt(1, 4)) {
			            for (int i = 9; i < 45; ++i) {
			                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
			                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			                    if (MovementUtil.isBad(is)) {
			                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
			                        canSetItems = false;
			                        shouldDrop = true;
			                        tickDelay = 0;
			                        break;
			                    } else {
			                    	canSetItems = true;
			                    }
			                }
			            }
			            tickDelay = 0;
		        	} else {
		        		tickDelay++;
		        	}
		        }
	        } else {
	        	this.tickDelay = 0;
	        }
    	}
    }
    
    @Subscribe
    public void eventPostUpdate(EventPlayerPostUpdate event) {

    }
    
    @Override
    public void onEnable() {
    	this.canSetItems = false;
    	this.shouldDrop = false;
    	this.tickDelay = 0;
    	super.onEnable();
    }
}
