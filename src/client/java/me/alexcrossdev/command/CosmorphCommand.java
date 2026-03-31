package me.alexcrossdev.command;

import com.mojang.brigadier.Command;
import me.alexcrossdev.screen.CosmorphScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CosmorphCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cosmorph")
                            .then(ClientCommandManager.literal("update")
                                    .executes(ctx -> update(ctx.getSource()))
                            )
            );
        });
    }

    private static int update(FabricClientCommandSource source) {
        source.getClient().send(() -> {
            MinecraftClient.getInstance().setScreen(new CosmorphScreen());
        });

        return Command.SINGLE_SUCCESS;
    }
}
