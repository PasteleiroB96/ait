package loqor.ait.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;

import loqor.ait.core.AITItems;
import loqor.ait.core.AITSounds;
import loqor.ait.core.data.DirectedGlobalPos;
import loqor.ait.core.util.AITModTags;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.data.DoorHandler;
import loqor.ait.tardis.data.loyalty.Loyalty;
import loqor.ait.tardis.data.travel.TravelHandler;
import loqor.ait.tardis.link.LinkableItem;
import loqor.ait.tardis.util.EnumSet;
import loqor.ait.tardis.util.Ordered;
import loqor.ait.tardis.util.TardisUtil;

public class KeyItem extends LinkableItem {

    private final EnumSet<Protocols> protocols;

    public KeyItem(Settings settings, Protocols... abs) {
        super(settings.maxCount(1), true);

        this.protocols = new EnumSet<>(Protocols::values);
        this.protocols.addAll(abs);
    }

    public enum Protocols implements Ordered {
        SNAP, HAIL, PERCEPTION, SKELETON;

        @Override
        public int index() {
            return ordinal();
        }
    }

    public boolean hasProtocol(Protocols var) {
        return this.protocols.contains(var);
    }

    public static boolean isKeyInInventory(PlayerEntity player) {
        return player.getInventory().contains(AITModTags.Items.KEY);
    }

    public static Collection<ItemStack> getKeysInInventory(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack stack : player.getInventory().main) {
            if (stack != null && stack.getItem() instanceof KeyItem)
                items.add(stack);
        }

        return items;
    }

    public static boolean hasMatchingKeyInInventory(PlayerEntity player, Tardis tardis) {
        Collection<ItemStack> keys = getKeysInInventory(player);

        for (ItemStack stack : keys) {
            KeyItem key = (KeyItem) stack.getItem();

            if (key.hasProtocol(Protocols.SKELETON))
                return true;

            Tardis found = KeyItem.getTardis(player.getWorld(), stack);

            if (found == tardis)
                return true;
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        Tardis tardis = KeyItem.getTardis(world, stack);

        if (tardis == null)
            return;

        KeyItem.hailMary(tardis, stack, player);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        Entity owner = entity.getOwner();

        if (!(owner instanceof ServerPlayerEntity player))
            return;

        Tardis tardis = KeyItem.getTardis(entity.getWorld(), entity.getStack());

        if (tardis == null)
            return;

        tardis.loyalty().subLevel(player, 5);
        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.CLOISTER);
    }

    private static void hailMary(Tardis tardis, ItemStack stack, PlayerEntity player) {
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem()))
            return;

        if (!tardis.stats().hailMary().get())
            return;

        TravelHandler travel = tardis.travel();
        KeyItem keyType = (KeyItem) stack.getItem().asItem();

        if (travel.handbrake())
            return;

        if (!keyType.hasProtocol(Protocols.HAIL))
            return;

        if (!tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))
            return;

        if (player.getHealth() > 4 || player.getWorld() == TardisUtil.getTardisDimension())
            return;

        World world = player.getWorld();
        BlockPos pos = player.getBlockPos();

        DirectedGlobalPos.Cached globalPos = DirectedGlobalPos.Cached.create((ServerWorld) world, pos,
                (byte) RotationPropertyHelper.fromYaw(player.getBodyYaw()));

        travel.dematerialize();
        travel.forceDestination(globalPos);
        travel.rematerialize();

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80, 3));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 6 * 20, 3));

        player.getItemCooldownManager().set(stack.getItem(), 60 * 20);

        tardis.stats().hailMary().set(false);
        tardis.<DoorHandler>handler(TardisComponent.Id.DOOR).previouslyLocked().set(false);

        // like a sound to show its been called
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 5f, 0.1f);
        world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 5f, 0.1f);
    }

    /*
     * @Override public ActionResult useOnBlock(ItemUsageContext context) { World
     * world = context.getWorld(); BlockPos pos = context.getBlockPos();
     * PlayerEntity player = context.getPlayer(); ItemStack stack =
     * context.getStack();
     *
     * if (world.isClient()) return ActionResult.SUCCESS;
     *
     * if (player == null || !player.isSneaking()) return ActionResult.PASS;
     *
     * if (!(world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlock))
     * return ActionResult.PASS;
     *
     * if (consoleBlock.tardis().isEmpty()) return ActionResult.FAIL;
     *
     * Tardis tardis = consoleBlock.tardis().get();
     *
     * if (tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION)) {
     * this.link(stack, consoleBlock.tardis().get()); return ActionResult.SUCCESS; }
     *
     * player.sendMessage(Text.translatable("message.ait.tardis.trust_issue",
     * true)); return ActionResult.FAIL; }
     */

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem() == AITItems.SKELETON_KEY)
            tooltip.add(Text.literal("CREATIVE ONLY SKELETON KEY").formatted(Formatting.DARK_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
