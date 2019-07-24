package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
    protected final Minecraft mc;
    protected final List<ResourcePackListEntry> resourcePackList;

    public GuiResourcePackList(Minecraft mcIn, int widthIn, int heightIn, List<ResourcePackListEntry> resourcePackList) {
        super(mcIn, widthIn, heightIn, 32, heightIn - 51, 36);
        mc = mcIn;
        this.resourcePackList = resourcePackList;
        setHasListHeader(true, (int)((float)mcIn.fontRenderer.getFontHeight() * 1.5F));
    }

    @Override
    protected void drawListHeader(int x, int y, Tessellator tessellator) {
        String s = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + getListHeader();
        mc.fontRenderer.drawString(s, x + (width >> 1) - (mc.fontRenderer.getStringWidth(s) >> 1), Math.min(top + 3, y), 16777215);
    }

    protected abstract String getListHeader();


    public List<ResourcePackListEntry> getList() {
        return this.resourcePackList;
    }

    @Override
    protected int getSize() {
        return this.getList().size();
    }

    @Override
    public ResourcePackListEntry getListEntry(int index) {
        return this.getList().get(index);
    }

    @Override
    public int getListWidth() {
        return this.width;
    }

    @Override
    protected int getScrollBarX() {
        return this.right - 6;
    }
}
