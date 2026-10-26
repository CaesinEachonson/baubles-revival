package baubles.api.cap;

import baubles.common.Baubles;
import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Persistence/registration of the player baubles inventory.
 */
public final class BaublesAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Baubles.MOD_ID);

    /** Serialises the container as a list of seven (possibly empty) item stacks. */
    public static final Codec<BaublesContainer> CODEC =
            ItemStack.OPTIONAL_CODEC.listOf().xmap(BaublesContainer::new, BaublesContainer::toList);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BaublesContainer>> BAUBLES =
            ATTACHMENT_TYPES.register("baubles", () -> AttachmentType.builder(() -> new BaublesContainer())
                    .serialize(CODEC)
                    .copyOnDeath()
                    .build());

    private BaublesAttachments() {
    }

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
