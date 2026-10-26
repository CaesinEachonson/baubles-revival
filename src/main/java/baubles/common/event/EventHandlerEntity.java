package baubles.common.event;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.network.BaublesNetwork;
import baubles.common.network.SyncBaublesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventHandlerEntity {

    private final Map<UUID, ItemStack[]> syncCache = new HashMap<>();

    @SubscribeEvent
    public void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            IBauble bauble = BaublesApi.getBauble(stack);
            if (bauble != null) {
                bauble.onWornTick(stack, player);
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            syncBaubles(serverPlayer, baubles);
        }
    }

    private void syncBaubles(ServerPlayer player, IBaublesItemHandler baubles) {
        ItemStack[] cached = this.syncCache.get(player.getUUID());
        if (cached == null || cached.length != baubles.getSlots()) {
            cached = new ItemStack[baubles.getSlots()];
            Arrays.fill(cached, ItemStack.EMPTY);
            this.syncCache.put(player.getUUID(), cached);
        }

        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            IBauble bauble = BaublesApi.getBauble(stack);
            boolean autoSync = bauble != null && bauble.willAutoSync(stack, player) && !ItemStack.matches(stack, cached[i]);
            if (baubles.isChanged(i) || autoSync) {
                BaublesNetwork.syncSlotToTracking(player, i);
                baubles.setChanged(i, false);
                cached[i] = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BaublesNetwork.syncAll(player);
        }
    }

    @SubscribeEvent
    public void playerStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer target)) return;
        if (!(event.getEntity() instanceof ServerPlayer receiver)) return;

        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(target);
        for (int i = 0; i < baubles.getSlots(); i++) {
            PacketDistributor.sendToPlayer(receiver,
                    new SyncBaublesPayload(target.getId(), i, baubles.getStackInSlot(i)));
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player replacement = event.getEntity();
        IBaublesItemHandler from = BaublesApi.getBaublesHandler(original);
        IBaublesItemHandler to = BaublesApi.getBaublesHandler(replacement);
        for (int i = 0; i < Math.min(from.getSlots(), to.getSlots()); i++) {
            to.setStackInSlot(i, from.getStackInSlot(i).copy());
        }
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        this.syncCache.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void playerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            ItemEntity drop = new ItemEntity(player.level(),
                    player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), stack.copy());
            drop.setPickUpDelay(40);
            float f1 = player.level().random.nextFloat() * 0.5F;
            float f2 = player.level().random.nextFloat() * (float) Math.PI * 2.0F;
            drop.setDeltaMovement(-Mth.sin(f2) * f1, 0.2, Mth.cos(f2) * f1);
            event.getDrops().add(drop);

            baubles.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
