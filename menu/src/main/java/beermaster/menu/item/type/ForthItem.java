package beermaster.menu.item.type;

import beermaster.context.map.ContextKey;
import beermaster.menu.Menu;
import beermaster.menu.item.Item;
import beermaster.menu.item.ItemHandler;

import java.util.Objects;

public record ForthItem(int position, String title, Menu nextMenu) implements Item {

    public ForthItem(String title, Menu nextMenu) {
        this(-1, title, nextMenu);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ItemHandler getItemHandler() {
        return context -> context.put(ContextKey.NEXT_MENU, nextMenu);
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public Item copyWithPosition(int position) {
        return new ForthItem(position, title, nextMenu);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForthItem forthItem = (ForthItem) o;
        return position == forthItem.position && Objects.equals(title, forthItem.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, title);
    }

    @Override
    public String toString() {
        return "ForthItem{" +
                "position=" + position +
                ", title='" + title + '\'' +
                '}';
    }
}
