package baubles.client.gui;

import baubles.client.BaublesClient;
import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * The player inventory screen with the seven extra bauble slots.
 */
public class GuiPlayerExpanded extends AbstractContainerScreen<ContainerPlayerExpanded> {

    public static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Baubles.MOD_ID, "textures/gui/expanded_inventory.png");

    public GuiPlayerExpanded(ContainerPlayerExpanded menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 115;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        for (Slot slot : this.menu.slots) {
            if (slot.hasItem() && slot.getMaxStackSize() == 1) {
                graphics.blit(BACKGROUND, x + slot.x, y + slot.y, 200, 0, 16, 16);
            }
        }

        if (this.minecraft != null && this.minecraft.player != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                    x + 26, y + 8, x + 78, y + 78, 30, 0.0625F,
                    (float) (x + 51) - (float) mouseX,
                    (float) (y + 75 - 50) - (float) mouseY,
                    this.minecraft.player);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == BaublesClient.KEY_BAUBLES.getKey().getValue()) {
            this.displayNormalInventory();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Switches back to the vanilla inventory screen.
     *
     * <p>Deliberately avoids {@code LocalPlayer#closeContainer()}: that closes the screen first
     * ({@code setScreen(null)}), which grabs the mouse and makes the cursor snap to the centre of
     * the screen before the inventory opens again. Sending the close packet and swapping the
     * screen in one go keeps the cursor where it is.</p>
     */
    public void displayNormalInventory() {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null || minecraft.player == null || minecraft.getConnection() == null) return;

        minecraft.getConnection().send(new ServerboundContainerClosePacket(this.menu.containerId));
        minecraft.player.containerMenu = minecraft.player.inventoryMenu;
        minecraft.setScreen(new InventoryScreen(minecraft.player));
    }
}
