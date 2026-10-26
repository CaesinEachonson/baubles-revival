package baubles.api;

import baubles.api.cap.BaublesAttachments;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Entry point of the Baubles API.
 *
 * @author Azanor
 */
public final class BaublesApi {

    private BaublesApi() {
    }

    /**
     * Retrieves the baubles inventory handler for the supplied player.
     */
    public static IBaublesItemHandler getBaublesHandler(Player player) {
        return player.getData(BaublesAttachments.BAUBLES);
    }

    /**
     * Returns the {@link IBauble} of the given stack, or null if the stack is not a bauble.
     * Items only have to implement {@link IBauble}; no capability lookup is required.
     */
    @Nullable
    public static IBauble getBauble(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        Item item = stack.getItem();
        return item instanceof IBauble bauble ? bauble : null;
    }

    public static boolean isBauble(ItemStack stack) {
        return getBauble(stack) != null;
    }

    /**
     * Returns the slot the given item type is equipped in, or -1 if it is not equipped.
     */
    public static int isBaubleEquipped(Player player, Item bauble) {
        IBaublesItemHandler handler = getBaublesHandler(player);
        for (int a = 0; a < handler.getSlots(); a++) {
            if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem() == bauble) return a;
        }
        return -1;
    }
}
