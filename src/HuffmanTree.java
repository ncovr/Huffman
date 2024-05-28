import javax.management.StringValueExp;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Representa un árbol de Huffman utilizado para la compresión y descompresión de datos.
 */
public class HuffmanTree {
    /**
     * Nodo raíz del árbol de Huffman.
     */
    Nodo raiz;

    /**
     * Constructor predeterminado que inicializa un árbol de Huffman vacío.
     */
    public HuffmanTree() {
        raiz = null;
    }

    /**
     * Crea un árbol de Huffman a partir de un arreglo de frecuencias.
     *
     * @param frecuencias Arreglo de frecuencias de bytes.
     * @return Un nuevo árbol de Huffman creado a partir de las frecuencias proporcionadas.
     */
    public static HuffmanTree of(long[] frecuencias) {
        HuffmanTree tree = new HuffmanTree();
        tree.generarArbol(frecuencias);
        return tree;
    }
    /**
     * Genera el árbol de Huffman a partir de un arreglo de frecuencias.
     * <p>
     * Cada nodo hoja conserva el caracter y su frecuencia de aparicion en la frase. Cada nodo hoja es hijo de un padre que conserva la suma de la frecuencia de sus hijos (que son dos por ser un arbol binomial)
     *</p>
     * @param frecuencias Arreglo de frecuencias de bytes.
     */
    public void generarArbol(long[] frecuencias) {
        // uso de min-heap obligatorio
        PriorityQueue<Nodo> minHeap = new PriorityQueue<>();

        // crear nodos con sus caracteres y sus frecuencias
        for (int i = 0; i < frecuencias.length; i++) { // recorremos las frecuancias
            if (frecuencias[i] > 0) { // si la frecuencia es cero, no almacenamos nada
                minHeap.add(new Nodo((byte) i, frecuencias[i]));
                // nuevo nodo(posición del caracter en la lista expresado en bytes, número de ocurrencias)
            }
        }

        // Contruccion del árbol. En cada iteración se extraen nodos del minHeap. Itera hasta que quede un nodo en el arbol
        while (minHeap.size() > 1) {
            Nodo i = minHeap.poll(); // retira el primer elemento del minHeap que seria el minimo de la lista
            Nodo d = minHeap.poll(); // para hacerlo hermano del siguiente en la lista y asi crear un nodo padre que los relacione
            Nodo p = new Nodo((byte) 0, i.frecuencia + d.frecuencia, i, d);
            // new nodo en el arbol: posicion del caracter en tabla ASCII, suma de frecuencia de sus hijos, hijo iz, hijo der
            minHeap.add(p); // añadir el nuevo nodo al arbol
        }

        // en este punto solo quedaría un elemento en el minHeap, el cual es la raiz de este arbol
        raiz = minHeap.poll();
    }

    /**
     * Genera la tabla de codificación a partir del árbol de Huffman.
     *
     * @return Un arreglo de cadenas que representa la tabla de codificación (ej:¨["1101","11"...]). El índice de la
     * tabla representa el byte para el cual se asigna el código. En el ejemplo anterior 0->"1101".
     * @throws RuntimeException Si el árbol de Huffman está vacío.
     */
    public String[] encodeTable() {
        if (raiz == null) throw new RuntimeException("encode error");
        String[] out = new String[256];
        generateCodes(raiz, "", out);
        return out;
    }

    /**
     * Recorre el arbol desde la raiz para generar los códigos Huffman
     * <p>Para asignarle un codigo a un caracter arbitrario, se debe recorrer desde la raiz hsta su hoja. en su codigo, comenzando desde la raiz, si
     * se accede al hijo izquierdo, se le asigna un 0, si se accede al hijo derecho se le asigna un 1</p>
     * <p>Por ejemplo: "h" tiene como código 101, esto quiere decir que para llegar hasta su hoja dentro del árbol, es necesario empezar desde la raíz,
     * accader al hijo derecho, luego al izquierdo, luego al derecho y encontraríamos la "h" (conceptualmente, ya que si solicitamos el valor, nos retornaría
     * un valor en byte el cual nos dice la posición que ocupa la ocurrencia de su presencia dentro del codigo)</p>
     */

