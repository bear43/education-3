package beermaster;

import beermaster.client.StubClientBuilder;
import beermaster.config.Configuration;
import beermaster.context.ApplicationContext;
import beermaster.io.console.ConsoleInput;
import beermaster.io.console.UserChoiceProvider;
import beermaster.menu.Menu;
import beermaster.menu.MenuBuilder;
import beermaster.menu.MenuSwitcher;
import beermaster.menu.render.MenuRenderer;
import beermaster.server.ServerWrapper;

import java.io.IOException;

public class ClientEntry {
    public static void main(String[] args) throws IOException {
        Configuration configuration = Configuration.getInstance();
        ApplicationContext applicationContext = ApplicationContext.getInstance();
        ConsoleInput input = new ConsoleInput();
        MenuBuilder menuBuilder = new MenuBuilder(applicationContext, input, configuration, new StubClientBuilder());
        Menu mainMenu = menuBuilder.build();
        MenuSwitcher menuSwitcher = new MenuSwitcher(mainMenu, new MenuRenderer(), new UserChoiceProvider(input));
        while (applicationContext.isRunning()) {
            try {
                menuSwitcher.doSwitch();
            } catch (Exception ex) {
                ex.printStackTrace();
                ServerWrapper.getInstance().close();
                ApplicationContext.getInstance().shutdown();
            }
        }
    }
}