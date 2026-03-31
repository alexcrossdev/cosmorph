package me.alexcrossdev.mixin.client;

import me.alexcrossdev.CosmorphClient;
import me.alexcrossdev.data.CosmorphData;
import me.alexcrossdev.data.CosmorphStorage;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    @Inject(
            method = "update",
            at = @At("HEAD")
    )
    private void overrideItemModel(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, World world, HeldItemContext heldItemContext, int seed, CallbackInfo ci) {
        if (!CosmorphClient.ENABLED) return;

        NbtComponent component = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) return;

        Identifier itemModel = stack.getComponents().get(DataComponentTypes.ITEM_MODEL);
        if (itemModel == null) return;

        String uuid = component.copyNbt().getString("uuid", null);
        if (uuid == null) return;

        CosmorphData data = CosmorphStorage.get(UUID.fromString(uuid));

        if (data == null) return;
        if (data.getModel().isEmpty()) return;

        String[] split = data.getModel().split(":");

        if (split.length != 2) return;

        stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(split[0], split[1]));
    }
}
