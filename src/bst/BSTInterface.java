package bst;


public interface BSTInterface<E extends Comparable<E>> {

    void insert(E value);

    boolean contains(E value);

    int size();

    boolean isEmpty();
}
