package loqor.ait.client.sounds.hum;

import static loqor.ait.AITMod.AIT_CONFIG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import loqor.ait.client.sounds.LoopingSound;
import loqor.ait.client.sounds.PlayerFollowingLoopingSound;
import loqor.ait.client.sounds.PlayerFollowingSound;
import loqor.ait.client.util.ClientTardisUtil;
import loqor.ait.registry.impl.CreakRegistry;
import loqor.ait.tardis.data.travel.TravelHandlerBase;
import loqor.ait.tardis.sound.CreakSound;
import loqor.ait.tardis.util.SoundHandler;
import loqor.ait.tardis.wrapper.client.ClientTardis;

public class ClientCreakHandler extends SoundHandler {

    private static final Random random = new Random();

    public static ClientCreakHandler create() {
        ClientCreakHandler handler = new ClientCreakHandler();

        handler.generateCreaks();
        return handler;
    }

    private void generateCreaks() {
        this.sounds = this.registryToList();
    }

    /**
     * Converts all the {@link CreakSound}'s in the {@link CreakRegistry} to
     * {@link LoopingSound} so they are usable
     *
     * @return A list of {@link LoopingSound} from the {@link CreakRegistry}
     */
    private List<LoopingSound> registryToList() {
        List<LoopingSound> list = new ArrayList<>();

        for (CreakSound sound : CreakRegistry.REGISTRY) {
            list.add(new PlayerFollowingLoopingSound(sound.sound(), SoundCategory.AMBIENT,
                    AIT_CONFIG.INTERIOR_HUM_VOLUME()));
        }

        return list;
    }

    public BlockPos randomNearConsolePos(BlockPos consolePos) {
        return consolePos.add(random.nextInt(8) - 1, 0, random.nextInt(8) - 1);
    }

    public void playRandomCreak(ClientTardis current) {
        CreakSound chosen = CreakRegistry.getRandomCreak();

        if (current.siege().isActive() && chosen.equals(CreakRegistry.WHISPER)) {
            current.getDesktop().getConsolePos()
                    .forEach(console -> startIfNotPlaying(
                            new PositionedSoundInstance(chosen.sound(), SoundCategory.HOSTILE, 0.5f, 1.0f,
                                    net.minecraft.util.math.random.Random.create(), randomNearConsolePos(console))));

            return;
        } else if (chosen.equals(CreakRegistry.WHISPER)) {
            return;
        }

        PlayerFollowingSound following = new PlayerFollowingSound(chosen.sound(), SoundCategory.AMBIENT,
                AIT_CONFIG.INTERIOR_HUM_VOLUME());
        startIfNotPlaying(following);
    }

    public void tick(MinecraftClient client) {
        if (this.sounds == null)
            this.generateCreaks();

        ClientTardis current = ClientTardisUtil.getCurrentTardis();

        if (current == null) {
            this.stopSounds();
            return;
        }

        // todo should they play even with power? just make them more rare??
        if (current.engine().hasPower()
                && (current.travel().getState() == TravelHandlerBase.State.LANDED || current.travel().autopilot())) {
            this.stopSounds();
            return;
        }

        // theyre in a tardis and theres no power so play creaks boi
        if (random.nextInt(512) == 32)
            this.playRandomCreak(current);
    }
}
