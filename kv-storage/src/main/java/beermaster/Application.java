package beermaster;

import beermaster.menu.MenuBuilder;
import beermaster.context.ApplicationContext;
import beermaster.data.DataDeltaResolverImpl;
import beermaster.entry.EntryFactory;
import beermaster.entry.EntryManager;
import beermaster.entry.simple.SimpleEntryFactory;
import beermaster.entry.storage.FileStorage;
import beermaster.io.console.ConsoleInput;
import beermaster.io.console.UserChoiceProvider;
import beermaster.mapper.DataToMapMapper;
import beermaster.mapper.DataToRawMapper;
import beermaster.mapper.RawToDataMapper;
import beermaster.menu.Menu;
import beermaster.menu.MenuSwitcher;
import beermaster.menu.render.MenuRenderer;
import beermaster.server.ServerBuilder;
import beermaster.server.StubServerBuilder;

public class Application {
    private static final String ROOT_PATH = System.getProperty("user.home") + "/kv-storage";
    private static final String DATA_FILENAME = "datum.bin";
    private static final String INFO_FILENAME = "info.obj";
    private static final ApplicationContext APPLICATION_CONTEXT = ApplicationContext.getInstance();

    public void run() {
        ConsoleInput input = new ConsoleInput();
        DataToMapMapper dataMapper = new DataToMapMapper();
        EntryManager entryManager = new EntryManager(new FileStorage(ROOT_PATH, DATA_FILENAME, INFO_FILENAME,
                dataMapper, new DataDeltaResolverImpl(), new RawToDataMapper(), new DataToRawMapper()));
        EntryFactory entryFactory = new SimpleEntryFactory(dataMapper);
        ServerBuilder serverBuilder = new StubServerBuilder();
        MenuBuilder menuBuilder = new MenuBuilder(entryManager, entryFactory, input, APPLICATION_CONTEXT, serverBuilder);
        Menu mainMenu = menuBuilder.build();
        MenuSwitcher menuSwitcher = new MenuSwitcher(mainMenu, new MenuRenderer(), new UserChoiceProvider(input));
        while (ApplicationContext.getInstance().isRunning()) {
            menuSwitcher.doSwitch();
        }
    }
}
