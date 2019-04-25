package net.minecraft.client.gui.element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface ITab {
    void focus();

    default List<GuiButton> getButtons(){
        return new ArrayList<>(0);
    }

    default void add(GuiButton... buttons){}

    GuiButton getButton();

    default void addTo(List<GuiButton> buttonList){}

    default void unfocus(){}
}
