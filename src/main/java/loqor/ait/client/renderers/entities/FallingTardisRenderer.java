package loqor.ait.client.renderers.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;

import loqor.ait.client.models.exteriors.ExteriorModel;
import loqor.ait.client.models.exteriors.SiegeModeModel;
import loqor.ait.client.renderers.AITRenderLayers;
import loqor.ait.core.blocks.ExteriorBlock;
import loqor.ait.core.data.schema.exterior.ClientExteriorVariantSchema;
import loqor.ait.core.entities.FallingTardisEntity;
import loqor.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.TardisExterior;
import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.data.BiomeHandler;

public class FallingTardisRenderer extends EntityRenderer<FallingTardisEntity> {

    public FallingTardisRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FallingTardisEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        Tardis tardis = entity.tardis().get();

        if (tardis == null)
            return;

        TardisExterior tardisExterior = tardis.getExterior();
        ClientExteriorVariantSchema exteriorVariant = tardisExterior.getVariant().getClient();

        if (exteriorVariant == null)
            return;

        if (MinecraftClient.getInstance().player == null)
            return;

        Identifier texture = exteriorVariant.texture();
        Identifier emission = exteriorVariant.emission();
        ExteriorModel model = exteriorVariant.model();

        if (model == null)
            return;

        matrices.push();
        int k = entity.getBlockState().get(ExteriorBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.multiply(
                RotationAxis.NEGATIVE_Y.rotationDegrees(!exteriorVariant.equals(ClientExteriorVariantRegistry.DOOM)
                        ? 180f + h
                        : MinecraftClient.getInstance().player.getHeadYaw() + 180f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        boolean siege = tardis.siege().isActive();

        if (siege) {
            model = new SiegeModeModel(SiegeModeModel.getTexturedModelData().createModel());
            texture = tardis.siege().texture().get();
        }

        model.renderFalling(entity, model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light,
                OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (siege) {
            matrices.pop();
            return;
        }

        if (emission != null)
            model.renderFalling(entity, model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(emission, true)),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (!exteriorVariant.equals(ClientExteriorVariantRegistry.CORAL_GROWTH)) {
            BiomeHandler biome = tardis.handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = biome.getBiomeKey().get(exteriorVariant.overrides());

            if (biomeTexture != null && !exteriorVariant.texture().equals(biomeTexture)) {
                model.renderFalling(entity, model.getPart(), matrices,
                        vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(biomeTexture)), light,
                        OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            }
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(FallingTardisEntity entity) {
        if (entity.tardis().get() == null)
            return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE; // random texture just so i dont crash

        return entity.tardis().get().getExterior().getVariant().getClient().texture();
    }
}
