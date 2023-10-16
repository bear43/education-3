package beermaster;

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

public class ServerEntry {

    public static void main(String[] args) {
        new Application().run();
    }
}