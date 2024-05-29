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
        long[] tablaFrecuencias = generarTablaDeFrecuencias(); // Arreglo con las frecuencias de cada carácter
        HuffmanTree arbolH = HuffmanTree.of(tablaFrecuencias); // Arbol hecho a partir de las frecuencias
        String[] encodeTable = arbolH.encodeTable(); // Arreglo con los códigos Huffman de cada carácter

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile)); // Abre el archivo para leer
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))) { // Abre el archivo para escribir

            // Escribir las frecuencias en el archivo de salida
            escribirTablaEnArchivo(tablaFrecuencias, outputStream);
            outputStream.writeByte(124); // Escribe un | para establecer el límite de las frecuencias

            // Leer el archivo de entrada y generar la secuencia de bits comprimidos
            StringBuilder secuencia = new StringBuilder(); // StringBuilder para ir concatenando los códigos
            String lectura = String.valueOf(inputStream.read()); // Captura cada caracter del archivo
            // LLega un caracter, tomamos su codigo ASCII, luego concatenamos al texto final el código Huffman presente en la posición ASCII del caracter
            while (!lectura.equals("-1")) { // Mientras hayan caracteres por leer
                secuencia.append(encodeTable[Integer.parseInt(lectura)]); // Concatenación: código Huffman en la posición x del arreglo. x es el código ASCII del caracter
                lectura = String.valueOf(inputStream.read()); // Lee lo que sigue
            }

            // Convertir la secuencia de bits en un array de bytes
            String secuenciaEnBits = secuencia.toString();
            byte longitudCompress = (byte) secuenciaEnBits.length();
            byte[] datosCompress = new byte[(secuenciaEnBits.length() + 7) / 8];

            for (int i = 0; i < secuenciaEnBits.length(); i++) {
                if (secuenciaEnBits.charAt(i) == '1') {
                    datosCompress[i / 8] |= (128 >> (i % 8));
                }
            }

            // Escribir la longitud en bits y los datos comprimidos
            outputStream.write(convertirEnteroASerieDeBits(longitudCompress).getBytes()); // Escribe la cantidad de bits
            outputStream.writeByte(124); // Escribe el separador | que establece el limite de la longitud en bits
            outputStream.write(secuencia.toString().getBytes()); // Escribe la codificación Huffman

            System.out.println("Compresión exitosa..."); // Informa por consola que se ha concluido la compresión

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera una tabla de frecuencias para los datos proporcionados en el archivo de entrada.
     * Esta tabla representa la frecuencia de cada byte del archivo de entrada.
     *
     * <p>Abre el archivo a comprimir, cuenta cuantas veces se repite cada caracter. Mientras va leyendo el archivo, al toparse con un caracter arbitrario,
     * suma 1 en la posición del caracter, el cual es el código ASCII</p>
     *
     * @return Una array de tipo long donde cada índice representa un valor único
     * en los datos y el valor en ese índice representa la frecuencia de ese valor.
     */
    public long[] generarTablaDeFrecuencias() {
        long[] out = new long[256]; // la tabla ASCII contiene 256 caracteres
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));// abre el archivo
            String linea; // representa cada linea leida del archivo de texto
            while ((linea = reader.readLine()) != null) { // lee hasta que no queden lineas por leer
                for (char c : linea.toCharArray()) { // se recorre cada caracter de la linea y se cuenta su frecuencia
                    // c es caracter. si pedimos c en int, nos da un número, el cual corresponde a su código en ASCII
                    out[(int) c] += 1; // sumamos 1 en la posición que ocupa el caracter
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
        for (Long l : tabla) { // para cada posición de la tabla
            // se escribe su contenido en formato binario en el archivo comprimido
            binaryString = String.format("%8s", Integer.toBinaryString((int) (l & 0xFF))).replace(' ', '0');
            writer.write(binaryString.getBytes());
        }
    }

    /**
     * Funcion que convierte, por ejemplo, 8 en 00000000000000000000000000001000
     */
    private String convertirEnteroASerieDeBits(int numero) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            int bit = (numero >> i) & 1;
            sb.append(bit);
        }
        return sb.toString();
    }
}
