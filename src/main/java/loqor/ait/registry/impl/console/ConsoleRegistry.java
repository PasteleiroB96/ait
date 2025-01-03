package loqor.ait.registry.impl.console;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import loqor.ait.AITMod;
import loqor.ait.data.schema.console.ConsoleTypeSchema;
import loqor.ait.data.schema.console.type.*;

public class ConsoleRegistry {

    public static final SimpleRegistry<ConsoleTypeSchema> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<ConsoleTypeSchema>ofRegistry(AITMod.id("console")))
            .buildAndRegister();

    public static ConsoleTypeSchema register(ConsoleTypeSchema schema) {
        return Registry.register(REGISTRY, schema.id(), schema);
    }

    public static ConsoleTypeSchema CORAL;
    public static ConsoleTypeSchema HARTNELL;
    public static ConsoleTypeSchema COPPER;
    public static ConsoleTypeSchema TOYOTA;
    public static ConsoleTypeSchema ALNICO;
    public static ConsoleTypeSchema STEAM;
    public static ConsoleTypeSchema HUDOLIN;

    public static void init() {
        HARTNELL = register(new HartnellType());
        CORAL = register(new CoralType());
        COPPER = register(new CopperType());
        TOYOTA = register(new ToyotaType());
        ALNICO = register(new AlnicoType());
        STEAM = register(new SteamType());
        // HUDOLIN = register(new HudolinType());
    }
}
