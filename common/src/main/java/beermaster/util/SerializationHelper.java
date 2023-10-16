package beermaster.util;

import java.io.*;

public class SerializationHelper {
    public static byte[] getBytes(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream dataOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T decodeObject(byte[] bytes) {
        try {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (T) objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
