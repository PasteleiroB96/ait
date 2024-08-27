package loqor.ait.data.schema.exterior.variant.adaptive;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.animation.PulsatingAnimation;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.data.schema.door.impl.AdaptiveDoorVariant;
import loqor.ait.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.data.schema.exterior.category.AdaptiveCategory;
import loqor.ait.registry.impl.door.DoorRegistry;

public class AdaptiveVariant extends ExteriorVariantSchema {

    public AdaptiveVariant() {
        super(AdaptiveCategory.REFERENCE, new Identifier(AITMod.MOD_ID, "exterior/adaptive"));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(AdaptiveDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.1, -0.5); // NORTH
            case 1, 2, 3 -> pos.add(0.38, 0.1, -0.38); // NORTH EAST p n
            case 4 -> pos.add(0.5, 0.1, 0); // EAST
            case 5, 6, 7 -> pos.add(0.38, 0.1, 0.38); // SOUTH EAST p p
            case 8 -> pos.add(0, 0.1, 0.5); // SOUTH
            case 9, 10, 11 -> pos.add(-0.38, 0.1, 0.38); // SOUTH WEST n p
            case 12 -> pos.add(-0.5, 0.1, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.38, 0.1, -0.38); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public double portalHeight() {
        return 2.1d;
    }

    @Override
    public double portalWidth() {
        return 0.75d;
    }
}
