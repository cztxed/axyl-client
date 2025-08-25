package axyl.client.modules.features.visuals;

import java.awt.Color;  
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.eventbus.Subscribe;

import axyl.client.Axyl;
import axyl.client.events.render.EventRender2D;
import axyl.client.font.Fonts;
import axyl.client.gui.clickgui.settings.Setting;
import axyl.client.modules.Category;
import axyl.client.modules.Module;
import axyl.client.modules.features.combat.AntiBot;
import axyl.client.modules.features.combat.KillAura;
import axyl.client.util.math.MathUtils;
import axyl.client.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class ESP extends Module {
	
	private Setting espFilled;
	private Setting espArmor;

	public ESP() {
        super("ESP", "Allows You to see players through walls", Keyboard.KEY_NONE, Category.Visuals);
    }
	
	@Override
	public void moduleSetup() {
        Axyl.ins.settingManager.createSetting(this.espFilled = new Setting("Filled", this, true, false));
        Axyl.ins.settingManager.createSetting(this.espArmor = new Setting("Armor bar", this, true, false));
        super.moduleSetup();
	}
	
    @Subscribe
    public void eventRender2D(EventRender2D event) {
        GL11.glPushMatrix();
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
        	if (entity instanceof EntityPlayer && !AntiBot.getBots().contains(entity)) {
                EntityLivingBase ent = (EntityLivingBase) entity;
                Vector4f position = null;
                if (ValidEnt(ent) && RenderUtil.isInViewFrustrum(ent) || (ent.equals(mc.thePlayer) && RenderUtil.isInViewFrustrum(ent) && !(mc.gameSettings.thirdPersonView == 0))) {
                    double posX = entity.posX;
                    double posY = entity.posY;
                    double posZ = entity.posZ;
                    double width = entity.width / 2;
                    double height = entity.height + (entity.isSneaking() ? -0.15 : 0.1);
                    
                    AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height + 0.05, posZ + width);
                    List<Vector3f> vectors = Arrays.asList(new Vector3f((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ), new Vector3f((float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ), new Vector3f((float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ), new Vector3f((float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ), new Vector3f((float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ), new Vector3f((float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ), new Vector3f((float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ), new Vector3f((float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ));
                    
                    mc.entityRenderer.setupCameraTransform(1f, 0);
                    for (Vector3f vector : vectors) {
                        vector = RenderUtil.project(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                        if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                            if (position == null) {
                                position = new Vector4f((float)vector.x, (float)vector.y, (float)vector.z, 0.0f);
                            }
                            position.x = (float) Math.min(vector.x, position.x);
                            position.y = (float) Math.min(vector.y, position.y);
                            position.z = (float) Math.max(vector.x, position.z);
                            position.w = (float) Math.max(vector.y, position.w);
                        }
                    }
                    
                    mc.entityRenderer.setupOverlayRendering();
                    if (position != null) {

    			        float x2 = (float) position.z;
                        float x = (float) position.x;
                        float w = (float) position.z - x;
                        float y = (float) position.y;
                        float h = (float) position.w - y;
                        
                        float size = 1f;
                        double v = Fonts.no_AA_arial_small.getStringWidth(ent.getName());
                        
                        String stng = ent.getName()  + ("  §7|§f  "+(MathUtils.roundToPlace(ent.getDistanceToEntity(mc.thePlayer), 2))+"m");
                        String stng2 = ent.getName()  + ("  |  "+(MathUtils.roundToPlace(ent.getDistanceToEntity(mc.thePlayer), 2))+"m");
                        
                        String text2 = MathUtils.roundToPlace(((ent.getHealth()*10)/200)*100, 0)+"%";

                        {
                            if(espFilled.getValBoolean()) {
                            	RenderUtil.drawBorderedRect(x + 1.5, y - 1.5, w - 0.5, h + 3, -0.5f, new Color(25, 25, 25, 200).getRGB(), 0x2f000000 + new Color(0, 0, 0).getRGB());
                            }
                        }
                        
                        if(ent.equals(KillAura.target)) {
                        	RenderUtil.drawBorderedRect(x + 1.5+w/2- Fonts.esp_font_tahoma.getStringWidth(stng2)/2-5, y - 2.5-9, Fonts.esp_font_tahoma.getStringWidth(stng2)+7, 8, -0.5f, new Color(50, 50, 75).getRGB(), new Color(50, 50, 255, 50).getRGB());
                        } else if(ent.hurtTime > 0) {
                        	RenderUtil.drawBorderedRect(x + 1.5+w/2- Fonts.esp_font_tahoma.getStringWidth(stng2)/2-5, y - 2.5-9, Fonts.esp_font_tahoma.getStringWidth(stng2)+7, 8, -0.5f, new Color(75, 20, 0).getRGB(), new Color(255, 20, 0, 50).getRGB());
                        } else {
                        	RenderUtil.drawBorderedRect(x + 1.5+w/2- Fonts.esp_font_tahoma.getStringWidth(stng2)/2-5, y - 2.5-9, Fonts.esp_font_tahoma.getStringWidth(stng2)+7, 8, -0.5f, new Color(25, 25, 25, 200).getRGB(), new Color(25, 25, 25, 100).getRGB());
                        }

                        Fonts.esp_font_tahoma.drawCenteredString(stng2, x+w/2, y - 2.5f-5-0.5f, 3);
                        Fonts.esp_font_tahoma.drawCenteredString(stng2, x+w/2, y - 2.5f-5+0.5f, 2);
                        Fonts.esp_font_tahoma.drawCenteredString(stng2, x+w/2+0.5f, y - 2.5f-5, 1);
                        Fonts.esp_font_tahoma.drawCenteredString(stng2, x+w/2-0.5f, y - 2.5f-5, 0);
                        Fonts.esp_font_tahoma.drawCenteredString(stng, x+w/2, y - 2.5f-5, -1);
                        
                        {
                        	RenderUtil.drawBar(x + 3f / 2, y - 1+h+5, w+0.5f, h + 2, 10, (float)MathUtils.clamp(((int)ent.getHealth()) / 2, 0, 10), HealthColor(ent));
                        }

                        {
                            //RenderUtil.drawCornerRect(x + 2.0 - 2 / 2 + 0.5, y - 1 - 2 / 2 + 0.5f, w - 0.5f, h + 1.5f + 2, 1, 0x6f000000, true, 0.5f, stng);
                            //RenderUtil.drawCornerRect(x + 2.0 - 2 / 2, y - 1.0 - 2 / 2, w - 0.5f, h + 2 + 1.5f, 1, 0x6f000000, true, 0.5f, "");
                            //RenderUtil.drawCornerRect(x + 2.5f - 2 / 2, y - 0.5f - 2 / 2, w - 1, h + 1 + 2, 0.5, color.getRGB(), false, 0, "");
                        }
                        
                        {
                            if(espArmor.getValBoolean()) {
                                double armorstrength = 0;
                                EntityPlayer player = (EntityPlayer) entity;
                                for (int index = 3; index >= 0; index--) {
                                    final ItemStack stack = player.inventory.armorInventory[index];
                                    if (stack != null) {
                                        armorstrength += getArmorStrength(stack);
                                    }
                                }
                                if(armorstrength > 0)
                                	RenderUtil.drawBar(x + 3f / 2, y - 1+h+8.5f, w+0.5f, h + 2, 4, (int) (Math.min(armorstrength, 40) / 10), 0xff58ffFF);
                            }
                        }
                    }
                }
            }
        });
        GL11.glPopMatrix();
    }
    private boolean ValidEnt(EntityLivingBase entity) {
        return (!entity.equals(mc.thePlayer)) && entity.getEntityId() != -1488 && entity.isEntityAlive();
    }
    
    private int HealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }
   
    private double getArmorStrength(final ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemArmor)) return -1;
        float damageReduction = ((ItemArmor) itemStack.getItem()).damageReduceAmount;
        Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if (enchantments.containsKey(Enchantment.protection.effectId)) {
            int level = (int) enchantments.get(Enchantment.protection.effectId);
            damageReduction += Enchantment.protection.calcModifierDamage(level, DamageSource.generic);
        }
        return damageReduction;
    }
}
