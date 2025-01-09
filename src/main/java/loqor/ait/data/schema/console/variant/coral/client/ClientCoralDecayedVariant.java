package loqor.ait.data.schema.console.variant.coral.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.client.models.consoles.ConsoleModel;
import loqor.ait.client.models.consoles.CoralConsoleModel;
import loqor.ait.data.schema.console.ClientConsoleVariantSchema;
import loqor.ait.data.schema.console.variant.coral.CoralDecayedVariant;

public class ClientCoralDecayedVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/coral_decayed.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/coral_decayed_emission.png"));

    public ClientCoralDecayedVariant() {
        super(CoralDecayedVariant.REFERENCE, CoralDecayedVariant.REFERENCE);
    }

    @Override
    public Identifier texture() {
        return TEXTURE;
    }

    @Override
    public Identifier emission() {
        return EMISSION;
    }

    @Override
    public ConsoleModel model() {
        return new CoralConsoleModel(CoralConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(1.15f, 1.2f, 0.5f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{90f, 135f};
    }
}
