package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.world.item.ItemStack;

/**
 * Convenience implementation of {@link IBauble} for a fixed {@link BaubleType}.
 */
public class BaubleItem implements IBauble {
    private final BaubleType baubleType;

    public BaubleItem(BaubleType type) {
        this.baubleType = type;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return baubleType;
    }
}
