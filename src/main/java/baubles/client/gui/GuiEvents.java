package baubles.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class GuiEvents {

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InventoryScreen) && !(screen instanceof GuiPlayerExpanded)) return;

        int left = (screen.width - GUI_WIDTH) / 2;
        int top = (screen.height - GUI_HEIGHT) / 2;
        event.addListener(new GuiBaublesButton(left + 64, top + 9, screen instanceof GuiPlayerExpanded));
    }
}
