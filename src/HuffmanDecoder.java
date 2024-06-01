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
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFileName)); // Abre el archivo comprimido
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFileName))) { // Abre el archivo descomprimido
            if(inputStream.available() == 0){ // Si el archivo está vacío, retorna la funcion para evitar que e programa se caiga
                File archivo = new File(inputFileName); // Se crea un objeto de la clase File para escribir en consola el nombre del archivo
                System.out.println(archivo.getName()+" está vacío (no hay texto que decodificar)"); // Informa por consola que el archivo a decodificar se encuentra vacio
                return; // Retorna, lo que hace que el programa termine sin errores
            }

            // Hay que leer el diccionario de 256 long
            long[] diccionario = new long[256]; // frecuencias de cada caracter
            int contador = 0; // Cuenta hasta 8
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < 256; i++) { // leer 256 caracteres (lee todo el diccionario)
                while (contador < 8) { // juntar 8 bits, transformarlos en long e insertarlos en diccionario[]
                    // Si entra aqui es porque se está armando el long
                    char lectura = (char) inputStream.read(); // Obtiene un caracter secuencialmente
                    stringBuilder.append(lectura); // Se concatena el caracter leído
                    contador++;
                }
                // Para llegar hasta aca, se juntaron 8 bits. Ahora, con ello se arma un long y se guarda en la posición i (código del caracter)
                diccionario[i] = Long.parseLong(stringBuilder.toString(), 2); // convertir binario a long
                stringBuilder.setLength(0); // resetear el StringBuilder, para armar una nueva secuencia
                contador = 0; // resetear el contador para volver a juntar 8 bits
            }
            inputStream.read(); // Como se leyó todo el diccionario, agregamos esta linea para liberar "|"

            // Hay que leer el total de bits (importante para no leer basura)
            int totalBits = 0;
            for (int i = 0; i < 32; i++) { // leer 32 bits
                totalBits <<= 1;
                totalBits |= inputStream.read() & 1; // construir totalBits a partir de los bits leídos
            }
            inputStream.read(); // Despeja la lectura sacando | del texto

            // Hay que crear un árbol de Huffman para descomprimir.
            HuffmanTree arbol = HuffmanTree.of(diccionario); // Arbol a partir del diccionario (frecuencias leídas)
            HuffmanIterator iterator = arbol.getIterator(); // Obtener un iterador para recorrer el árbol

            // Hay que decodificar el archivo usando el árbol. recorrer el árbol con el iterador. al llegar una hoja, append el carácter
            stringBuilder.delete(0, stringBuilder.length()); // limpiamos el stringbuilder para no declarar otro (reciclaje de la variable)
            int read = 0; // Captura la lectura secuencial del código Huffman

            while (read > -1) { // Lee hasta el último código Huffman
                if (iterator.isLeaf()) { // Si llegamos a una hoja
                    stringBuilder.append((char) iterator.getValue()); // Rescatamos el byte y lo interpretamos como caracter
                    outputStream.write((char) iterator.getValue()); // Escribimos el carácter en el archivo de salida
                    iterator.reset(); // volver a la raiz (nodo actual = raiz del arbol). Reestablecemos la ruta iniciando en la raiz
                } else { // Si el nodo del arbol no es una hoja (es una suma de frecuencias), significa que tenemos que bajar al hijo izquierdo o derecho
                    read = inputStream.read(); // Obtenemos la instrucción (0 o 1)
                    if (read == 48) { // Si obtenemos un 0 en la lectura, bajamos al hijo izquierdo (0 en ASCII es 48)
                        // bajar a la izquierda
                        iterator.forward(false); // Establece como nodo actual el hijo izquierdo del nodo actual
                    } else { // Si obtenemos un 1 en la lectura, bajamos al hijo derecho
                        // bajar a la derecha
                        iterator.forward(true); // Establece como nodo actual el hijo derecho del nodo actual
                    }
                }
            }

            System.out.println("Descompresión exitosa...");


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
