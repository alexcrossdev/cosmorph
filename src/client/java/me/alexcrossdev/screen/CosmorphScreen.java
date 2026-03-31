package me.alexcrossdev.screen;

import me.alexcrossdev.data.CosmorphData;
import me.alexcrossdev.data.CosmorphStorage;
import me.alexcrossdev.parser.TextParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class CosmorphScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget modelField;
    private TextWidget preview;
    private UUID uuid;

    private TextWidget titleWidget;
    private MultilineTextWidget helpWidget;

    private ItemStack previewStack;

    public CosmorphScreen() {
        super(Text.of("Cosmorph Editor"));
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        int y = 20;

        // Title
        titleWidget = new TextWidget(
                Text.literal("Cosmorph Editor").styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                MinecraftClient.getInstance().textRenderer
        );
        int titleWidth = textRenderer.getWidth(titleWidget.getMessage());
        titleWidget.setPosition(midX - titleWidth / 2, y);
        this.addDrawableChild(titleWidget);

        // Help block
        MutableText helpText = Text.literal("").styled(s -> s.withColor(Formatting.WHITE))
                .append(Text.literal("Formatting Tags\n").styled(s -> s.withColor(Formatting.GOLD)))
                .append(Text.literal("   <b>").append(Text.literal("bold").styled(s -> s.withBold(true))).append(Text.literal("</b>\n")))
                .append(Text.literal("   <i>").append(Text.literal("italic").styled(s -> s.withItalic(true))).append(Text.literal("</i>\n")))
                .append(Text.literal("   <#hex>").append(Text.literal("hex color").styled(s -> s.withColor(0x55FFFF))).append(Text.literal("<#/>\n")))
                .append(Text.literal("   <named>").append(Text.literal("named color").styled(s -> s.withColor(Formatting.LIGHT_PURPLE))).append(Text.literal("</>\n")));

        helpWidget = new MultilineTextWidget(helpText, MinecraftClient.getInstance().textRenderer);
        helpWidget.setPosition(10, y + 20);
        this.addDrawableChild(helpWidget);

        y += 100; // Start position for preview and fields

        // Preview
        preview = new TextWidget(Text.of(""), MinecraftClient.getInstance().textRenderer);
        this.addDrawableChild(preview);

        nameField = new TextFieldWidget(this.textRenderer, midX - 100, y, 200, 20, Text.literal("Item Name"));
        modelField = new TextFieldWidget(this.textRenderer, midX - 100, y + 30, 200, 20, Text.literal("Model ID"));

        nameField.setMaxLength(128);
        modelField.setMaxLength(128);

        this.addSelectableChild(nameField);
        this.addSelectableChild(modelField);

        int finalY = y;
        nameField.setChangedListener(text -> {
            preview.setMessage(TextParser.parse(text));
            int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(preview.getMessage());
            preview.setPosition(midX - textWidth / 2, finalY - 40);
        });

        ItemStack stack = MinecraftClient.getInstance().player.getMainHandStack();
        if (stack.isOf(Items.AIR) || stack.isEmpty()) {
            MinecraftClient.getInstance().setScreen(null);
            return;
        }
        previewStack = stack.copy();

        modelField.setChangedListener(text -> {
            try {
                Identifier modelId = Identifier.tryParse(text.toLowerCase());
                if (modelId != null) {
                    previewStack.set(DataComponentTypes.ITEM_MODEL, modelId);
                }
            } catch (Exception ignored) {}
        });

        NbtComponent component = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        Identifier itemModel = stack.getComponents().get(DataComponentTypes.ITEM_MODEL);

        if (component == null || !component.copyNbt().contains("uuid")) {
            MinecraftClient.getInstance().setScreen(null);
            return;
        }

        uuid = UUID.fromString(component.copyNbt().getString("uuid", null));
        CosmorphData data = CosmorphStorage.get(uuid);

        if (data == null) {
            nameField.setText(stack.getName().getString());
            preview.setMessage(stack.getName());
            if (itemModel != null) modelField.setText(itemModel.toString());
        } else {
            nameField.setText(data.name());
            preview.setMessage(TextParser.parse(data.name()));
            modelField.setText(data.model());
        }

        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(preview.getMessage());
        preview.setPosition(midX - textWidth / 2, y - 40);

        // Buttons
        ButtonWidget updateButton = ButtonWidget.builder(Text.literal("Update"), button -> {
            String nameText = nameField.getText();
            String modelText = modelField.getText().toLowerCase();
            Identifier modelId = Identifier.tryParse(modelText);
            if (modelId == null) return;
            if (!nameText.isEmpty() && !modelText.isEmpty()) {
                CosmorphStorage.set(uuid, new CosmorphData(nameText, modelText));
            }
            MinecraftClient.getInstance().setScreen(null);
        }).size(80, 20).position(midX + 10, y + 70).build();

        ButtonWidget resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            CosmorphStorage.remove(uuid);
            MinecraftClient.getInstance().setScreen(null);
        }).size(80, 20).position(midX - 90, y + 70).build();

        this.addDrawableChild(updateButton);
        this.addDrawableChild(resetButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!previewStack.isEmpty() && !previewStack.isOf(Items.AIR)) {
            int midX = this.width / 2;
            int previewSize = 64;
            int x = midX - previewSize / 2;
            int y = this.height / 2 + 20;

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(x, y);
            context.getMatrices().scale(previewSize / 16f, previewSize / 16f);

            context.drawItem(previewStack, 0, 0); // draw at scaled origin
            context.getMatrices().popMatrix();
        }

        super.render(context, mouseX, mouseY, deltaTicks);
        nameField.render(context, mouseX, mouseY, deltaTicks);
        modelField.render(context, mouseX, mouseY, deltaTicks);
    }
}