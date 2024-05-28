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
        HuffmanEncoder huffmanEncoder = new HuffmanEncoder("C:\\Users\\Nicolás Verdugo\\Desktop\\textoNatural.txt", "C:\\Users\\Nicolás Verdugo\\Desktop\\textocompressed.txt");
        huffmanEncoder.encode(); // implementar: comprimir el primer archivo entregado

        HuffmanDecoder huffmanDecoder = new HuffmanDecoder("C:\\Users\\Nicolás Verdugo\\Desktop\\textocompressed.txt","C:\\Users\\Nicolás Verdugo\\Desktop\\textodescompressed.txt");
        huffmanDecoder.decode();

//        if(args.length<3){
//            help(); // explica que deben el programa espera este formato: java Huffman [opcion] [archivo.huff] [archivo sin comprimir]
//        }else if(args[0].equalsIgnoreCase("-c")){
//            // si la opción es -c, el programa debe comprimir el archivo [archivo sin comprimir], entregándolo como [archivo.huff]
//            HuffmanEncoder huffmanEncoder=new HuffmanEncoder(args[2]/*[archivo sin comprimir]*/,args[1]/*[archivo.huff]*/);
//            huffmanEncoder.encode(); // implementar: comprimir el primer archivo entregado
//        }else if(args[0].equalsIgnoreCase(("-d"))){
//            // si la opcion es -d, el programa debe descomprimir el [archivo.huff] entregando [archivo sin comprimir]
//            HuffmanDecoder huffmanDecoder=new HuffmanDecoder(args[1],args[2]);
//            huffmanDecoder.decode();
//        }else{
//            help();
//        }

    }
}
