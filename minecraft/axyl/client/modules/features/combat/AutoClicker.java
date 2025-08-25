package axyl.client.modules.features.combat;

import java.awt.AWTException;
import java.awt.Event;  
import java.awt.List;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.input.InputClickEvent;
import axyl.client.events.player.EventTick;
import axyl.client.events.render.EventRender2D;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.math.MathUtils;
import axyl.client.util.network.PacketUtil;
import axyl.client.util.render.RenderUtil;
import axyl.client.util.time.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AutoClicker extends Module
{
	private int[] kList = {1,-1,0,-2,-1,1,2,-2,1,-1,0,-1,0,-1,2,-2,1,1,0,-1,-2};
	public boolean x;
	public int kurtois;
	private final Queue<Long> cpsList = new LinkedList<>();
	private final ArrayList<Long> ys = new ArrayList<>();
	private final ArrayList<Integer> avg = new ArrayList<>();
	private final Timer xCooldown = new Timer();
	private final Timer avgTimer = new Timer();
	private final Timer timer = new Timer();
	public int lastAvg;
	public int RANDOM_CPS;
	
	public Setting cps;

	public AutoClicker() {
		super("AutoClicker", "", 0, Category.Combat);
	}
	
	@Override
	public void moduleSetup() {
		Axyl.ins.settingManager.createSetting(cps = new Setting("Clicks Per Second (CPS)", this, 14, 1, 20, 0, false));
		super.moduleSetup();
	}
	
	@Subscribe
	public void eventUpdate(EventTick event) throws AWTException {
		this.suffix = ""+(int)cps.getValDouble()+" CPS";
		if(kurtois >= kList.length)
			kurtois = 0;
		
		{
			if(!cpsList.isEmpty())
			while (cpsList.peek() < System.currentTimeMillis()) {
				cpsList.remove();
			}
		}
		
		float a = 0;
		for(int i = 0; i < avg.size(); i++) {
			a+=avg.get(i);
		}
		
		a = a/avg.size();
		if(avg.size() >= 5) {
			avg.remove(0);
		}
		
		if(avgTimer.getTimePassed() > (Math.round(RandomUtils.nextInt(700, 1600) / 250) * 250)) {
			if(Math.abs(a-lastAvg) > 0.5f) {
				lastAvg = (int) a;
				avgTimer.reset();
			}
		}
		
        int l = 50; 
        int kurtoisFix = kList[kurtois];
        
        if(cpsList.size() >= RANDOM_CPS) {
            l = (int) (Math.round(RandomUtils.nextInt(100, 200) / 50) * 50)-kurtoisFix;
        } else {
            l = (int) (Math.round(RandomUtils.nextInt(0, 50) / 50) * 50)+kurtoisFix;
        }
        
        if(!avgTimer.hasReached(Math.round(RandomUtils.nextInt(325, 550) / 50) * 50)) {
        	l+=Math.round(RandomUtils.nextInt(0, 150) / 50) * 50;
        }
        
        if(mc.thePlayer.hurtTime == 0) {
        	x = false;
        }
        Robot robot = new Robot();
    	if(mc.gameSettings.keyBindAttack.isKeyDown()) {
    		if(mc.thePlayer.hurtTime == 10) {
    			if(xCooldown.hasReached(500)) {
    				x = true;
    				xCooldown.reset();
    			}
    		}
    		if(l < 0) {
    			l = 0;
    		} else if(l > 250) {
    			l = 250;
    		}
    		if(timer.hasReached(l) || x) {
            	RANDOM_CPS = (int) (cps.getValDouble()+(RandomUtils.nextInt(0, 5)-RandomUtils.nextInt(0, 3)));
    			int attackKey = mc.gameSettings.keyBindAttack.getKeyCode();
    			kurtois++;
    			avg.add(cpsList.size());
				
				Point point = MouseInfo.getPointerInfo().getLocation();
				int x = (int) point.getX();
				int y = (int) point.getY();
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode);
    			timer.reset();
    		} else {
    			
    		}
        } else {
        	this.ys.clear();
        	RANDOM_CPS = (int) (cps.getValDouble()+(RandomUtils.nextInt(0, 2)-RandomUtils.nextInt(0, 2)));
        	cpsList.clear();
        	x = false;
        	kurtois = 0;
        }
        if(x) {
        	if(mc.objectMouseOver.entityHit != null)
        		PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
        		PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.ATTACK));
        	x = false;
        }
	}
	
	@Subscribe
	public void eventClick(InputClickEvent event) {
		cpsList.add(System.currentTimeMillis() + 1000L);
		ys.add((long) cpsList.size());
	}
	
	@Subscribe
	public void eventrender2d(EventRender2D event) {

	}
    
    @Override
    public void onEnable() {
    	x = false;
    	lastAvg = 0;
    	kurtois = 0;
    	super.onEnable();
    }
}
