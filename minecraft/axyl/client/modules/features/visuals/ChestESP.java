package axyl.client.modules.features.visuals;

import net.minecraft.client.Minecraft;      
import java.util.Comparator;

import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import axyl.client.events.render.EventRender3D;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.util.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;

import java.awt.Color;
import java.util.ArrayList;

public class ChestESP extends Module
{ 
    public ChestESP() {
        super("ChestESP", "" , 0, Category.Visuals);
    }

    @Subscribe
    public void eventRender3D(EventRender3D event) {
        for (TileEntity tile : mc.theWorld.loadedTileEntityList) {
            double posX = tile.getPos().getX() - mc.getRenderManager().renderPosX;
            double posY = tile.getPos().getY() -mc.getRenderManager().renderPosY;
            double posZ = tile.getPos().getZ() - mc.getRenderManager().renderPosZ;
            if (tile instanceof TileEntityChest) {

                AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(posX, posY, posZ);
                TileEntityChest adjacent = null;
                if (((TileEntityChest) tile).adjacentChestXNeg != null)
                    adjacent = ((TileEntityChest) tile).adjacentChestXNeg;
                if (adjacent != null)
                    bb = bb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(adjacent.getPos().getX() - mc.getRenderManager().renderPosX, adjacent.getPos().getY() - mc.getRenderManager().renderPosY, adjacent.getPos().getZ() - mc.getRenderManager().renderPosZ));

                if (((TileEntityChest) tile).getChestType() == 1) {
                   drawBlockESP(bb, 255f, 91f, 86f, 255f,1f);
                } else {
                   drawBlockESP(bb, 255f, 180f, 0f, 25f,1f);
                }

            }
            if (tile instanceof TileEntityEnderChest) {
               drawBlockESP(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(posX, posY, posZ),78f, 7f, 205f, 255f,1f);
            }
        }
    }
    
    private void drawBlockESP(AxisAlignedBB bb, float red, float green, float blue, float alpha,float width) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0.2f);
        RenderUtil.drawBoundingBox(bb);
        GL11.glLineWidth(width);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
}
