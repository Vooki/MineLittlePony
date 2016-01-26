package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.BodyPart;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.renderer.AniParams;

import net.minecraft.client.renderer.GlStateManager;

public abstract class AbstractHeadPart implements IPonyPart {

    private AbstractPonyModel pony;

    @Override
    public void init(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;
    }

    @Override
    public void render(PonyData data, float scale) {
        pony.transform(BodyPart.HEAD);
        GlStateManager.translate(pony.bipedHead.offsetX, pony.bipedHead.offsetY, pony.bipedHead.offsetZ);
    }

    @Override
    public final void animate(PonyData data, AniParams ani) {
        rotateHead(ani.horz, ani.vert);
        if (pony.isSneak && !pony.isFlying) {
            position(0, 6, -2);
        } else {
            position(0, 0, 0);
        }
    }

    private void rotateHead(float horz, float vert) {
        float y;
        float x;
        if (pony.isSleeping) {
            y = 1.4F;
            x = 0.1F;
        } else {
            y = horz / (float) (180 / Math.PI);
            x = vert / (float) (180 / Math.PI);
        }
        x = Math.min(x, 0.5F);
        x = Math.max(x, -0.5F);

        rotate(x, y);
    }

    protected AbstractPonyModel getPony() {
        return pony;
    }

    protected abstract void position(float posX, float posY, float posZ);

    protected abstract void rotate(float rotX, float rotY);

}
