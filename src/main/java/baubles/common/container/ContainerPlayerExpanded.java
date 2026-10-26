package baubles.common.container;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * A copy of the vanilla player inventory menu with seven additional bauble slots.
 */
public class ContainerPlayerExpanded extends AbstractContainerMenu {

    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = 5;
    public static final int ARMOR_SLOT_START = 5;
    public static final int ARMOR_SLOT_END = 9;
    public static final int BAUBLE_SLOT_START = 9;
    public static final int BAUBLE_SLOT_END = 16;
    public static final int INV_SLOT_START = 16;
    public static final int INV_SLOT_END = 43;
    public static final int USE_ROW_SLOT_START = 43;
    public static final int USE_ROW_SLOT_END = 52;
    public static final int SHIELD_SLOT = 52;

    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private static final ResourceLocation[] EMPTY_ARMOR_SLOTS = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS};

    public final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
    public final ResultContainer resultSlots = new ResultContainer();
    public final IBaublesItemHandler baubles;
    private final Player owner;

    public ContainerPlayerExpanded(int windowId, Inventory inventory) {
        super(Baubles.EXPANDED_INVENTORY.get(), windowId);
        this.owner = inventory.player;
        this.baubles = BaublesApi.getBaublesHandler(owner);

        this.addSlot(new ResultSlot(owner, this.craftSlots, this.resultSlots, 0, 154, 28));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new Slot(this.craftSlots, j + i * 2, 116 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 4; k++) {
            EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[k];
            Slot armor = new Slot(inventory, 39 - k, 8, 8 + k * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return owner.getEquipmentSlotForItem(stack) == equipmentSlot;
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack stack = this.getItem();
                    return stack.isEmpty()
                            || !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)
                            || player.isCreative();
                }
            };
            // Slot#setBackground expects (atlas location, sprite name) - same order as ArmorSlot.
            armor.setBackground(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOTS[k]);
            this.addSlot(armor);
        }

        this.addSlot(new SlotBauble(owner, baubles, 0, 77, 8));
        this.addSlot(new SlotBauble(owner, baubles, 1, 77, 8 + 18));
        this.addSlot(new SlotBauble(owner, baubles, 2, 77, 8 + 2 * 18));
        this.addSlot(new SlotBauble(owner, baubles, 3, 77, 8 + 3 * 18));
        this.addSlot(new SlotBauble(owner, baubles, 4, 96, 8));
        this.addSlot(new SlotBauble(owner, baubles, 5, 96, 8 + 18));
        this.addSlot(new SlotBauble(owner, baubles, 6, 96, 8 + 2 * 18));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }

        Slot shield = new Slot(inventory, 40, 96, 62);
        shield.setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
        this.addSlot(shield);

        this.slotsChanged(this.craftSlots);
    }

    @Override
    public void slotsChanged(Container container) {
        Level level = this.owner.level();
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
        if (!(this.owner instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;

        ItemStack result = ItemStack.EMPTY;
        CraftingInput input = this.craftSlots.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipe = serverLevel.getServer().getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, serverLevel);
        if (recipe.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = recipe.get();
            CraftingRecipe craftingRecipe = holder.value();
            ItemStack assembled = craftingRecipe.assemble(input, serverLevel.registryAccess());
            if (assembled.isItemEnabled(serverLevel.enabledFeatures())) {
                result = assembled;
                this.resultSlots.setRecipeUsed(holder);
            }
        }

        this.resultSlots.setItem(0, result);
        this.setRemoteSlot(0, result);
        serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket(
                this.containerId, this.incrementStateId(), 0, result));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultSlots.clearContent();
        if (!player.level().isClientSide) {
            this.clearContainer(player, this.craftSlots);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, result);
            } else if (index >= CRAFT_SLOT_START && index < ARMOR_SLOT_END) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= BAUBLE_SLOT_START && index < BAUBLE_SLOT_END) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (BaublesApi.isBauble(stack)) {
                if (!this.moveItemStackTo(stack, BAUBLE_SLOT_START, BAUBLE_SLOT_END, false)
                        && !this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_START) {
                if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_START, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return result;
    }
}
