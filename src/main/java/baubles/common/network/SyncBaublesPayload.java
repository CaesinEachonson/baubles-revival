package baubles.common.network;

import baubles.common.Baubles;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Sends a single bauble slot of a player to nearby clients so the equipped bauble can be rendered.
 */
public record SyncBaublesPayload(int entityId, int slot, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncBaublesPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Baubles.MOD_ID, "sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBaublesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncBaublesPayload::entityId,
            ByteBufCodecs.VAR_INT, SyncBaublesPayload::slot,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncBaublesPayload::stack,
            SyncBaublesPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
