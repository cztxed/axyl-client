package axyl.client.modules.features.player;

import java.util.ArrayList; 

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.combat.AutoArmor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.BlockPos;
import tv.twitch.chat.Chat;

public class InvManager extends Module
{
	public int action = 0;
    private Setting slotSword;
	private Setting slotPickaxe;
	private Setting slotAxe;
	private Setting slotSpade;
	private Setting slotHoe;
    
	public InvManager() {
        super("InvManager", "", 0, Category.Player);
    }
    
    @Override
    public void moduleSetup() {
    	Axyl.ins.settingManager.createSetting(slotSword = new Setting("Slot Sword", this, 1, 1, 9, 0, false));
    	Axyl.ins.settingManager.createSetting(slotPickaxe = new Setting("Slot Pickaxe", this, 2, 1, 9, 0, false));
    	Axyl.ins.settingManager.createSetting(slotAxe = new Setting("Slot Axe", this, 3, 1, 9, 0, false));
    	Axyl.ins.settingManager.createSetting(slotSpade = new Setting("Slot Spade", this, 4, 1, 9, 0, false));
    	Axyl.ins.settingManager.createSetting(slotHoe = new Setting("Slot Hoe", this, 5, 1, 9, 0, false));
    }
    
    @Subscribe
    public void onUpdate(EventPlayerUpdate event) {
    	if(!Axyl.ins.modManager.getModuleByName("AutoArmor").isToggled()) {
    		AutoArmor.canCleanInv = true;
    	}
    	if(!Axyl.ins.modManager.getModuleByName("InvCleaner").isToggled()) {
    		InvCleaner.canSetItems = true;
    	}
    	if(InvCleaner.canSetItems)
    	if(AutoArmor.canCleanInv)
        if(mc.currentScreen instanceof GuiInventory) {
        	action++;
        	if(action == 2) {
            	{
            		//Swords
                    int targetItem = -1;
                    float itemDamage = 0;
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (is.getItem() instanceof ItemSword) {
                                float itemId = getDamage(is);
                                if (itemId >= itemDamage) {
                                    itemDamage = itemId;
                                    targetItem = i;
                                }
                            }
                        }
                    }
                	int slot = (int) this.slotSword.getValDouble() - 1;
                    final ItemStack current = mc.thePlayer.inventoryContainer.getSlot((int) (36 + slot)).getStack();
                    if (targetItem != -1 || current == null || (current.getItem() instanceof ItemSword && itemDamage > getDamage(current))) {
                        float dmg = -9999;
                        if (targetItem != 36 + slot && targetItem != -1 && itemDamage > dmg) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, targetItem, (int) slot, 2, mc.thePlayer);
                        }
                    }
            	}
        	}
        	if(action == 4) {
        		{
            		//Pickaxes
                    int targetItem = -1;
                    float itemDamage = 0;
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (is.getItem() instanceof ItemPickaxe) {
                                float damage = is.getDamage();
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                                damage += is.getMaxDamage() / 20;
                                float itemId = damage;
                                if (itemId >= itemDamage) {
                                    itemDamage = itemId;
                                    targetItem = i;
                                }
                            }
                        }
                    }
                	int slot = (int) this.slotPickaxe.getValDouble() - 1;
                    final ItemStack current = mc.thePlayer.inventoryContainer.getSlot((int) (36 + slot)).getStack();
                    if (targetItem != -1 || current == null || (current.getItem() instanceof ItemPickaxe && itemDamage > getDamage(current))) {
                        if (targetItem != 36 + slot && targetItem != -1 && itemDamage > -9999) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, targetItem, (int) slot, 2, mc.thePlayer);
                        }
                    }
            	}
        	}
        	if(action == 7) {
        		{
            		//Axes
                    int targetItem = -1;
                    float itemDamage = 0;
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (is.getItem() instanceof ItemAxe) {
                                float damage = is.getDamage();
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                                damage += is.getMaxDamage() / 20;
                                float itemId = damage;
                                if (itemId >= itemDamage) {
                                    itemDamage = itemId;
                                    targetItem = i;
                                }
                            }
                        }
                    }
                	int slot = (int) this.slotAxe.getValDouble() - 1;
                    final ItemStack current = mc.thePlayer.inventoryContainer.getSlot((int) (36 + slot)).getStack();
                    if (targetItem != -1 || current == null || (current.getItem() instanceof ItemAxe && itemDamage > getDamage(current))) {
                        if (targetItem != 36 + slot && targetItem != -1 && itemDamage > -9999) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, targetItem, (int) slot, 2, mc.thePlayer);
                        }
                    }
            	}
        	}
        	if(action == 10) {
        		{
            		//Spades
                    int targetItem = -1;
                    float itemDamage = 0;
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (is.getItem() instanceof ItemSpade) {
                                float damage = is.getDamage();
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                                damage += is.getMaxDamage() / 20;
                                float itemId = damage;
                                if (itemId >= itemDamage) {
                                    itemDamage = itemId;
                                    targetItem = i;
                                }
                            }
                        }
                    }
                	int slot = (int) this.slotSpade.getValDouble() - 1;
                    final ItemStack current = mc.thePlayer.inventoryContainer.getSlot((int) (36 + slot)).getStack();
                    if (targetItem != -1 || current == null || (current.getItem() instanceof ItemSpade && itemDamage > getDamage(current))) {
                        if (targetItem != 36 + slot && targetItem != -1 && itemDamage > -9999) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, targetItem, (int) slot, 2, mc.thePlayer);
                        }
                    }
            	}
        	}
        	if(action >= 14) {
        		action = -3;
        		{
            		//Hoes
                    int targetItem = -1;
                    float itemDamage = 0;
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (is.getItem() instanceof ItemHoe) {
                                float damage = is.getDamage();
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) / 10;
                                damage += is.getMaxDamage() / 20;
                                float itemId = damage;
                                if (itemId >= itemDamage) {
                                    itemDamage = itemId;
                                    targetItem = i;
                                }
                            }
                        }
                    }
                	int slot = (int) this.slotHoe.getValDouble() - 1;
                    final ItemStack current = mc.thePlayer.inventoryContainer.getSlot((int) (36 + slot)).getStack();
                    if (targetItem != -1 || current == null || (current.getItem() instanceof ItemHoe && itemDamage > getDamage(current))) {
                        if (targetItem != 36 + slot && targetItem != -1 && itemDamage > -9999) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, targetItem, (int) slot, 2, mc.thePlayer);
                        }
                    }
            	}
        	}
        } else {
        	action = -3;
        }
    }
    
    public float getDamage(final ItemStack itemStack) {
        float damage = ((ItemSword) itemStack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f;
        return damage;
    }
}
