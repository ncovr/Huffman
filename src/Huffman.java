/**
 * Programa principal que permite usar el compresor de Huffman desde la línea de comando.
 * Con este programa se puede comprimir un archivo cualquiera y descomprimirlo.
 */
public class Huffman {

    public static void help() {
        System.out.println("\nCompresor de archivos de huffman");
        System.out.println("Curso estructuras de datos Universidad del Bío-Bío");
        System.out.println("========================================================");
        System.out.println("uso:");
        System.out.println("   java Huffman [opcion] [archivo.huff] [archivo sin comprimir] ");
        System.out.println();
        System.out.println("Opciones:");
        System.out.println("        -c: compresion. El [archivo.huff] sera el resultado de comprimir mediante huffman el [archivo sin comprimir]");
        System.out.println("        -d: descompresion. El [archivo sin comprimir] es el resultado de descomprimir el [archivo.huff]");
        System.out.println("\nNota: Tíldes omitidos intencionalmente");


    }

    // [archivo sin comprimir] y [archivo.huff] corresponden a las rutas de los archivos

    public static void main(String[] args) {
        // Para probar el programa sin CMD
        String archivoInicial = "C:\\Users\\Nicolás Verdugo\\Desktop\\Nuevo Documento de texto.txt"; // Establezca ruta del archivo
        String archivoComprimido = "C:\\Users\\Nicolás Verdugo\\Desktop\\Nuevo Documento de texto - copia.txt"; // Establezca ruta del archivo
        String archivoDescomprimido = "C:\\Users\\Nicolás Verdugo\\Desktop\\Nuevo Documento de texto - copia (2).txt"; // Establezca ruta del archivo
        HuffmanEncoder huffmanEncoder = new HuffmanEncoder(archivoInicial, archivoComprimido);
        huffmanEncoder.encode(); // Codifica
        HuffmanDecoder huffmanDecoder = new HuffmanDecoder(archivoComprimido,archivoDescomprimido);
        huffmanDecoder.decode(); // Decodifica

        /*if (args.length < 3) {
            help(); // explica que el programa espera este formato: java Huffman [opcion] [archivo.huff] [archivo sin comprimir]
        } else if (args[0].equalsIgnoreCase("-c")) {
            // si la opción es -c, el programa debe comprimir el archivo [archivo sin comprimir], entregándolo como [archivo.huff]
            HuffmanEncoder huffmanEncoder = new HuffmanEncoder(args[2], args[1]);
            huffmanEncoder.encode(); // implementar: comprimir el primer archivo entregado
        } else if (args[0].equalsIgnoreCase(("-d"))) {
            // si la opcion es -d, el programa debe descomprimir el [archivo.huff] entregando [archivo sin comprimir]
            HuffmanDecoder huffmanDecoder = new HuffmanDecoder(args[1], args[2]);
            huffmanDecoder.decode();
        } else {
            help();
        }*/

    }
}
