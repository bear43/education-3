package beermaster.menu;

import beermaster.context.map.ContextKey;
import beermaster.context.map.MapContext;
import beermaster.io.console.UserChoiceProvider;
import beermaster.menu.render.Renderer;

public class MenuSwitcher {
    private static final String INVITATION = "Enter your choice: ";
    private final Menu initialMenu;
    private final Renderer renderer;
    private final UserChoiceProvider userChoiceProvider;
    private final MapContext mapContext;

    public MenuSwitcher(Menu initialMenu, Renderer renderer,
                        UserChoiceProvider userChoiceProvider) {
        this.initialMenu = initialMenu;
        this.renderer = renderer;
        this.userChoiceProvider = userChoiceProvider;
        this.mapContext = new MapContext();
    }

    public void doSwitch() {
        Menu currentMenu = mapContext.<Menu>get(ContextKey.CURRENT_MENU)
                .orElseGet(this::initMapContextWithInitialMenu);
        renderer.render(currentMenu);
        System.out.print(INVITATION);
        int userNumber;
        try {
            userNumber = userChoiceProvider.get();
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            return;
        }
        currentMenu.getItems()
                .stream()
                .filter(item -> item.getPosition() == userNumber)
                .findFirst()
                .get()
                .getItemHandler()
                .handle(mapContext);
        mapContext.<Menu>get(ContextKey.NEXT_MENU)
                .ifPresent(nextMenu -> mapContext.put(ContextKey.CURRENT_MENU, nextMenu));
        mapContext.remove(ContextKey.NEXT_MENU);
    }

    private Menu initMapContextWithInitialMenu() {
        mapContext.put(ContextKey.CURRENT_MENU, initialMenu);
        return initialMenu;
    }
}
