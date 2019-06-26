package com.minelittlepony.client.render;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface IPonyRender<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends PonyModelConstants {

    /**
     * Gets the wrapped pony model for this renderer.
     */
    ModelWrapper<T, M> getModelWrapper();

    IPony getEntityPony(T entity);

    RenderPony<T, M> getInternalRenderer();

    Identifier findTexture(T entity);

    /**
     * Called by riders to have their transportation adjust their position.
     */
    default void translateRider(T entity, IPony entityPony, LivingEntity passenger, IPony passengerPony, float ticks) {
        if (!passengerPony.getRace(false).isHuman()) {
            //float yaw = MathUtil.interpolateDegress(entity.prevRenderYawOffset, entity.renderYawOffset, ticks);
            float yaw = MathUtil.interpolateDegress((float)entity.prevRenderY, (float)entity.y, ticks);

            getModelWrapper().apply(entityPony.getMetadata());
            M model = getModelWrapper().getBody();

            model.transform(BodyPart.BACK);

            getInternalRenderer().applyPostureRiding(entity, yaw, ticks);
        }
    }
}
