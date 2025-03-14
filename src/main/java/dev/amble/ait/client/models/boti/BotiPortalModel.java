package dev.amble.ait.client.models.boti;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class BotiPortalModel extends SinglePartEntityModel {
    private final ModelPart BOTI;
    public BotiPortalModel(ModelPart root) {
        this.BOTI = root.getChild("BOTI");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData BOTI = modelPartData.addChild("BOTI", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -32.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        BOTI.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return BOTI;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}