package me.alexcrossdev.screen;

import me.alexcrossdev.data.CosmorphData;
import me.alexcrossdev.data.CosmorphStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class CosmorphScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget modelField;

    public CosmorphScreen() {
        super(Text.of("Cosmorph"));
    }

    @Override
    protected void init() {
        int midX = this.width / 2;

        nameField = new TextFieldWidget(this.textRenderer, midX - 100, this.height / 2 - 40, 200, 20, Text.literal("Item Name"));
        this.addSelectableChild(nameField);

        modelField = new TextFieldWidget(this.textRenderer, midX - 100, this.height / 2 - 10, 200, 20, Text.literal("Model ID"));
        this.addSelectableChild(modelField);

        ItemStack stack = MinecraftClient.getInstance().player.getMainHandStack();

        NbtComponent component = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        Identifier itemModel = stack.getComponents().get(DataComponentTypes.ITEM_MODEL);
        String uuid = component.copyNbt().getString("uuid", null);

        if (uuid == null)
            MinecraftClient.getInstance().setScreen(null);

        CosmorphData data = CosmorphStorage.get(UUID.fromString(uuid));

        if (data == null) {
            nameField.setText(stack.getName().getString());
            modelField.setText(itemModel.toString());
        } else {
            nameField.setText(data.getName());
            modelField.setText(data.getModel());
        }

        ButtonWidget updateButton = ButtonWidget.builder(Text.literal("Update"), button -> {
            String modelId = modelField.getText();
            if (!modelId.isEmpty() && !nameField.getText().isEmpty()) {
                CosmorphStorage.set(UUID.fromString(uuid),
                        new CosmorphData(nameField.getText().toString(), modelId)
                );
            }
            MinecraftClient.getInstance().setScreen(null);
        }).size(80, 20).position(midX + 10, this.height / 2 + 20).build();
        ButtonWidget resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            CosmorphStorage.remove(UUID.fromString(uuid));
            MinecraftClient.getInstance().setScreen(null);
        }).size(80, 20).position(midX - 90, this.height / 2 + 20).build();

        this.addDrawableChild(updateButton);
        this.addDrawableChild(resetButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        nameField.render(context,mouseX,mouseY,deltaTicks);
        modelField.render(context, mouseX, mouseY, deltaTicks);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
