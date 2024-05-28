import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * permite leer desde un archivo byte a byte de manera secuencial. Internamente tiene un buffer.
 */
public class FileBufferedBitReader implements AutoCloseable {
    /** Flujo de entrada para el archivo */
    FileInputStream input;

    /** Buffer para almacenar los bytes leídos del archivo */
    byte[] buffer;

    /** Cantidad de bytes que realmente contiene el buffer*/
    int bufferSize;

    /** Cantidad de bytes que se han leído del buffer*/
    int lecturas; //

    /**  Índice del bit en el byte de buffer[lecturas], cuando se lee de bits en bits.*/
    int bitPos = 0; //

    /**
     * Constructor que inicializa un FileBufferedBitReader a partir del nombre del archivo de entrada.
     *
     * @param inputFileName Nombre del archivo de entrada.
     * @throws FileNotFoundException Si el archivo de entrada no es encontrado.
     */
    public FileBufferedBitReader(String inputFileName) throws FileNotFoundException {
        this.input = new FileInputStream(inputFileName);
        buffer = new byte[4 * 1024];
        bufferSize = 0;
        lecturas = 0;
    }

    /**
     * Constructor que inicializa un FileBufferedBitReader con un flujo de entrada y un tamaño de buffer específico.
     *
     * @param input       Flujo de entrada para el archivo.
     * @param bufferLength Tamaño del buffer interno.
     */
    public FileBufferedBitReader(FileInputStream input, int bufferLength) {
        this.input = input;
        buffer = new byte[bufferLength];
        bufferSize = 0;
        lecturas = 0;
    }

    /**
     * Verifica si hay más bytes disponibles para leer en el archivo.
     *
     * @return true si hay más bytes disponibles, false de lo contrario.
     * @throws IOException Si ocurre un error de lectura en el archivo.
     */
    public boolean hasNext() throws IOException {
        if (lecturas < bufferSize) {
            return true;
        }
        bufferSize = input.read(buffer);
        if (bufferSize != -1) {
            lecturas = 0;
            bitPos = 0;
            return true;
        }
        return false;
    }

    /**
     * Lee el siguiente byte del archivo.
     *
     * @return El siguiente byte leído del archivo.
     * @throws IOException Si ocurre un error de lectura en el archivo.
     */
    public byte nextByte() throws IOException {
        if (hasNext()) {
            bitPos = 0;
            return buffer[lecturas++];
        }
        throw new IOException("No more byte to read");
    }

    /**
     * Lee el siguiente long del archivo.
     *
     * @return El siguiente long leído del archivo.
     * @throws IOException Si ocurre un error de lectura en el archivo.
     */
    public long nextLong() throws IOException {
        long out = 0;
        byte data;
        long dataL;
        for (int i = 1; i < Long.BYTES; i++) {
            data = nextByte();
            dataL = (long) data;
            out |= dataL << (Long.SIZE - (i * 8));
        }
        out |= (long) nextByte();
        return out;
    }

    /**
     * Permite leer el archivo bit a bit.
     * Toma el siguiente bit que no se ha leído desde el último byte no leído con nextByte.
     *
     * @return El siguiente bit leído del archivo.
     * @throws IOException Si ocurre un error de lectura en el archivo.
     */
    public boolean nextBit() throws IOException {
        byte mask= (byte) (1<<7-bitPos++);
        boolean output = ( mask & buffer[lecturas] ) != 0;
        if (bitPos == 8) {
            lecturas++;
            bitPos = 0;
        }
        return output;

    }
    /**
     * Cierra el flujo de entrada del archivo.
     *
     * @throws IOException Si ocurre un error al cerrar el flujo de entrada del archivo.
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
}
