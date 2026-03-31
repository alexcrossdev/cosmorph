package me.alexcrossdev.parser;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Stack;

public class TextParser {

    public static Text parse(String input) {
        MutableText result = Text.empty();
        StringBuilder buffer = new StringBuilder();

        Stack<Style> styleStack = new Stack<>();
        Style currentStyle = Style.EMPTY.withItalic(false);

        for (int i = 0; i < input.length();) {
            if (input.charAt(i) == '<') {
                int end = input.indexOf('>', i);

                if (end == -1) break;

                String tag = input.substring(i + 1, end);

                if (!buffer.isEmpty()) {
                    result.append(
                            Text.literal(buffer.toString()).setStyle(currentStyle)
                    );
                    buffer.setLength(0);
                }

                if (tag.startsWith("#")) {
                    try {
                        if (tag.length() == 7) {
                            styleStack.push(currentStyle);
                            int color = Integer.parseInt(tag.substring(1), 16);
                            currentStyle = currentStyle.withColor(color);
                        }
                    } catch (IllegalArgumentException ignored) {}
                } else if (tag.equals("/#")) {
                    if (!styleStack.empty()) {
                        currentStyle = styleStack.pop();
                    }
                } else if (tag.equals("b")) {
                    styleStack.push(currentStyle);
                    currentStyle = currentStyle.withBold(true);
                } else if (tag.equals("/b")) {
                    if (!styleStack.empty()) {
                        currentStyle = styleStack.pop();
                    }
                } else if (tag.equals("i")) {
                    styleStack.push(currentStyle);
                    currentStyle = currentStyle.withItalic(true);
                } else if (tag.equals("/i")) {
                    if (!styleStack.empty()) {
                        currentStyle = styleStack.pop();
                    }
                } else if (tag.equals("/")) {
                    if (!styleStack.empty()) {
                        currentStyle = styleStack.pop();
                    }
                } else {
                    try {
                        styleStack.push(currentStyle);
                        currentStyle = currentStyle.withColor(Formatting.valueOf(tag.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }

                i = end + 1;
            } else {
                buffer.append(input.charAt(i));
                i++;
            }
        }

        if (!buffer.isEmpty()) {
            result.append(
                    Text.literal(buffer.toString()).setStyle(currentStyle)
            );
        }

        return result;
    }
}
