## Модуль 3. Стандартная библиотека JDK

Для семинара была выбрана тема написания KV хранилища на основе log-append файлов ([Подробнее](https://medium.com/@arpitbhayani/bitcask-a-log-structured-fast-kv-store-c6c728a9536b), [А ещё тут](https://silhding.github.io/2021/08/20/A-Closer-Look-to-a-Key-Value-Storage-Engine/)).

Реализация сервера содержит работу с файлами log-append типа, однако не содержит главного - обработку клиентских соединений через селекторы. Необходимо реализовать интерфейсы Server, ServerBuilder.</br>
Реализация клиента также не содержит логики подключения к серверу, однако предоставляет интерфейсы Client, ClientBuilder, реализовав которые всё должно завестись.

Заготовки кода серверной части kv-storage:
```java
public interface Server {
    void start() throws IOException;
}

public interface ServerBuilder {
    ServerBuilder setSettings(ServerSettings settings);
    ServerSettings getSettings();
    Server build();
}
```

Заготовки кода клиента kv-storage-client:
```java
public interface Client extends Closeable {
    void start() throws IOException;
    Optional<byte[]> waitResponse() throws IOException;
    void send(byte[] bytes) throws IOException;
}

public interface ClientBuilder {
    Configuration getConfiguration();
    void setConfiguration(Configuration configuration);
    Client create();
}
```

Клиент и сервер содержат в себе реализации-заглушки (StubServer, StubServerBuilder, StubClient, StubClientBuilder), выбрасывающие сообщение "Unsupported operation" в System.err.
</br>
Также можно самостоятельно реализовать интерфейсы для работы с файлами:

```java
public interface DataIn {
    Collection<DataRecord> read() throws IOException;
    DataRecord read(int offset) throws IOException;
    boolean exists();
}
```
```java
public interface DataOut {
    DataOut write(Collection<DataRecord> records) throws IOException;
    DataOut create() throws IOException;
    DataOut delete();
}
```
```java
public interface DataInfoIn {
    InfoRecord read() throws IOException, ClassNotFoundException;
    boolean exists();
}
```
```java
public interface DataInfoOut {
    DataInfoOut write(InfoRecord infoRecord) throws IOException;
    DataInfoOut create() throws IOException;
    DataInfoOut delete();
}
```

Ветка master не содержит реализации клиент-серверных интерфейсов, лишь стабы.</br>
Ветка impl с реализацией.