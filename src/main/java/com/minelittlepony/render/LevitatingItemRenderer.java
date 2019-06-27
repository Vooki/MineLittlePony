package com.minelittlepony.render;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.util.render.Color;
import com.mumfrey.liteloader.client.overlays.IMinecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import static net.minecraft.client.renderer.GlStateManager.*;

public class LevitatingItemRenderer {

    private static boolean usingTransparency;

    public static boolean enableItemGlowRenderProfile() {
        if (usesTransparency()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }

        return usesTransparency();
    }

    public static boolean usesTransparency() {
        return usingTransparency;
    }

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {
        pushMatrix();
        disableLighting();
        setColor(glowColor);

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        usingTransparency = true;

        scale(1.1F, 1.1F, 1.1F);

        translate(0.01F, 0.01F, 0.01F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);
        translate(-0.02F, -0.02F, -0.02F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);

        usingTransparency = false;
        unsetColor();
        enableLighting();
        popMatrix();

        // I hate rendering
    }

    private void setColor(int glowColor) {
        GL14.glBlendColor(Color.r(glowColor), Color.g(glowColor), Color.b(glowColor), 0.2F);
    }

    private void unsetColor() {
        GL14.glBlendColor(255, 255, 255, 1);
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(ItemRenderer renderer, AbstractClientPlayer entity, ItemStack stack, TransformType transform, boolean left) {
        IPony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        pushMatrix();

        boolean doMagic = MineLittlePony.getConfig().fpsmagic && pony.getMetadata().hasMagic();

        if (doMagic) {
            setupPerspective(entity, stack, left);
        }

        renderer.renderItemSide(entity, stack, transform, left);

        if (doMagic) {
            disableLighting();

            usingTransparency = true;

            setColor(pony.getMetadata().getGlowColor());

            scale(1.1F, 1.1F, 1.1F);

            translate(0.01F, 0.01F, 0.01F);
            renderer.renderItemSide(entity, stack, transform, left);
            translate(-0.02F, -0.02F, -0.02F);
            renderer.renderItemSide(entity, stack, transform, left);

            usingTransparency = false;

            unsetColor();
            enableLighting();
        }

        popMatrix();

        // I hate rendering
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(EntityLivingBase entity, ItemStack stack, boolean left) {
        EnumAction action = stack.getItemUseAction();

        boolean doNormal = entity.getItemInUseCount() <= 0 || action == EnumAction.NONE;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            float ticks = ((IMinecraft)Minecraft.getMinecraft()).getTimer().elapsedPartialTicks - entity.ticksExisted;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 6) / 40;

            boolean handHeldTool = stack.getItemUseAction() == EnumAction.BOW
                    || stack.getItemUseAction() == EnumAction.BLOCK;

            translate(driftAmount - floatAmount / 4, floatAmount, handHeldTool ? -0.3F : -0.6F);

            if (!stack.getItem().isFull3D() && !handHeldTool) { // bows have to point forwards
                if (left) {
                    rotate(-60, 0, 1, 0);
                    rotate(30, 0, 0, 1);
                } else {
                    rotate(60, 0, 1, 0);
                    rotate(-30, 0, 0, 1);
                }
            }
        }
    }
}
