package beermaster.menu.item;

public interface Item {
    int getPosition();
    String getTitle();
    ItemHandler getItemHandler();
    Item copyWithPosition(int position);
}
