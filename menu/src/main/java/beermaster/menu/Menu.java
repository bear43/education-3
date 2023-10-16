package beermaster.menu;

import beermaster.menu.item.Item;
import beermaster.menu.item.type.ForthItem;
import beermaster.menu.item.type.ReturnItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Menu {
    private Menu parent;
    private final String title;
    private final List<Item> items;
    private final boolean root;

    public Menu(Menu parent, String title, boolean root) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title can not be empty");
        }
        this.title = title;
        this.items = new ArrayList<>();
        this.root = root;
        this.parent = parent;
    }

    public Menu(Menu parent, String title) {
        this(parent, title, false);
    }

    public Menu(String title, boolean root) {
        this(null, title, root);
    }

    public Menu(String title) {
        this(null, title, false);
    }

    public Menu getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Menu addItem(Item item) {
        if (item.getPosition() == -1) {
            int itemsSize = items.size();
            Item positionedItem = item.copyWithPosition(itemsSize + 1);
            items.add(positionedItem);
        } else {
            items.add(item);
        }
        return this;
    }

    public Menu addForthItem(ForthItem item) {
        item.nextMenu().setParent(this);
        return addItem(item);
    }

    public Menu addReturnItem(Function<String, ReturnItem> ctor) {
        if (hasReturnItem()) {
            throw new IllegalArgumentException("Return item (or just item in 0 position) already added to this menu");
        }
        items.add(ctor.apply(root ? "Exit" : "Return"));
        return this;
    }

    public boolean hasReturnItem() {
        return items.stream().anyMatch(it -> it.getPosition() == 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Menu) obj;
        return Objects.equals(this.parent, that.parent) &&
                Objects.equals(this.title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, title);
    }

    @Override
    public String toString() {
        return "Menu[" +
                "parent=" + parent + ", " +
                "title=" + title + ", ";
    }
}
