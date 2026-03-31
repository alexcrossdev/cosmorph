package me.alexcrossdev.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexcrossdev.CosmorphClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ToggleCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cosmorph")
                            .then(ClientCommandManager.literal("toggle")
                                    .executes(ctx -> toggle(ctx.getSource()))
                            )
            );
        });
    }

    private static int toggle(FabricClientCommandSource source) {
        CosmorphClient.ENABLED = !CosmorphClient.ENABLED;

        source.sendFeedback(
                Text.literal("Cosmorph: " + (CosmorphClient.ENABLED ? "Enabled" : "Disabled"))
        );

        return Command.SINGLE_SUCCESS;
    }
}
