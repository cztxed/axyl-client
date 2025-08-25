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
import axyl.client.modules.features.combat.AntiBot;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.time.Timer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AutoReport extends Module
{
	
	public String[] reasons = {"Fly", "NoKnockback", "KillAura", "AimBot",
	"AutoArmor", "Velocity", "Reach", "HitBox", "ChestStealer", "AutoClicker", "AimAssist"};
	public Timer timer = new Timer();
	public int timerDelay = 0;
	
    public AutoReport() {
        super("AutoReport", "", 0, Category.Other);
    }

    @Subscribe
    public void eventPacket(EventPacket event) {
    	net.minecraft.entity.Entity e = mc.theWorld.getLoadedEntityList().get(RandomUtils.nextInt(0, mc.theWorld.getLoadedEntityList().size()));
    	if(!e.isInvisible()) {
			if(!e.equals(mc.thePlayer)) {
				if(!AntiBot.getBots().contains(e))
    			if(e instanceof EntityPlayer) {
    		    	if(timer.hasReached(timerDelay)) {
    		    		PacketUtil.sendPacketNoEvent(new C01PacketChatMessage("/report " + e.getName() + " Hacking " + reasons[RandomUtils.nextInt(0, reasons.length)]));
    		    		Axyl.sendMessage("/report " + e.getName() + " Hacking " + reasons[RandomUtils.nextInt(0, reasons.length)]);
    		    		timerDelay = RandomUtils.nextInt(1000, 1100);
    		    		timer.reset();
    		    	}
    			}
			}
		}
    }
    
    @Override
    public void onEnable() {
    	timerDelay = RandomUtils.nextInt(60000, 60100);
    	super.onEnable();
    }
}
