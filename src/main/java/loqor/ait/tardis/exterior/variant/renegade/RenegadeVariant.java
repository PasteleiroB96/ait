package loqor.ait.tardis.exterior.variant.renegade;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.data.schema.door.DoorSchema;
import loqor.ait.core.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.registry.impl.door.DoorRegistry;
import loqor.ait.tardis.animation.ExteriorAnimation;
import loqor.ait.tardis.animation.PulsatingAnimation;
import loqor.ait.tardis.data.loyalty.Loyalty;
import loqor.ait.tardis.door.RenegadeDoorVariant;
import loqor.ait.tardis.exterior.category.RenegadeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class RenegadeVariant extends ExteriorVariantSchema {
	protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/renegade/renegade_";

	protected RenegadeVariant(String name) {
		super(RenegadeCategory.REFERENCE, new Identifier(AITMod.MOD_ID, "exterior/renegade/" + name), new Loyalty(Loyalty.Type.PILOT));
	}

	@Override
	public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
		return new PulsatingAnimation(exterior);
	}

	@Override
	public DoorSchema door() {
		return DoorRegistry.REGISTRY.get(RenegadeDoorVariant.REFERENCE);
	}

	@Override
	public boolean hasPortals() {
		return true;
	}

	@Override
	public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
		return switch (direction) {
			case 0 -> pos.add(0, 0.255, -0.4); // NORTH
			case 1, 2, 3 -> pos.add(0.28, 0.225, -0.28); // NORTH EAST p n
			case 4 -> pos.add(0.4, 0.255, 0); // EAST
			case 5, 6, 7 -> pos.add(0.28, 0.225, 0.28); // SOUTH EAST p p
			case 8 -> pos.add(0, 0.255, 0.4); // SOUTH
			case 9, 10, 11 -> pos.add(-0.28, 0.225, 0.28); // SOUTH WEST n p
			case 12 -> pos.add(-0.4, 0.255, 0); // WEST
			case 13, 14, 15 -> pos.add(-0.28, 0.225, -0.28); // NORTH WEST n n
			default -> pos;
		};
	}

	@Override
	public double portalHeight() {
		return 2.3d;
	}

	@Override
	public double portalWidth() {
		return 1d;
	}
}