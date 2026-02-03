package arraylist;

public interface Stack<E> {
    void push(E e);
    E pop();
    E peek();
    boolean isEmpty();
    int size();
}