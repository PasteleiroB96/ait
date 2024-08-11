package loqor.ait.tardis.door;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.core.data.schema.door.DoorSchema;

public class EasterHeadDoorVariant extends DoorSchema {

    public static final Identifier REFERENCE = new Identifier(AITMod.MOD_ID, "door/easter_head");

    public EasterHeadDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }
}
