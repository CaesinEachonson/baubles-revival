package baubles.api.cap;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * The inventory of a player's bauble slots.
 */
public interface IBaublesItemHandler extends IItemHandlerModifiable {

    boolean isItemValidForSlot(int slot, ItemStack stack, LivingEntity entity);

    /**
     * Used internally to prevent equip/unequip events from triggering when they should not.
     */
    boolean isEventBlocked();

    void setEventBlock(boolean blocked);

    /**
     * Used internally for syncing. Indicates if the slot has changed since the last sync.
     */
    boolean isChanged(int slot);

    void setChanged(int slot, boolean changed);
}
