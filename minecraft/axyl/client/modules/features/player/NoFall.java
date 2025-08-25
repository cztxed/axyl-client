package axyl.client.modules.features.player;

import java.util.ArrayList;  
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.network.EventPacket;
import axyl.client.events.network.PacketDir;
import axyl.client.events.player.EventBoundingBox;
import axyl.client.events.player.EventPlayerUpdate;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.network.PacketUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoFall extends Module
{	
    public static List<Packet> packets = new ArrayList();
    
	public double posy;
	public double fallDistance;
	public double lastFallDistance;
	private Setting nofallMode;
	public static Setting blink;
	
	public NoFall() {
        super("NoFall", "Prevents from taking fall damage", Keyboard.KEY_NONE, Category.Player);
    }
	
	@Override
	public void moduleSetup() {
        final ArrayList<String> options = new ArrayList<String>();
        
        options.add("Vanilla");
        options.add("SpoofGround");
        options.add("Duplicate");
        options.add("NoGround");
        options.add("Collide");
        options.add("Vulcan");
        
        Axyl.ins.settingManager.createSetting(nofallMode = new Setting("Mode", this, "Vanilla", options));
        Axyl.ins.settingManager.createSetting(blink = new Setting("Blink", this, false, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void updateEvent(EventPlayerUpdate event) {
		if(mc.thePlayer.capabilities.isFlying || mc.thePlayer.isSpectator()) {
			return;
		}
		this.suffix = nofallMode.getValString();
    	if(mc.thePlayer.onGround) {
        	if(!packets.isEmpty()) {
         		packets.forEach(PacketUtil::sendPacketNoEvent);
            	packets.clear();
        	}
    		fallDistance = 0;
    	} else {
    		if(lastFallDistance != mc.thePlayer.fallDistance) {
    			fallDistance+=mc.thePlayer.fallDistance/2.1307;
    			lastFallDistance = mc.thePlayer.fallDistance;
    		}
    	}
    	if(mc.thePlayer.onGround) {
        	if(nofallMode.getValString().equalsIgnoreCase("Duplicate")) {
                posy = (int)mc.thePlayer.posY-4-0.2336320060424839;
        	} else {
        		posy = (int)mc.thePlayer.posY-3-0.2336320060424839;
        	}
    	}
        if(!inVoid()) {
        	if(nofallMode.getValString().equalsIgnoreCase("Vanilla")) {
	        	if(mc.thePlayer.fallDistance >= 3f) {
        			if(blink.getValBoolean()) {
        				packets.add(new C03PacketPlayer(true));
        			} else {
        				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        			}
        			//mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer).down(1), EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 1, 0));
        			mc.thePlayer.fallDistance = 0;
	        	}
        	}
        }
	}
    @Subscribe
    public void onPacket(EventPacket event) {
    	if(mc.thePlayer.capabilities.isFlying  || mc.thePlayer.isSpectator()) {
    		fallDistance = 0;
    		mc.thePlayer.fallDistance = 0;
    		packets.clear();
    	}
		if(mc.theWorld == null || mc.thePlayer.isSpectator() || mc.thePlayer.capabilities.isFlying)
			return;
		
        if(!inVoid()) {
        	if(nofallMode.getValString().equalsIgnoreCase("NoGround")) {
        		if(event.getPacket() instanceof C03PacketPlayer) {
        			C03PacketPlayer p = (C03PacketPlayer) event.getPacket();
       				p.onGround = false;
        			p.y += Math.random() / 100000000000000000000f;
        		}
        	}
            if(nofallMode.getValString().equalsIgnoreCase("Duplicate")) {
	        	if(mc.thePlayer.posY < posy) {
	        		if(event.getPacket() instanceof C03PacketPlayer) {
	        			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
	        			//PacketUtil.sendPacket(event.getPacket());
	        			p.onGround = true;
            			posy = (int)mc.thePlayer.posY-3;
	        		}
        		}
            }
	        if(nofallMode.getValString().equalsIgnoreCase("Vulcan")) {
	        	if(mc.thePlayer.posY < posy-0.5) {
	        		if(event.getPacket() instanceof C03PacketPlayer) {
            			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
            			p.onGround = true;
            			if(mc.thePlayer.hurtTime > 0) {
            				p.y+=RandomUtils.nextDouble(0.1, 0.125);
            				mc.thePlayer.motionY = 0;
            			}
            			posy = (int)mc.thePlayer.posY-3;
            		}
        		}
	        }
	        if(nofallMode.getValString().equalsIgnoreCase("SpoofGround")) {
	        	if(mc.thePlayer.posY < posy) {
	        		if(event.getPacket() instanceof C03PacketPlayer) {
            			C03PacketPlayer p = (C03PacketPlayer)event.getPacket();
            			p.onGround = true;
            			//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.20000000298023224, mc.thePlayer.posZ);
            			//p.y = posy;
            			//PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ), EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 1f, 0.4f, 1f));
            			posy = (int)mc.thePlayer.posY-3+0.2336320060424839;
            		}
        		}
	        }
			if(blink.getValBoolean()) {
				if(!(event.getPacket() instanceof C0FPacketConfirmTransaction) && !(event.getPacket() instanceof C00PacketKeepAlive))
				if(event.getPacketDirection().equals(PacketDir.OUT)) {
					if(fallDistance >= 2.8f) {
						packets.add(event.getPacket());
						event.setCancelled(true);
					}
				}
			}
        } else {

        }
    }
    
    @Subscribe
    public void onCollide(EventBoundingBox event) {
    	if(mc.theWorld != null) {
            if(nofallMode.getValString().equalsIgnoreCase("Collide")) {
    	        if(mc.thePlayer.fallDistance >= 2.5f) {
    	        	mc.gameSettings.keyBindJump.pressed = false;
    	        	event.setAxisAlignedBB(new AxisAlignedBB(-42, -1, -42, 42, 1, 42).offset(mc.thePlayer.posX, (int)mc.thePlayer.posY-1, mc.thePlayer.posZ));
    	        }
            }
    	}
    }
    
    public boolean inVoid() {
    	if(Axyl.ins.modManager.getModuleByName("Flight").isToggled()) {
    		return true;
    	}
    	for (double posY = 0; posY < mc.thePlayer.posY; posY++) {
    		if (!(mc.theWorld.getBlockState(new BlockPos((mc.thePlayer).posX, posY, (mc.thePlayer).posZ)).getBlock() instanceof BlockAir)) {
    			return false;
    		}
    	} 
    	return true;
    }
    
    @Override
    public void onEnable() {
    	posy = mc.thePlayer.posY-3;
    	lastFallDistance = 0;
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	if(!packets.isEmpty()) {
    		packets.forEach(PacketUtil::sendPacketNoEvent);
        	packets.clear();
    	}
    	super.onDisable();
    }
}
