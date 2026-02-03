package linkedlist;
import genericlist.AbstractGenericList;
import genericlist.GenericList;
import genericlist.StepCounter;

public class LinkedGenericList<E> extends AbstractGenericList<E> {
private class Node {
E element;
Node next;
Node(E element) {
this.element = element;
this.next = null;
}
}
private Node head;
private Node tail;

public LinkedGenericList(StepCounter counter) {
    super(counter);
    head = tail = null;
    size = 0;
}

@Override
public void add(E e) {
    Node n = new Node(e);
    if (head == null) {      // empty list
        head = tail = n;
    } else {
        tail.next = n;
        tail = n;
    }
    size++;
    counter.inc();
}

@Override
public void add(int index, E e) {
    checkPositionIndex(index);

    if (index == size) {   // append
        add(e);
        return;
    }

    Node newNode = new Node(e);

    if (index == 0) { // insert at head
        newNode.next = head;
        head = newNode;
        if (tail == null) tail = newNode;
    } else {
        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
            counter.inc();
        }

        newNode.next = prev.next;
        prev.next = newNode;
    }

    size++;
    counter.inc();
}

@Override
public int contains(E element) { 
  Node current = head;
  int i = 0;
   while (current != null) {
       counter.inc();
     if (element == null ? current.element == null : element.equals(current.element)) return i;
       current = current.next;
      i++;
   }
  return -1;
}

@Override
public E get(int index) {
    checkElementIndex(index);

    Node current = head;
    for (int i = 0; i < index; i++) {
        current = current.next;
        counter.inc();
    }

    counter.inc();
    return current.element;
}

@Override
public E remove(int index) {
    checkElementIndex(index);

    Node removed;

    if (index == 0) {  // remove head
        removed = head;
        head = head.next;
        if (head == null) tail = null; // list is now empty
    } else {
        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
            counter.inc();
        }
        removed = prev.next;
        prev.next = removed.next;
        if (removed == tail) tail = prev;
    }

    size--;
    counter.inc();
    return removed.element;
}

@Override
public void clear() {
    head = tail = null;
    size = 0;
    counter.inc();
}
}