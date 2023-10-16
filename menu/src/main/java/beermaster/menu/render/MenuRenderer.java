package beermaster.menu.render;

import beermaster.menu.Menu;
import beermaster.menu.item.Item;

import java.util.List;

public final class MenuRenderer implements Renderer {

    public void render(Menu menu) {
        String title = menu.getTitle();
        List<Item> items = menu.getItems();
        System.out.printf("-----===[%s]===-----%n", title);
        for (int i = 0; i < items.size() - 1; i++) {
            printItem(i + 1, items.get(i));
        }
        Item returnOrExitItem = items.get(items.size() - 1);
        printItem(returnOrExitItem.getPosition(), returnOrExitItem);
    }

    private void printItem(int number, Item item) {
        System.out.printf("%d. %s%n", number, item.getTitle());
    }
}
