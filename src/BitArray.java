import java.nio.ByteBuffer;

/**
 * Clase que implementa un BitArray, también llamado bitmap, bitset, bitvector.
 *
 * Permite manejar a nivel de bits una secuencica de largo arbitrario de bits.
 *
 * @author Luis Gajardo, Miguel Romero y Fernando Santolaya
 * Basado en
 * http://stackoverflow.com/questions/15736626/java-how-to-create-and-manipulate-a-bit-array-with-length-of-10-million-bits
 */
public class BitArray {
    private static final int WORD_SIZE = Long.SIZE;
    private long length;
    protected long bits[];

    /**
     * Crea un Array de bits.
     *
     * @param size es la cantidad de bits que tiene el BitArray this.
     */
    public BitArray(long size) {
        this.length = size;
        //se constuye un arreglo con ceil(length/word_size) bloques.
        bits = new long[(int) ((size - 1) / WORD_SIZE + 1)];
    }

    private BitArray() {
        bits = null;
        length = 0;
    }

    /**
     * Permite conocer el valor 0 o 1 de la i-ésima posición del BitArray
     * los bits están enumerados partiendo desde 0 en adelante. el bit 0 es el primero por la izquierda.
     * @param pos La posición del bit que se desea obtener.
     * @return El valor del bit en la posición especificada. 'true' si es 1, 'false' si es 0.
     * @throws IndexOutOfBoundsException Si la posición especificada está fuera del rango válido.
     */
    public boolean getBit(int pos) {
        if (pos < 0) throw new IndexOutOfBoundsException("pos < 0: " + pos);
        if (pos >= length) throw new IndexOutOfBoundsException("pos >= length():" + pos);
        long mask = ((1L<<WORD_SIZE-1) >>> (pos % WORD_SIZE));
        return (bits[pos / WORD_SIZE] & mask) != 0;
    }

    /**
     * Establece el valor del bit en la posición especificada.
     * Este método establece en 1 el valor del bit en la posición especificada del dato.
     *
     * @param pos La posición del bit que se desea establecer en 1.
     * @throws IndexOutOfBoundsException Si la posición especificada está fuera del rango válido.
     */
    public void setBit(int pos) {
        if (pos < 0) throw new IndexOutOfBoundsException("pos < 0: " + pos);
        if (pos > length) throw new IndexOutOfBoundsException("pos >= length():" + pos);

        long block = bits[pos / WORD_SIZE];
        long mask = ((1L<<WORD_SIZE-1) >>> (pos % WORD_SIZE));
        block |= mask;
        bits[pos / WORD_SIZE] = block;
    }

    /**
     * Establece el valor del bit en la posición especificada.
     * Este método establece el valor del bit en la posición especificada del dato
     * con el valor booleano proporcionado.
     *
     * @param pos La posición del bit que se desea establecer.
     * @param b El valor booleano que se asignará al bit en la posición especificada.
     *           'true' para establecer el bit en 1, 'false' para establecerlo en 0.
     * @throws IndexOutOfBoundsException Si la posición especificada está fuera del rango válido.
     */
    public void setBit(int pos, boolean b) {
        if (pos < 0) throw new IndexOutOfBoundsException("pos < 0: " + pos);
        if (pos > length) throw new IndexOutOfBoundsException("pos >= length():" + pos);

        long block = bits[pos / WORD_SIZE];
        long mask = ((1L<<WORD_SIZE-1) >>> (pos % WORD_SIZE));
        if (b) {
            block |= mask;
        } else {
            block &= ~mask;
        }
        bits[pos / WORD_SIZE] = block;
    }

    /**
     * Establece el valor del bit en la posición especificada a 0.
     * Este método establece el valor del bit en la posición especificada del dato a 0.
     *
     * @param pos La posición del bit que se desea poner en 0.
     * @throws IndexOutOfBoundsException Si la posición especificada está fuera del rango válido.
     */
    public void clearBit(int pos) {
        if (pos < 0) throw new IndexOutOfBoundsException("pos < 0: " + pos);
        if (pos > length) throw new IndexOutOfBoundsException("pos >= length():" + pos);

        long block = bits[pos / WORD_SIZE];
        long mask = ((1L<<WORD_SIZE-1) >>> (pos % WORD_SIZE));
        block &= ~mask;
        bits[pos / WORD_SIZE] = block;
    }

    /**
     * Devuelve la longitud en bits de la secuencia.
     * Este método devuelve la cantidad de bits total de la secuencia de bits.
     *
     * @return La cantidad de bit en el BitArray
     */
    public long length() {
        return length;
    }

    /**
     * Retorna el tamaño del BitArray en bytes.
     * Este método devuelve el tamaño total del BitArray en bytes.
     *
     * @return El tamaño del BitArray en bytes.
     */
    public long size() {
        //8 por variable this.length
        //4 por bits.length
        //8 por la referencia a bits (pensando en arquitectura de 64 bits, peor caso).

        return (bits.length * WORD_SIZE) / 8 + 8 + 4 + 8;
    }

    /**
     * Retorna una representación de cadena de bits del BitArray como un String.
     * Este método genera una cadena de caracteres que representa los bits almacenados en el BitArray.
     *
     * @return Una representación de cadena de bits del BitArray como String.
     */
    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < length; i++) {
            out += getBit(i) ? "1" : "0";
        }
        return out;
    }
    /**
     * Retorna un BitArray creado a partir de un valor de byte.
     *
     * @param bitsString El valor del byte del cual se creará el BitArray.
     * @return Un nuevo BitArray creado a partir del valor de byte especificado.
     */
    public static BitArray valueOf(byte bitsString) {
        BitArray output = new BitArray(Byte.SIZE);
        output.bits[0] |= ((long) bitsString) << (WORD_SIZE - 8);
        return output;
    }
    /**
     * Retorna un BitArray creado a partir de un valor de long.
     *
     * @param bitsString El valor de long del cual se creará el BitArray.
     * @return Un nuevo BitArray creado a partir del valor de long especificado.
     */
    public static BitArray valueOf(long bitsString) {
        BitArray output = new BitArray(Long.SIZE);
        output.bits[0] = bitsString;
        return output;
    }
    /**
     * Retorna un BitArray creado a partir de un arreglo de long.
     *
     * @param bitsString El arreglo de long del cual se creará el BitArray.
     * @return Un nuevo BitArray creado a partir del arreglo de long especificado.
     */
    public static BitArray valueOf(long[] bitsString) {
        BitArray output = new BitArray();
        output.length = (long) bitsString.length * Long.SIZE;
        output.bits = bitsString.clone();
        return output;
    }

    /**
     * Retorna un arreglo de bytes que representa los bits almacenados en el BitArray.
     *
     * @return Un arreglo de bytes que representa los bits almacenados en el BitArray.
     */
    public byte[] toByteArray() {
        byte[] output = new byte[Long.BYTES * bits.length];
        int count = 0;
        for (int i = 0; i < bits.length; i++) {
            long word = bits[i];
            for (int j = 1; j < Long.BYTES; j++) {
                output[count++] = (byte) (word >>> (WORD_SIZE - (8 * j)));
            }
            output[count++] = (byte) word;
        }
        return output;
    }

}
