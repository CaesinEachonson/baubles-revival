package baubles.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue RENDER_BAUBLES = BUILDER
            .comment("Set this to false to disable rendering of baubles on the player.")
            .define("client.renderBaubles", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean renderBaubles() {
        try {
            return RENDER_BAUBLES.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
