import java.io.*;

/**
 * Clase para decodificar datos comprimidos utilizando el algoritmo de Huffman.
 * Esta clase proporciona funcionalidades para decodificar datos previamente comprimidos
 * utilizando el algoritmo de Huffman.
 *
 * @author Nicolás Verdugo
 * @version 1.0
 */
public class HuffmanDecoder {

    /**
     * Nombre del archivo de entrada a decodificar.
     */
    private String inputFileName;

    /**
     * Nombre del archivo de salida donde se almacenará la versión decodificada de los datos.
     */
    private String outputFileName;

    /**
     * Constructor de la clase HuffmanDecoder.
     *
     * @param inputFileName  Nombre del archivo de entrada que contiene los datos comprimidos a decodificar.
     * @param outputFileName Nombre del archivo de salida donde se almacenará la versión decodificada de los datos.
     */
    public HuffmanDecoder(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    /**
     * Decodifica los datos comprimidos utilizando el algoritmo de Huffman.
     * Este método decodifica los datos comprimidos previamente utilizando el algoritmo de Huffman (archivo de entrada)
     * y guarda la versión decodificada en el archivo de salida especificado en el constructor.
     */
    public void decode() {
        // Se abre el archivo para lectura por byte
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFileName));
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFileName))) {

            // Hay que leer el diccionario de 256 long FUNCIONA
            long[] diccionario = new long[256]; // frecuencias de cada caracter
            int contador = 0;
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < 256; i++) { // leer 256 caracteres
                while (contador < 8) { // juntar 8 bits, transformarlos en long e insertarlos en diccionario[]
                    char lectura = (char) inputStream.read();
                    stringBuilder.append(lectura);
                    contador++;
                }
                diccionario[i] = Long.parseLong(stringBuilder.toString(), 2); // convertir binario a long
                stringBuilder.setLength(0); // resetear el StringBuilder
                contador = 0; // resetear el contador
            }
            inputStream.read();

            // Hay que leer el total de bits (importante para no leer basura) FUNCIONA
            int totalBits = 0;
            for (int i = 0; i < 32; i++) { // leer 32 bits
                totalBits <<= 1;
                totalBits |= inputStream.read() & 1; // construir totalBits a partir de los bits leídos
            }
            inputStream.read();

            // Hay que crear un árbol de Huffman para descomprimir. PROBLEMAS CON EL ARBOL. EN VEZ DE DECODIFICAR. CORREGIR
            HuffmanTree arbol = HuffmanTree.of(diccionario);
            HuffmanIterator iterator = arbol.getIterator();

            // Hay que decodificar el archivo usando el árbol. recorrer el árbol con el iterador. al llegar una hoja, append el carácter
            stringBuilder.delete(0, stringBuilder.length()); // limpiamos el stringbuilder para no declarar otro (reciclaje de la variable)
            int leido = 0;
            int read = 0;
            iterator.reset();

            while (read > -1){
                if (iterator.isLeaf()) {
                    // si el nodo actual es una hoja, rescatar el byte del nodo e interpretarlo como caracter
                    stringBuilder.append((char) iterator.getValue()); // rescatamos el byte y lo interpretamos como caracter
                    outputStream.write((char) iterator.getValue()); // escribe en el archivo de salida
                    iterator.reset(); // volver a la raiz (nodo actual = raiz del arbol)
                    leido++;
                } else {
                    read = inputStream.read();
                    if (read == 48) { // si es 1. 49 en ASCII es 1
                        // bajar a la izquierda
                        iterator.forward(false);
                    } else {
                        // bajar a la derecha
                        iterator.forward(true);
                    }
                }
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
