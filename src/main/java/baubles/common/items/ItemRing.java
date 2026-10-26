package baubles.common.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The demonstration bauble, grants Haste while worn.
 */
public class ItemRing extends Item implements IBauble {

    public ItemRing(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!level.isClientSide) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getStackInSlot(i).isEmpty() && baubles.isItemValidForSlot(i, held, player)) {
                    baubles.setStackInSlot(i, held.copyWithCount(1));
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    onEquipped(held, player);
                    break;
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    @Override
    public void onWornTick(ItemStack stack, LivingEntity entity) {
        if (entity.tickCount % 39 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 0, true, true));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void onEquipped(ItemStack stack, LivingEntity entity) {
        entity.playSound(SoundEvents.ARMOR_EQUIP_DIAMOND.value(), 0.75F, 1.9F);
    }

    @Override
    public void onUnequipped(ItemStack stack, LivingEntity entity) {
        entity.playSound(SoundEvents.ARMOR_EQUIP_DIAMOND.value(), 0.75F, 2.0F);
    }
}
