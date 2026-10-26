package baubles.api.cap;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the baubles inventory.
 */
public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

    public static final int BAUBLE_SLOTS = 7;

    private final boolean[] changed = new boolean[BAUBLE_SLOTS];
    private boolean blockEvents = false;

    public BaublesContainer() {
        super(BAUBLE_SLOTS);
    }

    public BaublesContainer(List<ItemStack> stacks) {
        super(BAUBLE_SLOTS);
        for (int i = 0; i < Math.min(stacks.size(), BAUBLE_SLOTS); i++) {
            this.stacks.set(i, stacks.get(i));
        }
    }

    public List<ItemStack> toList() {
        List<ItemStack> list = new ArrayList<>(BAUBLE_SLOTS);
        for (int i = 0; i < BAUBLE_SLOTS; i++) {
            list.add(this.stacks.get(i));
        }
        return list;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, LivingEntity entity) {
        if (stack.isEmpty()) return false;
        IBauble bauble = BaublesApi.getBauble(stack);
        if (bauble == null || !bauble.getBaubleType(stack).hasSlot(slot)) return false;
        return entity == null || bauble.canEquip(stack, entity);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, null)) {
            super.setStackInSlot(slot, stack);
        }
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.isItemValidForSlot(slot, stack, null)) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isEventBlocked() {
        return blockEvents;
    }

    @Override
    public void setEventBlock(boolean blocked) {
        this.blockEvents = blocked;
    }

    @Override
    protected void onContentsChanged(int slot) {
        setChanged(slot, true);
    }

    @Override
    public boolean isChanged(int slot) {
        return changed[slot];
    }

    @Override
    public void setChanged(int slot, boolean change) {
        this.changed[slot] = change;
    }
}
