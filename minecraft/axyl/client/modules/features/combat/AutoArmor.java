package axyl.client.modules.features.combat;

import net.minecraft.item.Item;      
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventTick;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.time.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;

public class AutoArmor extends Module
{
	public static boolean canCleanInv;
    public Timer timer = new Timer();
    public int[] helmet;
    public int[] chestplate;
    public int[] leggings;
    public int[] boots;
    public int delay;
    
    public AutoArmor() {
        super("AutoArmor", "Automatically wears armor", Keyboard.KEY_NONE, Category.Combat);
    }
    
    @Override
    public void moduleSetup() {
        this.helmet = new int[] { 310, 306, 314, 302, 298 };
        this.chestplate = new int[] { 311, 307, 315, 303, 299 };
        this.leggings = new int[] { 312, 308, 316, 304, 300 };
        this.boots = new int[] { 313, 309, 317, 305, 301 };
        super.moduleSetup();
    }
    
    @Subscribe
    public void eventUpdate(EventTick event) {
		if(mc.currentScreen instanceof GuiInventory) {
    		if (mc.thePlayer != null) {
	            int slot = -1;
	            double prot = -1.0;
	            int switchArmor = -1;
	            
	            for (int i = 9; i < 45; ++i) {
	                double protValue;
	                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
	                if (stack == null || !equip(stack) && (!betterItems(stack) || equip(stack))) continue;
	                if (betterItems(stack) && switchArmor == -1) {
	                    switchArmor = swapItem(stack);
	                }
	                
	                if ((protValue = protValue(stack)) < prot) continue;
	                slot = i;
	                prot = protValue;
	            }
	            
	            if (slot != -1) {
	            	canCleanInv = false;
	                if(delay >= 3) {
	                    if (switchArmor != -1) {
	                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + switchArmor, 0, 1, mc.thePlayer);
	                    }
	                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
	                    delay = 0;
	                } else {
	                	delay++;
	                }
	            } else {
	            	canCleanInv = true;
	            	delay = 0;
	            }
	            
    		}
		} else {
			delay = 0;
		}
    }

    public boolean betterItems(final ItemStack stack) {
        if (mc.thePlayer.getEquipmentInSlot(4) != null &&Item.getIdFromItem(mc.thePlayer.getEquipmentInSlot(4).getItem()) == 397) return true;
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null  && stack.getUnlocalizedName().contains("boots") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(1)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(2) != null  && stack.getUnlocalizedName().contains("leggings") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(2)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(3) != null  && stack.getUnlocalizedName().contains("chestplate") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(3)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(4) != null  && stack.getUnlocalizedName().contains("helmet") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(4)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount) {
                return true;
            }
        }
        return false;
    }

    public int swapItem(final ItemStack stack) {
        if (mc.thePlayer.getEquipmentInSlot(4) != null && Item.getIdFromItem(mc.thePlayer.getEquipmentInSlot(4).getItem()) == 397) return 1;
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null  && stack.getUnlocalizedName().contains("boots") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(1)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return 4;
            }
            if (mc.thePlayer.getEquipmentInSlot(2) != null  && stack.getUnlocalizedName().contains("leggings") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(2)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return 3;
            }
            if (mc.thePlayer.getEquipmentInSlot(3) != null  && stack.getUnlocalizedName().contains("chestplate") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(3)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return 2;
            }
            if (mc.thePlayer.getEquipmentInSlot(4) != null  && stack.getUnlocalizedName().contains("helmet") && this.protValue(stack) + ((ItemArmor) stack.getItem()).damageReduceAmount > this.protValue(mc.thePlayer.getEquipmentInSlot(4)) + ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount) {
                return 1;
            }
        }
        return -1;
    }

    public boolean equip(ItemStack stack) {
        return mc.thePlayer.getEquipmentInSlot(1) == null  && stack.getUnlocalizedName().contains("boots") || mc.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings") || mc.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate") || mc.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet");
    }

    public double protValue(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemArmor)) {
            return 0.0;
        }
        return ((ItemArmor) stack.getItem()).damageReduceAmount + (100 - ((ItemArmor) stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 4 * 0.0075;
    }
    
    @Override
    public void onDisable() {
    	canCleanInv = true;
    	super.onDisable();
    }
    
    @Override
    public void onEnable() {
    	canCleanInv = false;
    	this.delay = 0;
    	super.onEnable();
    }
}
