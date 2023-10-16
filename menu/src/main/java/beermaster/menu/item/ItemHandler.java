package beermaster.menu.item;

import beermaster.context.map.MapContext;

/**
 * Обработчик пункта меню
 */
public interface ItemHandler {
    void handle(MapContext context);
}
