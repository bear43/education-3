package beermaster.menu.item.type;

import beermaster.menu.item.Item;
import beermaster.menu.item.ItemHandler;

import java.util.Objects;

public record FunctionalItem(int position, String title, ItemHandler itemHandler) implements Item {
    public FunctionalItem {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title can not be empty");
        }
        if (itemHandler == null) {
            throw new IllegalArgumentException("handler can not be null");
        }
    }

    public FunctionalItem(String title, ItemHandler itemHandler) {
        this(-1, title, itemHandler);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public Item copyWithPosition(int position) {
        return new FunctionalItem(position, title, itemHandler);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionalItem that = (FunctionalItem) o;
        return position == that.position && Objects.equals(title, that.title) && Objects.equals(itemHandler, that.itemHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, title, itemHandler);
    }
}
