package baubles.common.network;

import baubles.common.Baubles;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent by the client when the player wants to open the expanded (baubles) inventory.
 */
public record OpenBaublesInventoryPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenBaublesInventoryPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Baubles.MOD_ID, "open_inventory"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBaublesInventoryPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBaublesInventoryPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
