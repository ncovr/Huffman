/**
 * Interfaz para un iterador de Huffman.
 * Proporciona métodos para navegar a través de un árbol de Huffman.
 *
 * @author Miguel Romero
 * @version 1.0
 */
public interface HuffmanIterator {

    /**
     * Reinicia el iterador a la raíz del árbol de Huffman.
     * Reestablecer la lectura en la raiz del nodo
     */
    void reset();

    /**
     * Devuelve el valor del nodo actual en el árbol de Huffman.
     *
     * @return El valor del nodo actual.
     */
    byte getValue();

    /**
     * Avanza el iterador al siguiente nodo en el árbol de Huffman,
     * siguiendo la dirección indicada por el bit especificado.
     *
     * @param bit Dirección a la que avanzar el iterador.
     *            'true' indica avanzar al hijo derecho, 'false' al hijo izquierdo.
     */
    void forward(boolean bit);

    /**
     * Comprueba si el nodo actual en el árbol de Huffman es una hoja.
     *
     * @return 'true' si el nodo actual es una hoja, 'false' de lo contrario.
     */
    boolean isLeaf();
}
