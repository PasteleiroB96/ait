package loqor.ait.api.link;

import java.util.Optional;
import java.util.UUID;

import loqor.ait.api.link.v2.TardisRef;
import loqor.ait.core.tardis.ServerTardis;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.TardisManager;

public abstract class LinkableLivingEntity extends LivingEntity {

    public static final TrackedData<Optional<UUID>> TARDIS_ID  = DataTracker.registerData(
            LinkableLivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private TardisRef cache;

    protected LinkableLivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void link(ServerTardis tardis) {
        this.dataTracker.set(TARDIS_ID, Optional.of(tardis.getUuid()));
        this.createCache(tardis.getUuid());
    }

    private void reloadCache() {
        UUID id = this.tardisId().orElse(null);

        if (id == null)
            return;

        this.createCache(id);
    }

    private void createCache(UUID id) {
        this.cache = TardisRef.createAs(this, id);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TARDIS_ID, Optional.empty());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (TARDIS_ID.equals(data))
            this.reloadCache();
    }

    public Tardis tardis() {
        if (this.cache != null)
            return cache.get();

        this.reloadCache();
        return cache != null ? cache.get() : null;
    }

    private Optional<UUID> tardisId() {
        return this.dataTracker.get(TARDIS_ID);
    }
}
