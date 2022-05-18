package utils;

import java.io.*;

/**
 * Class for serializing and deserializing objects
 */
public class Serializer {
    /**
     * Serializes a serializable object
     * @param object Object
     * @param <T> Object's class
     * @return Byte array representation of object
     * @throws IOException
     */
    public <T> byte[] serialize(T object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byteArrayOutputStream.flush();
            byte[] buf = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return buf;
        }
    }

    /**
     * Deserializes a byte array into an Object object
     * @param buf Byte array
     * @return Object representation of object (class cast might be needed later)
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object deserialize(byte[] buf) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            Object response = objectInputStream.readObject();
            byteArrayInputStream.close();
            objectInputStream.close();
            return response;
        }
    }
}
