package axyl.client.modules.features.visuals;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumFacing;

public class FastBreak extends Module
{
    private Setting speed;

	public FastBreak() {
        super("FastBreak", "", 0, Category.Player);
    }
    
    @Override
    public void moduleSetup() {
    	Axyl.ins.settingManager.createSetting(this.speed = new Setting("Speed", this, 70, 0, 100, 0, false));
    }
    
    @Subscribe
    public void eventUpdate(EventPlayerUpdate event) {
    	this.suffix = speed.getValDouble() + "%";
        //mc.playerController.blockHitDelay = 0;
        if (mc.playerController.curBlockDamageMP >= speed.getValDouble()/100) {
        	//mc.playerController.onPlayerDestroyBlock(mc.objectMouseOver.getBlockPos(), EnumFacing.DOWN);
        	mc.playerController.curBlockDamageMP = 1.0F;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
