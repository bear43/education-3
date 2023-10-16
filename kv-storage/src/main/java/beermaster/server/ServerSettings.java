package beermaster.server;

public record ServerSettings(String name, int port, String fileStorageRoot, String dataFilename, String infoFilename) {

    public static final String DEFAULT_SERVER_NAME = "kv-storage-server";
    public static final int DEFAULT_PORT = 7777;
    public static final String DEFAULT_FILE_STORAGE_ROOT = "/Users/konstantinkotikov/IdeaProjects/education/kv-storage/storage/education";
    public static final String DEFAULT_DATA_FILENAME = "datum.bin";
    public static final String DEFAULT_INFO_FILENAME = "info.obj";

    public ServerSettings() {
        this(DEFAULT_SERVER_NAME, DEFAULT_PORT, DEFAULT_FILE_STORAGE_ROOT, DEFAULT_DATA_FILENAME, DEFAULT_INFO_FILENAME);
    }
}