    private void generateCodes(Nodo node, String code, String[] out) {
        if (node == null) return;

        // si el nodo entregado por parametro es una hoja, significa que podemos acceder al caracter y a su ocurrencia
        if (node.izquierdo == null && node.derecho == null) {
            out[node.byteCode & 0xFF] = code; // lo que esta entre corchetes trata de una operación de bits. en este caso nos sirve para entregar un entero sin signo
        } else {
            // sino, pasa a los nodos hijos de izquierda a derecha (asignandoles el 0 o 1 según corresponda)
            generateCodes(node.izquierdo, code + "0", out);
            generateCodes(node.derecho, code + "1", out);
        }
    }

    public void imprimirArbol() {
        imprimirArbolRecursivo(raiz, "", true);
    }

    /**
     * Método privado que realiza un recorrido preorden para imprimir el árbol de Huffman. Por ChatGPT
     *
     * @param nodo El nodo actual.
     * @param prefix El prefijo para representar la estructura del árbol.
     * @param isTail Indica si es el último nodo.
     */
    private void imprimirArbolRecursivo(Nodo nodo, String prefix, boolean isTail) {
        if (nodo == null) {
            return;
        }
        System.out.println(prefix + (isTail ? "└── " : "├── ") + nodo.byteCode + "(" + nodo.frecuencia + ")");
        if (nodo.izquierdo != null || nodo.derecho != null) {
            if (nodo.izquierdo != null) {
                imprimirArbolRecursivo(nodo.izquierdo, prefix + (isTail ? "    " : "│   "), nodo.derecho == null);
            }
            if (nodo.derecho != null) {
                imprimirArbolRecursivo(nodo.derecho, prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }

    /**
     * Obtiene un iterador para recorrer el árbol de Huffman.
     *
     * @return Un iterador para recorrer el árbol de Huffman.
     */
    public HuffmanIterator getIterator() {
        return new _HuffmanIterator(raiz);
    }


    /**
     * Clase interna que implementa la interfaz {@link HuffmanIterator} para recorrer el árbol de Huffman.
     */
    private class _HuffmanIterator implements HuffmanIterator {
        Nodo head;
        Nodo actual;

        public _HuffmanIterator(Nodo head) {
            this.head = head;
            actual = head;
        }

        //se deben implementar todos los métodos de la clase
        @Override
        public void reset() {
            actual = head;
        }

        @Override
        public byte getValue() {
            return actual.byteCode;
        }

        @Override
        public void forward(boolean bit) {
            actual = bit ? actual.derecho : actual.izquierdo;
        }

        @Override
        public boolean isLeaf() { // cuando no tiene hijos y no es nulo
            return actual != null && actual.izquierdo == null && actual.derecho == null;
        }
    }

    /**
     * Clase interna que representa un nodo del árbol de Huffman.
     */
    private class Nodo implements Comparable<Nodo> {
        //defina los atributos de la clase Nodo
        byte byteCode; // código del caracter
        long frecuencia; // cuantas veces aparece en el texto
        Nodo izquierdo; // hijo izquierdo
        Nodo derecho; // hijo derecho

        /**
         * Constructor que crea un nodo con el byte y la frecuencia especificados.
         *
         * @param charCode Byte del archivo.
         * @param frec     Frecuencia del byte en el archivo.
         */
        public Nodo(byte charCode, long frec) {
            this(charCode, frec, null, null);
        }

        /**
         * Constructor que crea un nodo con el byte, la frecuencia y los hijos especificados.
         *
         * @param charCode Byte del archivo.
         * @param frec     Frecuencia del byte en el archivo.
         * @param iz       Hijo izquierdo del nodo.
         * @param der      Hijo derecho del nodo.
         */
        public Nodo(byte charCode, long frec, Nodo iz, Nodo der) {
            byteCode = charCode;
            frecuencia = frec;
            izquierdo = iz;
            derecho = der;
        }

        /**
         * Compara este nodo con otro nodo por su frecuencia.
         *
         * @param o Nodo con el que se compara.
         * @return Un valor negativo, cero o un valor positivo si este nodo es menor, igual o mayor que el otro nodo (o), respectivamente.
         */
        @Override
        public int compareTo(Nodo o) {
            return Long.compare(this.frecuencia, o.frecuencia);
        }
    }

}
