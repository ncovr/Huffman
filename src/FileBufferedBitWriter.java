import java.io.*;


/**
 * Escritura secuencial de un bitString a un archivo. internamente utiliza un buffer.
 */
public class FileBufferedBitWriter implements Closeable {
    /** Buffer para almacenar los bits a escribir en el archivo */
    BitArray buffer;
    /**  Tamaño del buffer*/
    int bufferSize;
    /** Cantidad de bits actualmente en el buffer */
    int bitCount;
    /** Flujo de salida para escribir en el archivo */
    BufferedOutputStream output;

    /**
     * Constructor que inicializa un FileBufferedBitWriter a partir del nombre del archivo de salida.
     *
     * @param outputFileName Nombre del archivo de salida.
     * @throws FileNotFoundException Si el archivo de salida no es encontrado.
     */
    public FileBufferedBitWriter(String outputFileName) throws FileNotFoundException {
        this.output = new BufferedOutputStream(new FileOutputStream(outputFileName));
        bufferSize = 8 * 4 * 1024;//4KB
        buffer = new BitArray(bufferSize);
        bitCount = 0;
    }

    /**
     * Constructor que inicializa un FileBufferedBitWriter con un flujo de salida y un tamaño de buffer específico.
     *
     * @param output         Flujo de salida para el archivo.
     * @param bufferBitLength Tamaño del buffer interno en bits.
     */
    public FileBufferedBitWriter(BufferedOutputStream output, int bufferBitLength) {
        this.output = output;
        bufferSize = bufferBitLength;
        buffer = new BitArray(bufferBitLength);
        bitCount = 0;
    }

    /**
     * Agrega al final del archivo una secuencia de bits.
     *
     * @param bitString Secuencia de bits almacenada en un String ("1110000111").
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    public void write(String bitString) throws IOException {
        for (char c : bitString.toCharArray()) {
            if (bitCount >= bufferSize) flushBuffer();
            if (c == '1') buffer.setBit(bitCount++);
            else buffer.clearBit(bitCount++);
        }
    }

    /**
     * Agrega al final del archivo un byte.
     *
     * @param bitsString El byte que se escribirá en el archivo.
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    public void write(byte bitsString)throws IOException{
        BitArray bitSet=BitArray.valueOf(bitsString);
        write(bitSet);
    }

    /**
     * Agrega al final del archivo un long.
     *
     * @param bitsString El long que se escribirá en el archivo.
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    public void write(long bitsString) throws IOException {
        BitArray theBits=BitArray.valueOf(bitsString);
        write(theBits);

    }
    /**
     * Agrega al final del archivo un arreglo de long.
     *
     * @param bitsString El arreglo de long que se escribirá en el archivo.
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    public void write(long[] bitsString) throws IOException {
        BitArray theBits=BitArray.valueOf(bitsString);
        write(theBits);
    }

    /**
     * Agrega al final del archivo una secuencia de bits representada por un BitArray.
     *
     * @param bitsString El BitArray que contiene la secuencia de bits a escribir en el archivo.
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    public void write(BitArray bitsString) throws IOException {
        for(int i=0;i< bitsString.length();i++){
            if (bitCount >= bufferSize) flushBuffer();
            buffer.setBit(bitCount++,bitsString.getBit(i));
        }
    }

    /**
     * Vacía el buffer escribiendo su contenido en el archivo.
     *
     * @throws IOException Si ocurre un error de escritura en el archivo.
     */
    private void flushBuffer() throws IOException {
        if (bitCount > 0) {
            output.write(buffer.toByteArray(), 0, (bitCount +7) / 8);
            bitCount = 0;
        }
    }

    /**
     * Cierra el flujo de salida del archivo.
     *
     * @throws IOException Si ocurre un error al cerrar el flujo de salida del archivo.
     */
    @Override
    public void close() throws IOException {
        flushBuffer();
        output.close();
    }
}
