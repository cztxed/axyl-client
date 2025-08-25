package axyl.client.modules.features.visuals;

import net.minecraft.entity.player.EntityPlayer;  
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.BlockPos;
import axyl.client.Axyl;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.client.gui.inventory.GuiChest;

public class VClip extends Module
{
	private Setting clipY;
    
    public VClip() {
        super("VClip", "", 0, Category.Player);
    }
    
    @Override
    public void moduleSetup() {
    	Axyl.ins.settingManager.createSetting(this.clipY = new Setting("Y", this, -3.0, -10, 10, 0, false));
    }
    
    @Override
    public void onEnable() {
    	mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
    	mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + clipY.getValDouble(), mc.thePlayer.posZ);
    	this.toggle();
        super.onEnable();
    }
}
