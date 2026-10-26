package baubles.common;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.UUID;

/**
 * Development smoke test. Enable with {@code -Dbaubles.smoketest=true} to have a
 * dedicated server log the state of the mod after startup.
 */
public class SmokeTest {

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean("baubles.smoketest")) return;

        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.randomUUID(), "BaublesSmokeTest"));

        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        Baubles.LOGGER.info("[smoketest] slots={}", handler.getSlots());

        ItemStack ring = new ItemStack(Baubles.RING.get());
        Baubles.LOGGER.info("[smoketest] item registered={} id={}",
                !ring.isEmpty(), BuiltInRegistries.ITEM.getKey(ring.getItem()));
        Baubles.LOGGER.info("[smoketest] slot0 accepts ring (amulet slot, expected false)={}",
                handler.isItemValidForSlot(0, ring, player));
        Baubles.LOGGER.info("[smoketest] slot1 accepts ring (ring slot, expected true)={}",
                handler.isItemValidForSlot(1, ring, player));

        handler.setStackInSlot(1, ring);
        ItemStack equipped = handler.getStackInSlot(1);
        Baubles.LOGGER.info("[smoketest] equipped in slot1={} count={}", equipped.getItem(), equipped.getCount());

        ItemStack dirt = new ItemStack(net.minecraft.world.item.Items.DIRT);
        handler.setStackInSlot(2, dirt);
        Baubles.LOGGER.info("[smoketest] non-bauble rejected (expected true = still empty)={}",
                handler.getStackInSlot(2).isEmpty());

        Baubles.LOGGER.info("[smoketest] bauble slot search={}", BaublesApi.isBaubleEquipped(player, ring.getItem()));

        server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(), "baubles help");
        Baubles.LOGGER.info("[smoketest] /baubles help executed without exception");

        Baubles.LOGGER.info("[smoketest] menu type={}", Baubles.EXPANDED_INVENTORY.get());
        Baubles.LOGGER.info("[smoketest] done");
    }
}
