package baubles.client;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.common.network.OpenBaublesInventoryPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientEvents {

    @SubscribeEvent
    public void clientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            while (BaublesClient.KEY_BAUBLES.consumeClick()) {
                // drain
            }
            return;
        }
        while (BaublesClient.KEY_BAUBLES.consumeClick()) {
            if (mc.screen == null) {
                PacketDistributor.sendToServer(new OpenBaublesInventoryPayload());
            }
        }
    }

    @SubscribeEvent
    public void itemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        IBauble bauble = BaublesApi.getBauble(stack);
        if (bauble != null) {
            event.getToolTip().add(Component.translatable("name." + bauble.getBaubleType(stack).name())
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
