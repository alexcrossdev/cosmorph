package me.alexcrossdev;

import me.alexcrossdev.command.CosmorphCommand;
import me.alexcrossdev.command.ToggleCommand;
import me.alexcrossdev.data.CosmorphStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

import static me.alexcrossdev.Cosmorph.MOD_ID;

public class CosmorphClient implements ClientModInitializer {
	public static boolean ENABLED = true;

	public static final File MOD_CONFIG_DIR = new File(
			FabricLoader.getInstance().getConfigDir().toFile(), MOD_ID
	);

	@Override
	public void onInitializeClient() {
		ToggleCommand.register();
		CosmorphCommand.register();

		CosmorphStorage.load();
	}
}