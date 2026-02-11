package arraylist;

public interface Queue<E> {
    void enqueue(E e);
    E dequeue();
    E peek();
    boolean isEmpty();
    int size();
}

