package beermaster.menu.item.type;

import beermaster.context.ApplicationContext;
import beermaster.context.map.MapContext;
import beermaster.menu.item.Item;
import beermaster.menu.item.ItemHandler;
import beermaster.menu.item.handler.AbstractReturnItemHandler;

public class ReturnItemImpl extends ReturnItem {

    private final ApplicationContext applicationContext;

    public ReturnItemImpl(int position, String title, ApplicationContext applicationContext) {
        super(position, title);
        this.applicationContext = applicationContext;
    }

    public ReturnItemImpl(String title, ApplicationContext applicationContext) {
        super(title);
        this.applicationContext = applicationContext;
    }

    @Override
    public ItemHandler getItemHandler() {
        return new AbstractReturnItemHandler() {
            @Override
            protected void onExit(MapContext context) {
                applicationContext.shutdown();
            }
        };
    }

    @Override
    public Item copyWithPosition(int position) {
        return new ReturnItemImpl(position, title, applicationContext);
    }
}
