package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class BaublesNetwork {

    private BaublesNetwork() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, BaublesNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(OpenBaublesInventoryPayload.TYPE, OpenBaublesInventoryPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        serverPlayer.openMenu(new SimpleMenuProvider(
                                (windowId, inventory, player) -> new ContainerPlayerExpanded(windowId, inventory),
                                Component.translatable("container.baubles")));
                    }
                }));

        registrar.playToClient(SyncBaublesPayload.TYPE, SyncBaublesPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() == null) return;
                    if (context.player().level().getEntity(payload.entityId()) instanceof net.minecraft.world.entity.player.Player target) {
                        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(target);
                        if (payload.slot() >= 0 && payload.slot() < handler.getSlots()) {
                            handler.setStackInSlot(payload.slot(), payload.stack());
                        }
                    }
                }));
    }

    /**
     * Sends every slot of the given player to everyone tracking them (and to themselves).
     */
    public static void syncAll(ServerPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < handler.getSlots(); i++) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                    new SyncBaublesPayload(player.getId(), i, handler.getStackInSlot(i)));
        }
    }

    public static void syncSlotToTracking(ServerPlayer player, int slot) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new SyncBaublesPayload(player.getId(), slot, handler.getStackInSlot(slot)));
    }
}
