package genericlist;
public interface GenericList<E> {
void add(E element);
void add(int index, E element);
E get(int index);
E remove(int index);
int contains(E element); // index of first match, or -1
int size();
boolean isEmpty();
void clear();
@Override String toString();
GenericList<E> newList();
}