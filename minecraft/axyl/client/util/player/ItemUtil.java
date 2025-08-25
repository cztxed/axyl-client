package axyl.client.util.player;

import axyl.client.Axyl;
import axyl.client.util.network.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class ItemUtil {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void changeSlot(int slot, boolean silent) {
		if(mc.thePlayer.c09CurrentSlot != slot) {
			PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
			if(!silent)
			mc.thePlayer.inventory.currentItem = slot;
		}
	}
	
	public static int findItemSlot() {
		int slot = mc.thePlayer.inventory.currentItem;
        for (int i = 0; i < 8; ++i) {
            final ItemStack is = mc.thePlayer.inventory.getStackInSlot(i);
            if(is != null)
            if (is.getItem() instanceof ItemBlock) {
            	slot = i;
            }
        }
		return slot;
	}
	
	public static ItemStack getCurrentItem() {
		return mc.thePlayer.getCurrentEquippedItem();
	}
	
	public static int getCurrentSlot() {
		return mc.thePlayer.inventory.currentItem;
	}
}
