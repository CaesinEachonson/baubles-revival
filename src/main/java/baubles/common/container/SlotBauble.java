package baubles.common.container;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler {

    private final int baubleSlot;
    private final Player player;

    public SlotBauble(Player player, IBaublesItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
        this.baubleSlot = slot;
        this.player = player;
    }

    private IBaublesItemHandler handler() {
        return (IBaublesItemHandler) getItemHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return handler().isItemValidForSlot(baubleSlot, stack, player);
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = getItem();
        if (stack.isEmpty()) return false;
        IBauble bauble = BaublesApi.getBauble(stack);
        return bauble == null || bauble.canUnequip(stack, player);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (!handler().isEventBlocked()) {
            IBauble bauble = BaublesApi.getBauble(stack);
            if (bauble != null) bauble.onUnequipped(stack, player);
        }
        super.onTake(player, stack);
    }

    @Override
    public void set(ItemStack stack) {
        ItemStack old = getItem();
        boolean changed = !ItemStack.matches(stack, old);
        if (changed && !handler().isEventBlocked() && !old.isEmpty()) {
            IBauble bauble = BaublesApi.getBauble(old);
            if (bauble != null) bauble.onUnequipped(old, player);
        }

        super.set(stack);

        if (changed && !handler().isEventBlocked() && !stack.isEmpty()) {
            IBauble bauble = BaublesApi.getBauble(stack);
            if (bauble != null) bauble.onEquipped(stack, player);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
