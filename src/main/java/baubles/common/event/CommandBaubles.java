package baubles.common.event;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

public class CommandBaubles {

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("baubles")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> help(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> help(ctx.getSource())))
                .then(Commands.literal("view")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> view(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))))
                .then(Commands.literal("clear")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> clearAll(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
                                .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                                        .executes(ctx -> clearSlot(ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "slot"))))));

        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(root);
        dispatcher.register(Commands.literal("baub").requires(source -> source.hasPermission(2)).redirect(node));
        dispatcher.register(Commands.literal("bau").requires(source -> source.hasPermission(2)).redirect(node));
    }

    private int help(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("commands.baubles.help.alias"), false);
        source.sendSuccess(() -> Component.translatable("commands.baubles.help.view"), false);
        source.sendSuccess(() -> Component.translatable("commands.baubles.help.clear"), false);
        return 1;
    }

    private int view(CommandSourceStack source, ServerPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        source.sendSuccess(() -> Component.translatable("commands.baubles.view.header", player.getDisplayName()), false);
        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            final int slot = i;
            IBauble bauble = BaublesApi.getBauble(stack);
            String type = bauble == null ? "?" : bauble.getBaubleType(stack).name();
            source.sendSuccess(() -> Component.literal(" [Slot " + slot + "] " + type + " " + stack.getHoverName().getString()), false);
        }
        return 1;
    }

    private int clearAll(CommandSourceStack source, ServerPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            baubles.setStackInSlot(i, ItemStack.EMPTY);
        }
        source.sendSuccess(() -> Component.translatable("commands.baubles.clear.all", player.getDisplayName()), false);
        player.sendSystemMessage(Component.translatable("commands.baubles.clear.all.notify", source.getDisplayName()));
        return 1;
    }

    private int clearSlot(CommandSourceStack source, ServerPlayer player, int slot) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (slot < 0 || slot >= baubles.getSlots()) {
            source.sendFailure(Component.translatable("commands.baubles.invalid_slot", slot));
            return 0;
        }
        baubles.setStackInSlot(slot, ItemStack.EMPTY);
        source.sendSuccess(() -> Component.translatable("commands.baubles.clear.slot", slot, player.getDisplayName()), false);
        player.sendSystemMessage(Component.translatable("commands.baubles.clear.slot.notify", slot, source.getDisplayName()));
        return 1;
    }

    @SuppressWarnings("unused")
    private static Collection<ServerPlayer> players(CommandSourceStack source) {
        return source.getServer().getPlayerList().getPlayers();
    }
}
