package me.alexcrossdev.mixin.client;

import me.alexcrossdev.CosmorphClient;
import me.alexcrossdev.data.CosmorphData;
import me.alexcrossdev.data.CosmorphStorage;
import me.alexcrossdev.parser.TextParser;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void cosmorphOverrideName(CallbackInfoReturnable<Text> cir) {
        if (!CosmorphClient.ENABLED) return;

        ItemStack stack = (ItemStack)(Object)this;

        NbtComponent component = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) return;
        String uuid = component.copyNbt().getString("uuid", null);
        if (uuid == null) return;

        CosmorphData data = CosmorphStorage.get(UUID.fromString(uuid));

        if (data == null) return;
        if (data.name() == null) return;

        cir.setReturnValue(TextParser.parse(data.name()));
    }
}
