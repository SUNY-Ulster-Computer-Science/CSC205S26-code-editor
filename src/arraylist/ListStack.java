package arraylist;

import genericlist.GenericList;

public final class ListStack<E> implements Stack<E> {
private final GenericList<E> list;
public ListStack(GenericList<E> list) {
this.list = list;
}
@Override
public void push(E e) { list.add(e); }
@Override
public E pop() {
if (list.isEmpty()) throw new IllegalStateException("empty");
return list.remove(list.size() - 1);
}
@Override
public E peek() {
if (list.isEmpty()) throw new IllegalStateException("empty");
return list.get(list.size() - 1);
}
@Override
public boolean isEmpty() { return list.isEmpty(); }
@Override
public int size() { return list.size(); }
}
