import java.io.*;

/**
 * Clase para codificar datos utilizando el algoritmo de Huffman.
 * Esta clase proporciona funcionalidades para comprimir datos utilizando el algoritmo de Huffman.
 */
public class HuffmanEncoder {

    /**
     * Ruta al archivo de entrada.
     */
    private String inputFile;

    /**
     * Ruta al archivo de salida.
     */
    private String outputFile;

    /**
     * Constructor de la clase HuffmanEncoder.
     *
     * @param inputFile  Ruta al archivo de entrada que se desea comprimir.
     * @param outputFile Ruta al archivo de salida donde se almacenará la versión comprimida de los datos.
     */

    public HuffmanEncoder(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }


    /**
     * lee el archivo de entrada y lo comprime usando Huffman, en el archivo de salida.
     * formato archivo de salida:
     * long[256] frecuencias|long largo_en_bits|bits archivo comprimido...
     */
    public void encode() {
        long[] tablaFrecuencias = generarTablaDeFrecuencias();
        HuffmanTree arbolH = HuffmanTree.of(tablaFrecuencias);
        arbolH.imprimirArbol();
        String[] encodeTable = arbolH.encodeTable();
        for (int i = 0; i < encodeTable.length; i++) {
            if(encodeTable[i] != null){
                System.out.println((char) i +" "+encodeTable[i]);
            }
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))) {

            // Escribir las frecuencias en el archivo de salida
            escribirTablaEnArchivo(tablaFrecuencias, outputStream);
            outputStream.writeByte(124);

            // Leer el archivo de entrada y generar la secuencia de bits comprimidos
            // Ejemplo: h -> 01 FUNCIONA
            // llega "h", tomamos el codigo ascii, buscamos el codigo huffman en el índice de "h" en ascii en la tablaFrecuencias y lo concatenamos con la salida
            // aqui esta el problema. el orden de la codificacion debe ser en orden de lectura del archivo original
            StringBuilder secuencia = new StringBuilder();
            String lectura = "";

            while(!lectura.equals("-1")){
                lectura = String.valueOf(inputStream.read());
                if(!lectura.equals("-1")){
                    secuencia.append(encodeTable[Integer.parseInt(lectura)]);
                }
                System.out.println(lectura);
            }

            // Convertir la secuencia de bits en un array de bytes
            String secuenciaEnBits = secuencia.toString();
            System.out.println("Secuencia: "+secuenciaEnBits);
            byte longitudCompress = (byte) secuenciaEnBits.length();
            byte[] datosCompress = new byte[(secuenciaEnBits.length() + 7) / 8];

            for (int i = 0; i < secuenciaEnBits.length(); i++) {
                if (secuenciaEnBits.charAt(i) == '1') {
                    datosCompress[i / 8] |= (128 >> (i % 8));
                }
            }

            // Escribir la longitud en bits y los datos comprimidos
            outputStream.write(convertirEnteroASerieDeBits(longitudCompress).getBytes());
            outputStream.writeByte(124);
            outputStream.write(secuencia.toString().getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera una tabla de frecuencias para los datos proporcionados en el archivo de entrada.
     * Esta tabla representa la frecuencia de cada byte del archivo de entrada.
     *
     * @return Una array de tipo long donde cada índice representa un valor único
     * en los datos y el valor en ese índice representa la frecuencia de ese valor.
     */
    public long[] generarTablaDeFrecuencias() {
        //abre el archivo y cuenta la frecuencia de cada byte (8bits). Cada byte se puede interpretar como un caracter
        //de la tabla ASCII
        long[] out = new long[256];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));// abre el archivo
            String linea; // representa cada linea del archivo de texto
            while ((linea = reader.readLine()) != null) {
                for (char character : linea.toCharArray()) { // se recorre cada caracter de la linea y se cuenta su frecuencia
                    // contar la frecuencia. en la posición ASCII del char sumar 1
                    out[(int) character] += 1;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * <p>
     * Escribir cada frecuencia en formato de bits en el archivo de salida
     * </p><p>
     * *     Función por fines de simplificación de código
     * * </p>
     */
    private void escribirTablaEnArchivo(long[] tabla, DataOutputStream writer) throws IOException {
        String binaryString = "";
        for (Long l : tabla) {
            binaryString = String.format("%8s", Integer.toBinaryString((int) (l & 0xFF))).replace(' ', '0');
            writer.write(binaryString.getBytes());
        }
    }

    /**
     * Funcion que convierte, por ejemplo, 8 en 00000000000000000000000000001000
     * */
    public String convertirEnteroASerieDeBits(int numero) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            int bit = (numero >> i) & 1;
            sb.append(bit);
        }
        return sb.toString();
    }
}
