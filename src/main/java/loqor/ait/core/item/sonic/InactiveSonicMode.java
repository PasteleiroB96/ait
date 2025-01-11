package loqor.ait.core.item.sonic;

import net.minecraft.util.Identifier;

import loqor.ait.data.schema.sonic.SonicSchema;

public class InactiveSonicMode extends SonicMode {

    protected InactiveSonicMode(int index) {
        super(index);
    }

    @Override
    public int maxTime() {
        return 0;
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.inactive();
    }
}
