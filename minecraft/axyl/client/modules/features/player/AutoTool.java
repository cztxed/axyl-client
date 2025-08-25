package axyl.client.modules.features.player;

import java.util.ArrayList;  

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.player.EventTick;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class AutoTool extends Module
{
    public int lastSlot = -1;
    public boolean isBreaking = false;
    
    public AutoTool() {
        super("AutoTool", "Automatically picks the best tools", Keyboard.KEY_NONE, Category.Player);
    }
    
    @Subscribe
    public void eventTick(EventTick event) {
        if (this.mc.currentScreen == null && mc.thePlayer != null && mc.theWorld != null && this.mc.objectMouseOver != null && this.mc.objectMouseOver.getBlockPos() != null && this.mc.objectMouseOver.entityHit == null && Mouse.isButtonDown(0)) {
            float bestSpeed = 1.0F;
            int betterSlot = -1;
            Block block = mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock();
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
        } else if (mc.thePlayer != null && mc.theWorld != null) {
            if (this.isBreaking) {
                mc.thePlayer.inventory.currentItem = this.lastSlot;
                this.isBreaking = false;
            }
            this.lastSlot = mc.thePlayer.inventory.currentItem;
        }
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
}
