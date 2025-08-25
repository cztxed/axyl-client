package axyl.client.modules.features.other;

import java.util.ArrayList;   
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class StaffDetector extends Module
{

    public StaffDetector() {
        super("StaffDetector", "", 0, Category.Other);
    }

    @Subscribe
    public void eventPacket(EventPacket event) {	
        if (event.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
            if (packet.getAction() == S38PacketPlayerListItem.Action.UPDATE_LATENCY) {
                if(packet.getEntries().size() != GuiPlayerTabOverlay.scoreboardEntityList.size()) {
                	Axyl.sendMessage("Staff in game detected! (Entries:"+(packet.getEntries().size()-GuiPlayerTabOverlay.scoreboardEntityList.size())+")");
                }
            }
        }
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
}
