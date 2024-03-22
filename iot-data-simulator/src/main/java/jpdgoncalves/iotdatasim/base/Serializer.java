package jpdgoncalves.iotdatasim.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Serializer capable of encoding to bytes
 * and decoding from bytes some data type T.
 * @param <T> The type of data that this Serializer handles.
 */
public interface Serializer<T> {

    /**
     * Write the instance to the output stream.
     * The way this is done is by writing at the start
     * an int indicating the size of the serialized data and
     * then writing the serialized data itself.
     * @param out To where the data will be writen.
     * @param instance The instance to write to the output stream.
     * @throws IOException If an error occurs while writing the
     * data to the output stream.
     */
    public default void write(OutputStream out, T instance) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(out);
        byte[] data = serialize(instance);
        int size = data.length;
        outputStream.writeInt(size);
        outputStream.write(data);
    }

    /**
     * Encodes a type T instance to bytes.
     * @param instance The instance to encode.
     * @return The bytes of the instance.
     */
    public byte[] serialize(T instance);

    /**
     * Reads data writing with the write method of the class
     * that implements this interface. It does this by first
     * reading from the InputStream an int indicating the number 
     * of serialized bytes and then reading the number of bytes
     * indicated by this number.
     * @param in The InputStream from which the bytes will
     * be read.
     * @return An instance of the class.
     * @throws IOException If an error occurs while reading data
     * from the input stream.
     */
    public default T read(InputStream in) throws IOException {
        DataInputStream inputStream = new DataInputStream(in);
        int size = inputStream.readInt();
        byte[] data = inputStream.readNBytes(size);
        return deserialize(data);
    }

    /**
     * Decodes an array of bytes to a
     * instance of type T.
     * @param data The bytes of the encoded instance.
     * @return The decoded instance.
     */
    public T deserialize(byte[] data);
}
