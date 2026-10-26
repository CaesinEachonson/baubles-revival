package baubles.client.gui;

import baubles.common.Baubles;
import baubles.common.network.OpenBaublesInventoryPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Small button shown in the top left of the inventory screen that toggles between the
 * normal and the expanded inventory.
 */
public class GuiBaublesButton extends AbstractWidget {

    private final boolean expanded;

    public GuiBaublesButton(int x, int y, boolean expanded) {
        super(x, y, 10, 10, Component.translatable(expanded ? "button.normal" : "button.baubles"));
        this.expanded = expanded;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = this.isHovered() ? 200 : 210;
        graphics.blit(GuiPlayerExpanded.BACKGROUND, this.getX(), this.getY(), u, 48, 10, 10);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (this.expanded) {
            if (mc.screen instanceof GuiPlayerExpanded expandedScreen) {
                expandedScreen.displayNormalInventory();
            } else {
                mc.player.closeContainer();
                mc.setScreen(new InventoryScreen(mc.player));
            }
        } else {
            PacketDistributor.sendToServer(new OpenBaublesInventoryPayload());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
