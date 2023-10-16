package beermaster.menu;

import beermaster.menu.item.Item;
import beermaster.menu.item.type.ForthItem;
import beermaster.menu.item.type.ReturnItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ItemInserter {
    private final List<Item> items;
    private final Function<String, ReturnItem> returnItemCtor;

    public ItemInserter(Function<String, ReturnItem> returnItemCtor) {
        this.items = new ArrayList<>();
        this.returnItemCtor = returnItemCtor;
    }


    public ItemInserter add(Item item) {
        items.add(item);
        return this;
    }

    public ItemInserter clear() {
        items.clear();
        return this;
    }

    /**
     * Adds all items that have been already placed in an {@link ItemInserter} instance.
     * Automatically adds additional {@link ReturnItem} item (Back/Exit) to the menu.
     * @param menu The target menu the items should be inserted to.
     */
    public ItemInserter insert(Menu menu) {
        for (Item item : items) {
            if (item instanceof ForthItem forthItem) {
                menu.addForthItem(forthItem);
            } else {
                menu.addItem(item);
            }
        }
        if (!menu.hasReturnItem()) {
            menu.addReturnItem(returnItemCtor);
        }
        return this;
    }
}
