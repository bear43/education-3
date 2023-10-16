package beermaster.menu;

import beermaster.client.ClientBuilder;
import beermaster.config.Configuration;
import beermaster.context.ApplicationContext;
import beermaster.context.map.ContextKey;
import beermaster.io.console.StringInput;
import beermaster.menu.item.type.ForthItem;
import beermaster.menu.item.type.FunctionalItem;
import beermaster.menu.item.type.ReturnItemImpl;
import beermaster.server.ServerWrapper;

public class MenuBuilder {

    private static final String MAIN_MENU = "Main menu";
    private static final String CLIENT_INFO = "Client info";
    private static final String SERVER_INFO = "Server info";
    private static final String SHOW = "Show";
    private static final String SET_PORT = "Set port";
    private static final String CONFIG_STRING = "Config:%n%s%n";
    private static final String ENTER_PORT_INVITATION = "Enter new port value: ";
    private static final String PARSE_PORT_ERROR = "Exception occurred during parsing port: ";
    private static final String SET_NAME = "Set name";
    private static final String ENTER_NAME_INVITATION = "Enter new name value: ";
    private static final String NAME_CAN_NOT_BE_BLANK = "New name can not be blank. Enter at least one character";
    private static final String CONNECT = "Connect";
    private static final String NO_SERVER_NAME_RETURNED = "No server name has been provider by the server";
    private final ItemInserter itemInserter;
    private final StringInput input;
    private final Configuration configuration;
    private final ClientBuilder clientBuilder;

    public MenuBuilder(ApplicationContext applicationContext, StringInput input, Configuration configuration, ClientBuilder clientBuilder) {
        this.itemInserter = new ItemInserter(title -> new ReturnItemImpl(title, applicationContext));
        this.input = input;
        this.configuration = configuration;
        this.clientBuilder = clientBuilder;
    }

    public Menu build() {
        Menu mainMenu = new Menu(MAIN_MENU, true);
        Menu clientInfoMenu = new Menu(CLIENT_INFO);
        Menu serverInfoMenu = new Menu(SERVER_INFO);
        setupMainMenu(mainMenu, clientInfoMenu, serverInfoMenu);
        setupClientInfoMenu(clientInfoMenu);
        setupServerInfoMenu(serverInfoMenu);
        return mainMenu;
    }

    private void setupServerInfoMenu(Menu serverInfoMenu) {
        itemInserter
                .add(new FunctionalItem(SHOW, context -> System.out.printf(CONFIG_STRING, configuration.getServerInfo())))
                .add(new FunctionalItem(SET_PORT, context -> {
                    System.out.print(ENTER_PORT_INVITATION);
                    String portStr = input.read();
                    int port;
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (Exception ex) {
                        System.err.println(PARSE_PORT_ERROR + ex.getMessage());
                        return;
                    }
                    configuration.getServerInfo().setPort(port);
                }))
                .insert(serverInfoMenu)
                .clear();
    }

    private void setupClientInfoMenu(Menu clientInfoMenu) {
        itemInserter
                .add(new FunctionalItem(SHOW, context -> System.out.printf(CONFIG_STRING, configuration.getClientInfo())))
                .add(new FunctionalItem(SET_NAME, context -> {
                    System.out.print(ENTER_NAME_INVITATION);
                    String name = input.read();
                    if (name.isBlank()) {
                        System.err.println(NAME_CAN_NOT_BE_BLANK);
                        return;
                    }
                    configuration.getClientInfo().setName(name);
                }))
                .insert(clientInfoMenu)
                .clear();
    }

    private void setupMainMenu(Menu mainMenu, Menu clientInfoMenu, Menu serverInfoMenu) {
        itemInserter
                .add(new ForthItem(CLIENT_INFO, clientInfoMenu))
                .add(new ForthItem(SERVER_INFO, serverInfoMenu))
                .add(new FunctionalItem(CONNECT, context -> {
                    clientBuilder.setConfiguration(configuration);
                    ServerWrapper serverWrapper = ServerWrapper.getInstance();
                    serverWrapper.init(clientBuilder);
                    Menu kvStorageMenu = serverWrapper.getServerName()
                            .map(serverName -> new Menu(mainMenu, serverName))
                            .orElse(null);
                    if (kvStorageMenu == null) {
                        System.err.println(NO_SERVER_NAME_RETURNED);
                        return;
                    }
                    setupKvStorageMenu(serverWrapper, kvStorageMenu);
                    context.put(ContextKey.NEXT_MENU, kvStorageMenu);
                }))
                .insert(mainMenu)
                .clear();
    }

    private void setupKvStorageMenu(ServerWrapper serverWrapper, Menu kvStorageMenu) {
        new ItemInserter(title -> new ReturnItemImpl(title, ApplicationContext.getInstance()))
                .add(new FunctionalItem("Read an entry", ctx -> {
                    System.out.print("Enter an entry name: ");
                    String entryName = input.read();
                    if (entryName.isBlank()) {
                        System.err.println("Requested entry name should not be blank");
                        return;
                    }
                    serverWrapper.fetchEntry(entryName)
                            .ifPresentOrElse(System.out::println, () -> {
                                System.err.println("Server returned empty answer on requested entry. It seems there is no such entry on the server");
                            });
                }))
                .add(new FunctionalItem(0, "Return", context -> {
                    serverWrapper.close();
                    context.put(ContextKey.NEXT_MENU, kvStorageMenu.getParent());
                }))
                .insert(kvStorageMenu);
    }
}
