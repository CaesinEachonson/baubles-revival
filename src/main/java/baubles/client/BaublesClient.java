package baubles.client;

import baubles.client.gui.GuiEvents;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = Baubles.MOD_ID, dist = Dist.CLIENT)
public class BaublesClient {

    public static final KeyMapping KEY_BAUBLES = new KeyMapping(
            "keybind.baublesinventory",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.inventory");

    public BaublesClient(IEventBus modBus) {
        modBus.addListener(this::registerKeys);
        modBus.addListener(this::registerScreens);
        modBus.addListener(this::addLayers);

        NeoForge.EVENT_BUS.register(new ClientEvents());
        NeoForge.EVENT_BUS.register(new GuiEvents());
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_BAUBLES);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Baubles.EXPANDED_INVENTORY.get(), GuiPlayerExpanded::new);
    }

    private void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model model : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(model);
            if (renderer != null) {
                renderer.addLayer(new BaublesRenderLayer(renderer));
            }
        }
    }
}
