package beermaster.menu.item.handler;

import beermaster.context.map.ContextKey;
import beermaster.context.map.MapContext;
import beermaster.menu.Menu;
import beermaster.menu.item.ItemHandler;

public abstract class AbstractReturnItemHandler implements ItemHandler {

    private static final String CURRENT_MENU_NULL = "CURRENT_MENU can not be null";

    protected abstract void onExit(MapContext context);

    @Override
    public void handle(MapContext context) {
        Menu currentMenu = context.<Menu>get(ContextKey.CURRENT_MENU)
                .orElseThrow(() -> new IllegalStateException(CURRENT_MENU_NULL));
        Menu parentMenu = currentMenu.getParent();
        if (parentMenu == null) {
            onExit(context);
        } else {
            context.put(ContextKey.NEXT_MENU, parentMenu);
        }
    }
}
