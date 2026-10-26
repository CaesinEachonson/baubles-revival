package baubles.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Implemented by items that can be worn in bauble slots.
 *
 * @author Azanor
 */
public interface IBauble {

    /**
     * The type of bauble this is. Determines which slots it can go into.
     */
    BaubleType getBaubleType(ItemStack stack);

    /**
     * Called once per tick while the bauble is worn.
     */
    default void onWornTick(ItemStack stack, LivingEntity entity) {
    }

    /**
     * Called when the bauble is equipped.
     */
    default void onEquipped(ItemStack stack, LivingEntity entity) {
    }

    /**
     * Called when the bauble is unequipped.
     */
    default void onUnequipped(ItemStack stack, LivingEntity entity) {
    }

    /**
     * Can this bauble be placed in a bauble slot?
     */
    default boolean canEquip(ItemStack stack, LivingEntity entity) {
        return true;
    }

    /**
     * Can this bauble be removed from a bauble slot?
     */
    default boolean canUnequip(ItemStack stack, LivingEntity entity) {
        return true;
    }

    /**
     * Will this bauble automatically sync to nearby clients when its stack changes?
     */
    default boolean willAutoSync(ItemStack stack, LivingEntity entity) {
        return false;
    }
}
