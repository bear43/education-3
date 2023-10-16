package beermaster.menu;

import beermaster.context.ApplicationContext;
import beermaster.context.map.ContextKey;
import beermaster.entry.Entry;
import beermaster.entry.EntryFactory;
import beermaster.entry.EntryManager;
import beermaster.io.console.ConsoleInput;
import beermaster.menu.item.type.ReturnItemImpl;
import beermaster.menu.item.type.ForthItem;
import beermaster.menu.item.type.FunctionalItem;
import beermaster.server.Server;
import beermaster.server.ServerBuilder;
import beermaster.server.ServerSettings;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class MenuBuilder {
    private static final Pattern ENTRY_NAME_PATTERN = Pattern.compile("^(/[a-zA-Z]+)+$");

    private record EntryData(String entryName, int version, Map<String, String> data) {}
    private final EntryManager entryManager;
    private final EntryFactory entryFactory;
    private final ConsoleInput input;
    private final ItemInserter itemInserter;
    private final ServerBuilder serverBuilder;

    public MenuBuilder(EntryManager entryManager, EntryFactory entryFactory, ConsoleInput input, ApplicationContext applicationContext, ServerBuilder serverBuilder) {
        this.entryManager = entryManager;
        this.entryFactory = entryFactory;
        this.input = input;
        this.itemInserter = new ItemInserter(title -> new ReturnItemImpl(title, applicationContext));
        this.serverBuilder = serverBuilder;
    }

    public Menu build() {
        Menu mainMenu = new Menu("Main menu", true);
        Menu entryMenu = new Menu(mainMenu, "Entries");
        Menu serverMenu = new Menu(mainMenu, "Server");
        Menu settingsMenu = new Menu(serverMenu, "Settings");
        Menu entryDataBuilder = new Menu(entryMenu, "Entry actions");


        setupMenuItems(mainMenu, entryMenu, serverMenu);
        setupEntriesItems(entryMenu, entryDataBuilder);


        setupServerItems(serverMenu, settingsMenu);

        return mainMenu;
    }

    private void setupServerItems(Menu serverMenu, Menu settingsMenu) {
        setupSettingsItems(settingsMenu);

        itemInserter
                .add(new ForthItem("Settings", settingsMenu))
                .add(new FunctionalItem("Start", context -> {
                    System.out.println("Warning: you could not return back from the server mode. Let's go? (Y/N)");
                    String answer = input.read();
                    if ("Y".equals(answer)) {
                        context.get(ContextKey.ADDITIONAL_CONTEXT)
                                .map(ServerSettings.class::cast)
                                .ifPresent(this::startServer);
                    } else if (!"N".equals(answer)) {
                        System.err.println("Answer only with Y or N");
                    }
                }))
                .insert(serverMenu)
                .clear();
    }

    private void setupSettingsItems(Menu settingsMenu) {
        itemInserter
                .clear()
                .add(new FunctionalItem("Show settings", context -> {
                    System.out.println("Current settings:");
                    context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(ServerSettings.class::cast)
                            .ifPresent(System.out::println);
                }))
                .add(new FunctionalItem("Set name", context -> {
                    System.out.print("Enter name: ");
                    String name = input.read();
                    context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(ServerSettings.class::cast)
                            .map(settings -> new ServerSettings(name, settings.port(), settings.fileStorageRoot(), settings.dataFilename(), settings.infoFilename()))
                            .ifPresent(settings -> context.put(ContextKey.ADDITIONAL_CONTEXT, settings));
                }))
                .add(new FunctionalItem("Set port", context -> {
                    System.out.print("Enter port: ");
                    String portStr = input.read();
                    int port;
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (Exception ex) {
                        System.err.println("Error occurred: " + ex.getMessage());
                        return;
                    }
                    context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(ServerSettings.class::cast)
                            .map(settings -> new ServerSettings(settings.name(), port, settings.fileStorageRoot(), settings.dataFilename(), settings.infoFilename()))
                            .ifPresent(settings -> context.put(ContextKey.ADDITIONAL_CONTEXT, settings));
                }))
                .insert(settingsMenu)
                .clear();
    }

    private void startServer(ServerSettings settings) {
        serverBuilder.setSettings(settings);
        Server server = serverBuilder.build();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupEntriesItems(Menu entryMenu, Menu entryActions) {
        itemInserter
                .add(new FunctionalItem("Show all", context -> {
                    Collection<Entry> entries = entryManager.findAll();
                    System.out.println("Found next entries:");
                    System.out.println(entries);
                }))
                .add(new FunctionalItem("Create", context -> getEntryName().ifPresent(entryName -> {
                    context.put(ContextKey.NEXT_MENU, entryActions);
                    context.put(ContextKey.ADDITIONAL_CONTEXT, new EntryData(entryName, 1, new HashMap<>()));
                })))
                .add(new FunctionalItem("Edit", context -> getEntryName().map(entryManager::read)
                        .ifPresent(entry -> {
                            context.put(ContextKey.NEXT_MENU, entryActions);
                            context.put(ContextKey.ADDITIONAL_CONTEXT,
                                    new EntryData(entry.getName(), entry.getVersion() + 1, entry.getData()));
                        })))
                .add(new FunctionalItem("Delete", context -> getEntryName().ifPresent(entryName -> {
                    entryManager.delete(entryName);
                    context.put(ContextKey.NEXT_MENU, entryMenu);
                })))
                .insert(entryMenu)
                .clear();


        setupEntryActions(entryMenu, entryActions);
    }

    private Optional<String> getEntryName() {
        System.out.print("Enter name of the entry: ");
        String entryName = input.read();
        if (!ENTRY_NAME_PATTERN.matcher(entryName)
                .matches()) {
            System.err.println("Incorrect input! An entry name should comprises the delimiter \"/\" and characters a-zA-Z. This pattern can be repeated: /test or /test/probe but not test/ or test/probe");
            return Optional.empty();
        }
        return Optional.of(entryName);
    }

    private void setupEntryActions(Menu entryMenu, Menu entryActions) {
        itemInserter
                .add(new FunctionalItem("Show data", context -> {
                    EntryData entryData = context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(EntryData.class::cast)
                            .orElseThrow(() -> new IllegalStateException("Context should contain data about the entry"));
                    System.out.println("Entry name: " + entryData.entryName);
                    System.out.println("Entry data: " + entryData.data);
                }))
                .add(new FunctionalItem("Add data", context -> {
                    EntryData entryData = context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(EntryData.class::cast)
                            .orElseThrow(() -> new IllegalStateException("Context should contain data about the entry"));
                    System.out.print("Enter key: ");
                    String key = input.read();
                    if (key.isBlank()) {
                        System.err.println("Key can not be blank!");
                        return;
                    }
                    System.out.print("Enter value: ");
                    String value = input.read();
                    if ("null".equals(value)) {
                        value = null;
                    }
                    entryData.data.put(key, value);
                }))
                .add(new FunctionalItem("Delete data", context -> {
                    EntryData entryData = context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(EntryData.class::cast)
                            .orElseThrow(() -> new IllegalStateException("Context should contain data about the entry"));
                    System.out.print("Enter key: ");
                    String key = input.read();
                    if (key.isBlank()) {
                        System.err.println("Key can not be blank!");
                        return;
                    }
                    entryData.data.remove(key);
                }))
                .add(new FunctionalItem("Save", context -> {
                    EntryData entryData = context.get(ContextKey.ADDITIONAL_CONTEXT)
                            .map(EntryData.class::cast)
                            .orElseThrow(() -> new IllegalStateException("Context should contain data about the entry"));
                    try {
                        Entry entry = entryFactory.create(entryData.entryName, entryData.version, entryData.data);
                        if (entryData.version > 1) {
                            entryManager.update(entry);
                        } else {
                            entryManager.create(entry);
                        }
                    } catch (Exception ex) {
                        System.err.println("Exception occurred: " + ex.getMessage());
                        return;
                    }
                    System.out.printf("The entry %s has been saved!%n", entryData.entryName);
                    context.remove(ContextKey.ADDITIONAL_CONTEXT);
                    context.put(ContextKey.NEXT_MENU, entryMenu);
                }))
                .insert(entryActions)
                .clear();
    }

    private void setupMenuItems(Menu mainMenu, Menu entryMenu, Menu serverMenu) {
        itemInserter
                .clear()
                .add(new ForthItem("Entries", entryMenu))
                .add(new FunctionalItem("Server", context -> {
                    context.put(ContextKey.ADDITIONAL_CONTEXT, new ServerSettings());
                    context.put(ContextKey.NEXT_MENU, serverMenu);
                }))
                .insert(mainMenu)
                .clear();
    }
}
