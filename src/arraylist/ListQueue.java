package arraylist;

import genericlist.GenericList;

public final class ListQueue<E> implements Queue<E> {
private final GenericList<E> list;
public ListQueue(GenericList<E> list) {
this.list = list;
}
@Override
public void enqueue(E e) { list.add(e); }
@Override
public E dequeue() {
if (list.isEmpty()) throw new IllegalStateException("empty");
return list.remove(0);
}
@Override
public E peek() {
if (list.isEmpty()) throw new IllegalStateException("empty");
return list.get(0);
}
@Override
public boolean isEmpty() { return list.isEmpty(); }
@Override
public int size() { return list.size(); }
}
