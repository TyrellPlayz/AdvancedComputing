package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.util.KeyListener;
import com.tyrellplayz.zlib.util.RenderUtil;
import com.tyrellplayz.zlib.util.helper.GLHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TextArea extends Component {

    private static final String UNFORMATTED_SPLIT = "(?<=%1$s)|(?=%1$s)";
    private static final String[] DELIMITERS = new String[]{"(\\s|$)(?=(([^\"]*\"){2})*[^\"]*$)", "[\\p{Punct}&&[^@\"]]", "\\p{Digit}+"};
    private static final String SPLIT_REGEX;
    static {
        StringJoiner joiner = new StringJoiner("|");

        for (String s : DELIMITERS) joiner.add(s);

        SPLIT_REGEX = String.format("(?<=%1$s)|(?=%1$s)", "(" + joiner + ")");
    }

    protected Font font;
    protected String text = "";
    protected String placeholder = null;
    private int padding = 4;
    private boolean isFocused = false;
    private boolean editable = true;
    private List<String> lines = new ArrayList();
    private int visibleLines;
    private int maxLines;
    private TextArea.ScrollBar scrollBar;
    private boolean scrollBarVisible = true;
    private int scrollBarSize = 3;
    private int horizontalScroll;
    private int verticalScroll;
    private int horizontalOffset;
    private int verticalOffset;
    private int cursorTick = 0;
    private int cursorX;
    private int cursorY;
    private int clickedX;
    private int clickedY;
    private boolean wrapText = false;
    private int maxLineWidth;
    private IHighlight highlight = null;
    private KeyListener keyListener = null;
    protected Color placeholderColour = new Color(1.0F, 1.0F, 1.0F, 0.35F);
    protected Color textColour;
    protected Color backgroundColour;
    protected Color secondaryBackgroundColour;
    protected Color borderColour;

    public TextArea(int left, int top, int width, int height) {
        super(left, top, width, height);
        this.textColour = Color.WHITE;
        this.backgroundColour = Color.DARK_GRAY;
        this.secondaryBackgroundColour = Color.GRAY;
        this.borderColour = Color.BLACK;
        this.font = Minecraft.getInstance().font;
        double i = height - this.padding * 2 + 1;
        this.visibleLines = (int)Math.floor(i / 9);
        this.lines.add("");
    }

    @Override
    public void tick() {
        ++this.cursorTick;
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawRectWithColour(stack,this.getXPos(), this.getYPos(), this.getWidth(), this.getHeight(), this.backgroundColour.darker().darker());
            RenderUtil.drawRectWithColour(stack,this.getXPos() + 1.0D, this.getYPos() + 1.0D, this.getWidth() - 1, this.getHeight() - 1, this.backgroundColour);
            if (!this.isFocused && this.placeholder != null && (this.lines.isEmpty() || this.lines.size() == 1 && this.lines.get(0).isEmpty())) {
                RenderSystem.enableBlend();
                RenderUtil.drawTextClipped(stack, this.placeholder, (int)this.getXPos() + this.padding, (int)this.getYPos() + this.padding, this.getWidth() - this.padding * 2, this.placeholderColour.getRGB(), false);
            }

            GLHelper.pushScissor((int)this.getXPos() + this.padding, (int)this.getYPos() + this.padding, this.getWidth() - this.padding * 2, this.getHeight() - this.padding * 2);

            int visibleWidth;
            float scrollPercentage;
            int scrollBarWidth;
            int relativeScrollX;
            int stringWidth;
            for(visibleWidth = 0; visibleWidth < this.visibleLines && visibleWidth + this.verticalScroll < this.lines.size(); ++visibleWidth) {
                //float scrollPercentage = (float)(this.verticalScroll + this.verticalOffset) / (float)(this.lines.size() - this.visibleLines);
                scrollPercentage = (float)this.maxLineWidth / (float)(this.getWidth() - this.padding * 2);
                scrollBarWidth = Mth.clamp(this.horizontalScroll + (int)((float)this.horizontalOffset * scrollPercentage), 0, Math.max(0, this.maxLineWidth - (this.getWidth() - this.padding * 2)));
                relativeScrollX = (int)((float)(this.lines.size() - this.visibleLines) * scrollPercentage);
                stringWidth = visibleWidth + Mth.clamp(relativeScrollX, 0, Math.max(0, this.lines.size() - this.visibleLines));
                String var10002;
                float var10003;
                if (this.highlight == null) {
                    var10002 = this.lines.get(stringWidth);
                    var10003 = (float)this.getXPos() + (float)this.padding - (float)scrollBarWidth;
                    float var34 = (float)this.getYPos() + (float)this.padding;
                    RenderUtil.drawText(stack, var10002, var10003, var34 + (float)(visibleWidth * 9), this.textColour);
                } else {
                    String[] words = this.lines.get(stringWidth).split(SPLIT_REGEX);
                    StringBuilder builder = new StringBuilder();
                    String[] var15 = words;
                    int var16 = words.length;

                    for(int var17 = 0; var17 < var16; ++var17) {
                        String word = var15[var17];
                        ChatFormatting[] formatting = this.highlight.getKeywordFormatting(word);
                        ChatFormatting[] var20 = formatting;
                        int var21 = formatting.length;

                        for(int var22 = 0; var22 < var21; ++var22) {
                            ChatFormatting format = var20[var22];
                            builder.append(format);
                        }

                        builder.append(word);
                        builder.append(ChatFormatting.RESET);
                    }

                    var10002 = builder.toString();
                    var10003 = (float)((int)this.getXPos() + this.padding - scrollBarWidth);
                    int var10004 = (int)this.getYPos() + this.padding;
                    RenderUtil.drawText(stack, var10002, var10003, (float)(var10004 + visibleWidth * 9), new Color(-1));
                }
            }

            GLHelper.popScissor();
            GLHelper.pushScissor((int)this.getXPos() + this.padding, (int)this.getYPos() + this.padding - 1, this.getWidth() - this.padding * 2 + 1, this.getHeight() - this.padding * 2 + 1);
            int visibleScrollBarWidth;
            if (this.editable && this.isFocused) {
                float linesPerUnit = (float)this.lines.size() / (float)this.visibleLines;
                visibleScrollBarWidth = Mth.clamp(this.verticalScroll + this.verticalOffset * (int)linesPerUnit, 0, Math.max(0, this.lines.size() - this.visibleLines));
                if (this.isFocused && this.cursorY >= visibleScrollBarWidth && this.cursorY < visibleScrollBarWidth + this.visibleLines && this.cursorTick / 10 % 2 == 0) {
                    String subString = this.getActiveLine().substring(0, this.cursorX);
                    scrollBarWidth = this.getWidth() - this.padding * 2;
                    float pixelsPerUnit = (float)this.maxLineWidth / (float)(this.getWidth() - this.padding * 2);
                    stringWidth = RenderUtil.getTextWidth(subString);
                    double posX = this.getXPos() + (double)this.padding + (double)stringWidth - (double)Mth.clamp(this.horizontalScroll + (int)((float)this.horizontalOffset * pixelsPerUnit), 0, Math.max(0, this.maxLineWidth - scrollBarWidth));
                    double var32 = this.getYPos() + (double)this.padding;
                    int var10001 = this.cursorY - visibleScrollBarWidth;
                    double posY = var32 + (double)(var10001 * 9);
                    double var33 = posY - 1.0D;
                    RenderUtil.drawRectWithColour(stack,posX, var33, 1, 9, Color.WHITE);
                }
            }

            GLHelper.popScissor();
            if (this.scrollBarVisible) {
                double scrollX;
                if (this.lines.size() > this.visibleLines) {
                    visibleWidth = this.getHeight() - 4;
                    visibleScrollBarWidth = Math.max(20, (int)((float)this.visibleLines / (float)this.lines.size() * (float)visibleWidth));
                    scrollPercentage = Mth.clamp((float)(this.verticalScroll + this.verticalOffset) / (float)(this.lines.size() - this.visibleLines), 0.0F, 1.0F);
                    double scrollBarY = (float)(visibleWidth - visibleScrollBarWidth) * scrollPercentage;
                    scrollX = this.getYPos() + 2.0D + scrollBarY;
                    RenderUtil.drawRectWithColour(stack,this.getXPos() + (double)this.getWidth() - 2.0D - (double)this.scrollBarSize, scrollX, 2, visibleScrollBarWidth, this.placeholderColour);
                }

                if (!this.wrapText && this.maxLineWidth >= this.getWidth() - this.padding * 2) {
                    visibleWidth = this.getWidth() - this.padding * 2;
                    visibleScrollBarWidth = this.getWidth() - 4 - (this.lines.size() > this.visibleLines ? this.scrollBarSize + 1 : 0);
                    scrollPercentage = (float)(this.horizontalScroll + 1) / (float)(this.maxLineWidth - visibleWidth + 1);
                    scrollBarWidth = Math.max(20, (int)((float)visibleWidth / (float)this.maxLineWidth * (float)visibleScrollBarWidth));
                    relativeScrollX = (int)(scrollPercentage * (float)(visibleScrollBarWidth - scrollBarWidth));
                    scrollX = this.getXPos() + 2.0D + (double) Mth.clamp(relativeScrollX + this.horizontalOffset, 0, visibleScrollBarWidth - scrollBarWidth);
                    RenderUtil.drawRectWithColour(stack,scrollX, this.getYPos() + (double)this.getHeight() - (double)this.scrollBarSize - 2.0D, scrollBarWidth, 2, this.placeholderColour);
                }
            }
        }

    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        if (this.isFocused && lostFocus) {
            this.isFocused = false;
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        if (this.isVisible() && this.isEnabled()) {
            this.isFocused = true;
            TextArea.ScrollBar scrollBar = this.isMouseInsideScrollBar(mouseX, mouseY);
            if (scrollBar != null) {
                this.scrollBar = scrollBar;
                switch(scrollBar) {
                    case HORIZONTAL:
                        this.clickedX = (int)mouseX;
                        break;
                    case VERTICAL:
                        this.clickedY = (int)mouseY;
                }

            } else if (this.editable) {
                if (RenderUtil.isMouseWithin(mouseX, mouseY, this.getXPos() + (double)this.padding, this.getYPos() + (double)this.padding, this.getWidth() - this.padding * 2, this.getHeight() - this.padding * 2)) {
                    int lineX = (int)mouseX - (int)this.getXPos() - this.padding + this.horizontalScroll;
                    int var10000 = (int)mouseY - (int)this.getYPos() - this.padding;
                    int lineY = var10000 / 9 + this.verticalScroll;
                    if (lineY >= this.lines.size()) {
                        this.cursorX = this.lines.get(Math.max(0, this.lines.size() - 1)).length();
                        this.cursorY = this.lines.size() - 1;
                    } else {
                        this.cursorX = this.getClosestLineIndex(lineX, Mth.clamp(lineY, 0, this.lines.size() - 1));
                        this.cursorY = lineY;
                    }

                    this.cursorTick = 0;
                    this.updateScroll();
                }

            }
        }
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        if (this.scrollBar != null) {
            switch(this.scrollBar) {
                case HORIZONTAL:
                    this.horizontalOffset = (int)mouseX - this.clickedX;
                    break;
                case VERTICAL:
                    int visibleScrollBarHeight = this.getHeight() - 4;
                    int scrollBarHeight = Math.max(20, (int)((float)this.visibleLines / (float)this.lines.size() * (float)visibleScrollBarHeight));
                    float spacing = (float)(visibleScrollBarHeight - scrollBarHeight) / (float)(this.lines.size() - this.visibleLines);
                    this.verticalOffset = (int)((mouseY - (double)this.clickedY) / (double)spacing);
            }
        }

    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (this.scrollBar != null) {
            float scrollPercentage;
            switch(this.scrollBar) {
                case HORIZONTAL:
                    scrollPercentage = (float)this.maxLineWidth / (float)(this.getWidth() - this.padding * 2);
                    this.horizontalScroll = Mth.clamp(this.horizontalScroll + (int)((float)this.horizontalOffset * scrollPercentage), 0, this.maxLineWidth - (this.getWidth() - this.padding * 2));
                    break;
                case VERTICAL:
                    scrollPercentage = Mth.clamp((float)(this.verticalScroll + this.verticalOffset) / (float)(this.lines.size() - this.visibleLines), 0.0F, 1.0F);
                    this.verticalScroll = (int)((float)(this.lines.size() - this.visibleLines) * scrollPercentage);
            }

            this.horizontalOffset = 0;
            this.verticalOffset = 0;
            this.scrollBar = null;
        }

    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isVisible() && this.isEnabled() && this.isFocused && this.editable) {
            if (Screen.isPaste(keyCode)) {
                String clip = SharedConstants.filterText(ChatFormatting.stripFormatting(Minecraft.getInstance().keyboardHandler.getClipboard()));
                String[] lines = clip.split("\n");

                for(int i = 0; i < lines.length - 1; ++i) {
                    this.writeText(lines[i] + "\n");
                }

                this.writeText(lines[lines.length - 1]);
            } else {
                switch(keyCode) {
                    case 257:
                        this.performReturn();
                        break;
                    case 258:
                        this.writeText('\t');
                        break;
                    case 259:
                        this.performBackspace();
                    case 260:
                    case 261:
                    default:
                        break;
                    case 262:
                        this.moveCursorRight(1);
                        break;
                    case 263:
                        this.moveCursorLeft(1);
                        break;
                    case 264:
                        this.moveCursorDown();
                        break;
                    case 265:
                        this.moveCursorUp();
                }
            }

            if (this.keyListener != null) {
                this.keyListener.onKeyTyped('0', keyCode);
            }

            this.updateScroll();
        }
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
        if (this.isVisible() && this.isEnabled() && this.isFocused && this.editable) {
            if (SharedConstants.isAllowedChatCharacter(character)) {
                this.writeText(character);
            }

            if (this.keyListener != null) {
                this.keyListener.onKeyTyped(character, character);
            }

            this.updateScroll();
        }
    }

    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
        if (RenderUtil.isMouseWithin(mouseX, mouseY, this.getXPos(), this.getYPos(), this.getWidth(), this.getHeight())) {
            if (delta < 0.0D) {
                this.scroll(1);
            } else if (delta > 0.0D) {
                this.scroll(-1);
            }
        }

    }

    private TextArea.ScrollBar isMouseInsideScrollBar(double mouseX, double mouseY) {
        if (!this.scrollBarVisible) {
            return null;
        } else {
            int visibleWidth;
            int scrollBarWidth;
            if (this.lines.size() > this.visibleLines) {
                visibleWidth = this.getHeight() - 4;
                float scrollPercentage = (float)this.verticalScroll / (float)(this.lines.size() - this.visibleLines);
                int scrollBarHeight = Math.max(20, (int)((float)this.visibleLines / (float)this.lines.size() * (float)visibleWidth));
                scrollBarWidth = (int)(scrollPercentage * (float)(visibleWidth - scrollBarHeight));
                double posX = this.getXPos() + (double)this.getWidth() - 2.0D - (double)this.scrollBarSize;
                double posY = this.getYPos() + 2.0D + (double)Mth.clamp(scrollBarWidth + this.verticalOffset, 0, visibleWidth - scrollBarHeight);
                if (RenderUtil.isMouseInside(mouseX, mouseY, posX, posY, posX + (double)this.scrollBarSize, posY + (double)scrollBarHeight)) {
                    return TextArea.ScrollBar.VERTICAL;
                }
            }

            if (!this.wrapText && this.maxLineWidth >= this.getWidth() - this.padding * 2) {
                visibleWidth = this.getWidth() - this.padding * 2;
                int visibleScrollBarWidth = this.getWidth() - 4 - (this.lines.size() > this.visibleLines ? this.scrollBarSize + 1 : 0);
                float scrollPercentage = (float)this.horizontalScroll / (float)(this.maxLineWidth - visibleWidth + 1);
                scrollBarWidth = Math.max(20, (int)((float)visibleWidth / (float)this.maxLineWidth * (float)visibleScrollBarWidth));
                int relativeScrollX = (int)(scrollPercentage * (float)(visibleScrollBarWidth - scrollBarWidth));
                double posX = this.getXPos() + 2.0D + (double)Mth.clamp(relativeScrollX, 0, visibleScrollBarWidth - scrollBarWidth);
                double posY = this.getYPos() + (double)this.getHeight() - 2.0D - (double)this.scrollBarSize;
                if (RenderUtil.isMouseInside(mouseX, mouseY, posX, posY, posX + (double)scrollBarWidth, posY + (double)this.scrollBarSize)) {
                    return TextArea.ScrollBar.HORIZONTAL;
                }
            }

            return null;
        }
    }

    private String getActiveLine() {
        return this.lines.get(this.cursorY);
    }

    public void performBackspace() {
        if (this.cursorY != 0 || this.cursorX != 0) {
            this.removeCharAtCursor();
            if (this.wrapText && this.cursorY + 1 < this.lines.size()) {
                String activeLine = this.getActiveLine();
                if (activeLine.contains("\n")) {
                    return;
                }

                String result = activeLine + this.lines.remove(this.cursorY + 1);
                if (RenderUtil.getTextWidth(result) > this.getWidth() - this.padding * 2) {
                    String trimmed = RenderUtil.clipTextToWidth(result, this.getWidth() - this.padding * 2);
                    this.lines.set(this.cursorY, trimmed);
                    if (trimmed.charAt(trimmed.length() - 1) != '\n') {
                        this.prependToLine(this.cursorY + 1, result.substring(trimmed.length()));
                    } else if (this.cursorY + 1 < this.lines.size()) {
                        this.lines.add(this.cursorY + 1, trimmed);
                    } else {
                        this.lines.add(trimmed);
                    }
                } else {
                    this.lines.set(this.cursorY, result);
                }
            }

            this.recalculateMaxWidth();
        }
    }

    public void performReturn() {
        if (this.maxLines <= 0 || this.getNewLineCount() != this.maxLines - 1) {
            int lineIndex = this.cursorY;
            String activeLine = this.getActiveLine();
            if (this.cursorX == activeLine.length()) {
                this.lines.set(lineIndex, activeLine + "\n");
                if (!this.wrapText || lineIndex + 1 == this.lines.size()) {
                    this.lines.add(lineIndex + 1, "");
                }
            } else {
                this.lines.set(lineIndex, activeLine.substring(0, this.cursorX) + "\n");
                this.lines.add(lineIndex + 1, activeLine.substring(this.cursorX));
            }

            if (this.cursorY + 1 >= this.verticalScroll + this.visibleLines) {
                this.scroll(1);
            }

            this.moveCursorRight(1);
            this.recalculateMaxWidth();
        }
    }

    private int getNewLineCount() {
        int count = 0;

        for(int i = 0; i < this.lines.size() - 1; ++i) {
            if (this.lines.get(i).endsWith("\n")) {
                ++count;
            }
        }

        return count;
    }

    private void removeCharAtCursor() {
        String activeLine = this.getActiveLine();
        String previousLine;
        if (this.cursorX > 0) {
            previousLine = activeLine.substring(0, this.cursorX - 1);
            String tail = activeLine.substring(this.cursorX);
            this.lines.set(this.cursorY, previousLine + tail);
            this.moveCursorLeft(1);
        } else {
            if (this.wrapText) {
                if (activeLine.isEmpty()) {
                    this.lines.remove(this.cursorY);
                }

                previousLine = this.lines.get(this.cursorY - 1);
                this.lines.set(this.cursorY - 1, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)));
                this.moveCursorLeft(1);
            } else {
                previousLine = this.lines.get(this.cursorY - 1);
                this.moveCursorLeft(1);
                if (!activeLine.isEmpty()) {
                    this.lines.set(this.cursorY, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)) + activeLine);
                } else {
                    this.lines.set(this.cursorY, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)));
                }

                this.lines.remove(this.cursorY + 1);
            }

            if (this.verticalScroll > 0) {
                this.scroll(-1);
            }

            this.recalculateMaxWidth();
        }
    }

    public void writeText(char c) {
        int prevCursorY = this.cursorY;
        this.writeText(Character.toString(c));
        if (this.wrapText && prevCursorY != this.cursorY) {
            this.moveCursorRight(1);
        }

    }

    public void writeText(String text) {
        text = text.replace("\r", "");
        String activeLine = this.getActiveLine();
        String head = activeLine.substring(0, this.cursorX);
        String tail = activeLine.substring(this.cursorX);
        if (this.wrapText) {
            String result;
            String trimmed;
            if (text.endsWith("\n")) {
                result = head + text;
                if (RenderUtil.getTextWidth(result) > this.getWidth() - this.padding * 2) {
                    trimmed = RenderUtil.clipTextToWidth(result, this.getWidth() - this.padding * 2);
                    this.lines.set(this.cursorY, trimmed);
                    this.prependToLine(this.cursorY + 1, result.substring(trimmed.length()));
                } else {
                    this.lines.set(this.cursorY, result);
                }

                this.prependToLine(this.cursorY + 1, tail);
            } else {
                result = head + text + tail;
                if (RenderUtil.getTextWidth(result) > this.getWidth() - this.padding * 2) {
                    trimmed = RenderUtil.clipTextToWidth(result, this.getWidth() - this.padding * 2);
                    this.lines.set(this.cursorY, trimmed);
                    this.prependToLine(this.cursorY + 1, result.substring(trimmed.length()));
                } else {
                    this.lines.set(this.cursorY, result);
                }
            }
        } else if (text.endsWith("\n")) {
            this.lines.set(this.cursorY, head + text);
            this.prependToLine(this.cursorY + 1, tail);
        } else {
            this.lines.set(this.cursorY, head + text + tail);
        }

        this.moveCursorRight(text.length());
        this.recalculateMaxWidth();
    }

    private void prependToLine(int lineIndex, String text) {
        if (lineIndex == this.lines.size()) {
            this.lines.add("");
        }

        if (text.length() > 0) {
            if (lineIndex < this.lines.size()) {
                if (text.charAt(Math.max(0, text.length() - 1)) == '\n') {
                    this.lines.add(lineIndex, text);
                    return;
                }

                String result = text + this.lines.get(lineIndex);
                if (RenderUtil.getTextWidth(result) > this.getWidth() - this.padding * 2) {
                    String trimmed = RenderUtil.clipTextToWidth(result, this.getWidth() - this.padding * 2);
                    this.lines.set(lineIndex, trimmed);
                    this.prependToLine(lineIndex + 1, result.substring(trimmed.length()));
                } else {
                    this.lines.set(lineIndex, result);
                }
            }

        }
    }

    public void moveCursorRight(int amount) {
        if (amount > 0) {
            String activeLine = this.getActiveLine();
            if ((this.cursorY != this.lines.size() - 1 || this.cursorX != activeLine.length()) && (this.cursorX <= 0 || activeLine.charAt(this.cursorX - 1) != '\n')) {
                this.cursorTick = 0;
                if (this.cursorX < activeLine.length() && activeLine.charAt(this.cursorX) != '\n') {
                    ++this.cursorX;
                } else if (this.cursorY + 1 < this.lines.size()) {
                    this.cursorX = 0;
                    if (this.cursorY >= this.verticalScroll + this.visibleLines - 1) {
                        this.scroll(1);
                    }

                    this.moveYCursor(1);
                }

                this.moveCursorRight(amount - 1);
            }
        }
    }

    public void moveCursorLeft(int amount) {
        if (amount > 0) {
            if (this.cursorX != 0 || this.cursorY != 0) {
                this.cursorTick = 0;
                if (this.cursorX > 0) {
                    --this.cursorX;
                } else {
                    this.cursorX = this.lines.get(this.cursorY - 1).length();
                    if (this.cursorX > 0 && this.lines.get(this.cursorY - 1).charAt(this.cursorX - 1) == '\n') {
                        --this.cursorX;
                    }

                    if (this.cursorY - 1 < this.verticalScroll) {
                        this.scroll(-1);
                    }

                    this.moveYCursor(-1);
                }

                this.moveCursorLeft(amount - 1);
            }
        }
    }

    private void moveCursorUp() {
        if (this.cursorY != 0) {
            this.cursorTick = 0;
            String previousLine = this.lines.get(this.cursorY - 1);
            if (this.cursorX >= previousLine.length()) {
                this.cursorX = previousLine.length();
                if (previousLine.contains("\n")) {
                    --this.cursorX;
                }
            }

            if (this.cursorY - 1 < this.verticalScroll) {
                this.scroll(-1);
            }

            this.moveYCursor(-1);
        }
    }

    private void moveCursorDown() {
        if (this.cursorY != this.lines.size() - 1) {
            this.cursorTick = 0;
            String nextLine = this.lines.get(this.cursorY + 1);
            if (this.cursorX > nextLine.length()) {
                this.cursorX = nextLine.length();
                if (nextLine.endsWith("\n")) {
                    --this.cursorX;
                }
            }

            if (this.cursorY + 1 >= this.verticalScroll + this.visibleLines) {
                this.scroll(1);
            }

            this.moveYCursor(1);
        }
    }

    private void moveYCursor(int amount) {
        this.cursorY += amount;
        if (this.cursorY < 0) {
            this.cursorY = 0;
            this.cursorX = 0;
        }

        if (this.cursorY >= this.lines.size()) {
            this.cursorX = this.lines.get(this.lines.size() - 1).length();
            this.cursorY = this.lines.size() - 1;
        }

    }

    private void scroll(int amount) {
        this.verticalScroll += amount;
        if (this.verticalScroll < 0) {
            this.verticalScroll = 0;
        } else if (this.verticalScroll > this.lines.size() - this.visibleLines) {
            this.verticalScroll = Math.max(0, this.lines.size() - this.visibleLines);
        }

    }

    private void updateText() {
        List<String> updatedLines = new ArrayList<>();
        int totalLength;
        int lineIndex;
        if (this.wrapText) {
            for(totalLength = 0; totalLength < this.lines.size() - 1; ++totalLength) {
                String line = this.lines.get(totalLength);
                if (line.equals("\n")) {
                    updatedLines.add(line);
                } else {
                    List<FormattedText> split = this.font.getSplitter().splitLines(new TextComponent(this.lines.get(totalLength)), this.getWidth() - this.padding * 2, Style.EMPTY);

                    for(int j = 0; j < split.size() - 1; ++j) {
                        updatedLines.add(split.get(j).getString());
                    }

                    if (split.size() > 0) {
                        updatedLines.add(split.get(split.size() - 1) + "\n");
                    }
                }
            }

            List<FormattedText> split = this.font.getSplitter().splitLines(new TextComponent(this.lines.get(this.lines.size() - 1)), this.getWidth() - this.padding * 2, Style.EMPTY);

            for(lineIndex = 0; lineIndex < split.size() - 1; ++lineIndex) {
                updatedLines.add(split.get(lineIndex).getString());
            }

            if (split.size() > 0) {
                updatedLines.add(split.get(split.size() - 1).getString());
            }

            List<FormattedText> activeLine = this.font.getSplitter().splitLines(new TextComponent(this.lines.get(this.cursorY)), this.getWidth() - this.padding * 2, Style.EMPTY);

            for (FormattedText line : activeLine) {
                if (totalLength + line.getString().length() >= this.cursorX) {
                    this.cursorX -= totalLength;
                    break;
                }

                totalLength += line.getString().length();
                ++this.cursorY;
            }
        } else {
            totalLength = 0;
            lineIndex = 0;
            StringBuilder builder = new StringBuilder();

            do {
                String line = this.lines.get(lineIndex);
                if (totalLength > 0) {
                    builder.append(" ");
                }

                builder.append(line);
                if (lineIndex == this.cursorY) {
                    this.cursorX += totalLength;
                    this.cursorY = updatedLines.size();
                } else {
                    totalLength += line.length();
                }

                if (!line.endsWith("\n")) {
                    if (lineIndex == this.lines.size() - 1) {
                        updatedLines.add(builder.toString());
                        break;
                    }
                } else {
                    updatedLines.add(builder.toString());
                    builder.setLength(0);
                    totalLength = 0;
                }

                ++lineIndex;
            } while(lineIndex < this.lines.size());
        }

        this.lines = updatedLines;
        this.recalculateMaxWidth();
    }

    private void updateScroll() {
        if (!this.wrapText) {
            int visibleWidth = this.getWidth() - this.padding * 2;
            int textWidth = RenderUtil.getTextWidth(this.lines.get(this.cursorY).substring(0, this.cursorX));
            if (textWidth < this.horizontalScroll) {
                this.horizontalScroll = Math.max(0, textWidth - 1);
            } else if (textWidth > this.horizontalScroll + visibleWidth) {
                this.horizontalScroll = Math.max(0, textWidth - visibleWidth + 1);
            } else if (this.cursorX == 0) {
                this.horizontalScroll = 0;
            }
        }

        if (this.cursorY < this.verticalScroll) {
            this.verticalScroll = Math.min(Math.max(0, this.cursorY - 1), Math.max(0, this.lines.size() - this.visibleLines));
        } else if (this.cursorY >= this.verticalScroll + this.visibleLines) {
            this.verticalScroll = Math.max(0, Math.min(this.cursorY + 1 - (this.visibleLines - 1), this.lines.size() - this.visibleLines));
        }

    }

    private void recalculateMaxWidth() {
        int maxWidth = 0;

        for (String line : this.lines) {
            if (RenderUtil.getTextWidth(line) > maxWidth) {
                maxWidth = RenderUtil.getTextWidth(line);
            }
        }

        this.maxLineWidth = maxWidth;
    }

    private int getClosestLineIndex(int lineX, int lineY) {
        String line = this.lines.get(lineY);
        int clickedCharX = RenderUtil.clipTextToWidth(line, lineX).length();
        int nextCharX = Mth.clamp(clickedCharX + 1, 0, Math.max(0, line.length()));
        int clickedCharWidth = RenderUtil.getTextWidth(line.substring(0, clickedCharX));
        int nextCharWidth = RenderUtil.getTextWidth(line.substring(0, nextCharX));
        int clickedDistanceX = Math.abs(clickedCharWidth - lineX);
        int nextDistanceX = Math.abs(nextCharWidth - lineX - 1);
        int charX;
        if (Math.min(clickedDistanceX, nextDistanceX) == clickedDistanceX) {
            charX = clickedCharX;
        } else {
            charX = nextCharX;
        }

        if (charX > 0 && this.lines.get(lineY).charAt(charX - 1) == '\n') {
            --charX;
        }

        return charX;
    }

    public void clear() {
        this.cursorX = 0;
        this.cursorY = 0;
        this.lines.clear();
        this.lines.add("");
    }

    public void setText(String text) {
        this.lines.clear();
        String[] splitText = text.replace("\r", "").split("\n");

        for(int i = 0; i < splitText.length - 1; ++i) {
            this.lines.add(splitText[i] + "\n");
        }

        this.lines.add(splitText[splitText.length - 1]);
        this.cursorX = splitText[splitText.length - 1].length();
        this.cursorY = splitText.length - 1;
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < this.lines.size() - 1; ++i) {
            builder.append(this.lines.get(i));
        }

        builder.append(this.lines.get(this.lines.size() - 1));
        return builder.toString();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
        this.horizontalScroll = 0;
        this.updateText();
    }

    public void setScrollBarVisible(boolean scrollBarVisible) {
        this.scrollBarVisible = scrollBarVisible;
    }

    public void setScrollBarSize(int scrollBarSize) {
        this.scrollBarSize = Math.max(0, scrollBarSize);
    }

    public void setHighlight(IHighlight highlight) {
        this.highlight = highlight;
    }

    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        this.visibleLines = (int)Math.floor((this.getHeight() - padding * 2) / 9);
    }

    public void setTextColour(Color colour) {
        this.textColour = colour;
    }

    public void setBackgroundColour(Color colour) {
        this.backgroundColour = colour;
    }

    public void setBorderColour(Color colour) {
        this.borderColour = colour;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setMaxLines(int maxLines) {
        if (maxLines < 0) {
            maxLines = 0;
        }

        this.maxLines = maxLines;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public void showHiddenCharacters(boolean showHiddenCharacters) {
    }

    private enum ScrollBar {
        HORIZONTAL,
        VERTICAL
    }

}
