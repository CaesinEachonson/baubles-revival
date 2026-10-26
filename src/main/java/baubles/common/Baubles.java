package baubles.common;

import baubles.api.cap.BaublesAttachments;
import baubles.common.event.CommandBaubles;
import baubles.common.event.EventHandlerEntity;
import baubles.common.items.ItemRing;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.network.BaublesNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Baubles.MOD_ID)
public class Baubles {

    public static final String MOD_ID = "baubles";
    public static final String MOD_NAME = "Baubles";
    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final DeferredItem<ItemRing> RING =
            ITEMS.registerItem("ring", ItemRing::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerPlayerExpanded>> EXPANDED_INVENTORY =
            MENUS.register("expanded_inventory", () -> IMenuTypeExtension.create(
                    (windowId, inventory, data) -> new ContainerPlayerExpanded(windowId, inventory)));

    public Baubles(IEventBus modBus, ModContainer container) {
        ITEMS.register(modBus);
        MENUS.register(modBus);
        BaublesAttachments.register(modBus);
        BaublesNetwork.register(modBus);

        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        modBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(new EventHandlerEntity());
        NeoForge.EVENT_BUS.register(new CommandBaubles());
        NeoForge.EVENT_BUS.register(new SmokeTest());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(RING);
        }
    }
}
