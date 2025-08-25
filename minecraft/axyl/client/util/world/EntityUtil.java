package axyl.client.util.world;

import java.util.List; 

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import axyl.client.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.optifine.reflect.Reflector;

public class EntityUtil extends Utility {
	
	public static Object[] getEntityCustom(float pitch, float yaw, final double distance, final double expand, final float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        final Entity var2 = mc.getRenderViewEntity();
        Entity entity = null;
        if (var2 == null || mc.theWorld == null) {
            return null;
        }
        mc.mcProfiler.startSection("pick");
        final net.minecraft.util.Vec3 var3 = var2.getPositionEyes(0.0f);
        final net.minecraft.util.Vec3 var4 = var2.getLook(pitch);
        final net.minecraft.util.Vec3 var5 = var3.addVector(var4.xCoord * distance, var4.yCoord * distance, var4.zCoord * distance);
        net.minecraft.util.Vec3 var6 = null;
        final float var7 = 1.0f;
        final java.util.List<Entity> var8 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox().addCoord(var4.xCoord * distance, var4.yCoord * distance, var4.zCoord * distance).expand(var7, var7, var7));
        double var9 = distance;
        for (int var10 = 0; var10 < var8.size(); ++var10) {
            final Entity var11 = (Entity) var8.get(var10);
            if (var11.canBeCollidedWith()) {
                final float var12 = var11.getCollisionBorderSize();

                AxisAlignedBB var13 = var11.getEntityBoundingBox().expand(var12, var12, var12);
                var13 = var13.expand(expand, 0, expand);
                final MovingObjectPosition var14 = var13.calculateIntercept(var3, var5);
                if (var13.isVecInside(var3)) {
                    if (0.0 < var9 || var9 == 0.0) {
                        entity = var11;
                        var6 = ((var14 == null) ? var3 : var14.hitVec);
                        var9 = 0.0;
                    }
                }
                else if (var14 != null) {
                    final double var15 = var3.distanceTo(var14.hitVec);
                    if (var15 < var9 || var9 == 0.0) {
                        boolean canRiderInteract = false;
                        if (var11 == var2.ridingEntity && !canRiderInteract) {
                            if (var9 == 0.0) {
                                entity = var11;
                                var6 = var14.hitVec;
                            }
                        }
                        else {
                            entity = var11;
                            var6 = var14.hitVec;
                            var9 = var15;
                        }
                    }
                }
            }
        }
        if (var9 < distance && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }
        mc.mcProfiler.endSection();
        if (entity == null || var6 == null) {
            return null;
        }
        return new Object[] { entity, var6 };
	}
	
    public static Entity getMouseOver(float yaw, float pitch, double ex)
    {	
    	float partialTicks = 1f;
        Entity entity = mc.getRenderViewEntity();
        Entity entToReturn = null;
        if (entity != null && mc.theWorld != null)
        {
            mc.mcProfiler.startSection("pick");
            double d0 = (double)mc.playerController.getBlockReachDistance();
            
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            int i = 3;

            if (mc.playerController.extendedReach())
            {
                d0 = 6.0D;
                d1 = 6.0D;
            }
            else if (d0 > 3.0D)
            {
                flag = true;
            }

            if (mc.objectMouseOver != null)
            {
                d1 = mc.thePlayer.rayTraceCustom(4.5f, 1f, yaw, pitch).hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getVectorForRotation(pitch, yaw);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = d1;

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity1 = (Entity)list.get(j);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1+ex, (double)f1, (double)f1+ex);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3))
                {
                    if (d2 >= 0.0D)
                    {
                        entToReturn = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                }
                else if (movingobjectposition != null)
                {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D)
                    {
                        boolean flag1 = false;

                        if (Reflector.ForgeEntity_canRiderInteract.exists())
                        {
                            flag1 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                        }

                        if (!flag1 && entity1 == entity.ridingEntity)
                        {
                            if (d2 == 0.0D)
                            {
                            	entToReturn = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        }
                        else
                        {
                        	entToReturn = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (entToReturn != null && flag && vec3.distanceTo(vec33) > 3.0D)
            {
            	entToReturn = null;
            }
            mc.mcProfiler.endSection();
        }
        
        return entToReturn;
    }
    
    public static Entity getMouseOver(float yaw, float pitch, double ex, double r)
    {	
    	float partialTicks = 1f;
        Entity entity = mc.getRenderViewEntity();
        Entity entToReturn = null;
        if (entity != null && mc.theWorld != null)
        {
            mc.mcProfiler.startSection("pick");
            double d0 = r;
            
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            int i = 3;

            if (mc.objectMouseOver != null)
            {
                d1 = mc.thePlayer.rayTraceCustom((float) r, 1f, yaw, pitch).hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getVectorForRotation(pitch, yaw);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return true;
                }
            }));
            double d2 = d1;

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity1 = (Entity)list.get(j);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1+ex, (double)f1, (double)f1+ex);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3))
                {
                    if (d2 >= 0.0D)
                    {
                        entToReturn = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                }
                else if (movingobjectposition != null)
                {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D)
                    {
                        boolean flag1 = false;

                        if (Reflector.ForgeEntity_canRiderInteract.exists())
                        {
                            flag1 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                        }

                        if (!flag1 && entity1 == entity.ridingEntity)
                        {
                            if (d2 == 0.0D)
                            {
                            	entToReturn = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        }
                        else
                        {
                        	entToReturn = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (entToReturn != null && flag && vec3.distanceTo(vec33) > 3.0D)
            {
            	entToReturn = null;
            }
            mc.mcProfiler.endSection();
        }
        
        return entToReturn;
    }
}
