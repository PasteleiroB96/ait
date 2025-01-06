package loqor.ait.core.blockentities;

import static loqor.ait.core.blockentities.ConsoleBlockEntity.nextConsole;
import static loqor.ait.core.blockentities.ConsoleBlockEntity.nextVariant;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import loqor.ait.AITMod;
import loqor.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import loqor.ait.core.AITBlockEntityTypes;
import loqor.ait.core.AITBlocks;
import loqor.ait.core.AITItems;
import loqor.ait.core.world.TardisServerWorld;
import loqor.ait.data.schema.console.ConsoleTypeSchema;
import loqor.ait.data.schema.console.ConsoleVariantSchema;
import loqor.ait.registry.impl.console.ConsoleRegistry;
import loqor.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class ConsoleGeneratorBlockEntity extends InteriorLinkableBlockEntity {
    public static final Identifier SYNC_TYPE = AITMod.id("sync_gen_type");
    public static final Identifier SYNC_VARIANT = AITMod.id("sync_gen_variant");
    private Identifier type;
    private Identifier variant;

    public ConsoleGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CONSOLE_GENERATOR_ENTITY_TYPE, pos, state);
        this.type = ConsoleRegistry.HARTNELL.id();
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (!TardisServerWorld.isTardisDimension(world))
            return;

        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() == AITItems.SONIC_SCREWDRIVER) {
            this.createConsole(player);
            return;
        }

        if (stack.isOf(Items.BLAZE_POWDER)) {
            stack.decrement(1);

            this.createConsole(player);
            return;
        }

        world.playSound(null, this.pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 0.5f, 1.0f);

        if (sneaking) {
            this.changeConsole(nextVariant(this.getConsoleVariant()));
        } else {
            this.changeConsole(nextConsole(this.getConsoleSchema()));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.type != null)
            nbt.putString("console", this.type.toString());
        if (this.variant != null)
            nbt.putString("variant", this.variant.toString());
    }

    private void createConsole(PlayerEntity player) {
        ConsoleBlockEntity consoleBlockEntity = new ConsoleBlockEntity(pos, AITBlocks.CONSOLE.getDefaultState());

        consoleBlockEntity.setType(this.getConsoleSchema());
        consoleBlockEntity.setVariant(this.getConsoleVariant());

        if (world == null)
            return;

        if (this.tardis().isPresent() && !this.tardis().get().isUnlocked(this.getConsoleVariant())) {
            player.sendMessage(Text.literal("This console is not unlocked yet!").formatted(Formatting.ITALIC), true);
            world.playSound(null, this.pos, SoundEvents.ENTITY_GLOW_ITEM_FRAME_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f);
            return;
        }

        // ConsoleBlockEntity marks for controls when it gets linked
        world.setBlockState(this.pos, AITBlocks.CONSOLE.getDefaultState());
        world.addBlockEntity(consoleBlockEntity);

        world.playSound(null, this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }

    public ConsoleTypeSchema getConsoleSchema() {
        if (type == null) {
            this.setConsoleSchema(ConsoleRegistry.HARTNELL.id());
        }

        return ConsoleRegistry.REGISTRY.get(type);
    }

    public void setConsoleSchema(Identifier type) {
        this.type = type;

        this.markDirty();
        this.syncType();

        if (this.getWorld() instanceof ServerWorld serverWorld)
            serverWorld.getChunkManager().markForUpdate(this.pos);
    }

    public ConsoleVariantSchema getConsoleVariant() {
        if (this.variant == null)
            this.variant = this.getConsoleSchema().getDefaultVariant().id();

        return ConsoleVariantRegistry.getInstance().get(this.variant);
    }

    public void setVariant(Identifier variant) {
        this.variant = variant;

        this.markDirty();
        this.syncVariant();

        if (this.getWorld() instanceof ServerWorld serverWorld)
            serverWorld.getChunkManager().markForUpdate(this.pos);
    }

    public void changeConsole(ConsoleTypeSchema schema) {
        this.setConsoleSchema(schema.id());
        this.setVariant(schema.getDefaultVariant().id());
    }

    public void changeConsole(ConsoleVariantSchema schema) {
        this.setConsoleSchema(schema.parent().id());
        this.setVariant(schema.id());
    }

    private void syncType() {
        if (!hasWorld() || world.isClient())
            return;

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(getConsoleSchema().id().toString());
        buf.writeBlockPos(getPos());

        for (PlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_TYPE, buf); // safe cast as we know its server
        }
    }

    private void syncVariant() {
        if (!hasWorld() || world.isClient())
            return;

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(getConsoleVariant().id().toString());
        buf.writeBlockPos(getPos());

        for (PlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_VARIANT, buf); // safe cast as we know its
                                                                                        // server
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("console")) {
            Identifier console = new Identifier(nbt.getString("console"));
            this.setConsoleSchema(console);
        }

        if (nbt.contains("variant")) {
            Identifier variant = new Identifier(nbt.getString("variant"));
            this.setVariant(variant);
        }

        super.readNbt(nbt);
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
