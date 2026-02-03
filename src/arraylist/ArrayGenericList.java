package arraylist;

import genericlist.AbstractGenericList;
import genericlist.StepCounter;
import genericlist.GenericList;
import java.util.Arrays;

public class ArrayGenericList<E> extends AbstractGenericList<E> {

    private static final int defaultvalue = 10;
    private E[] elements;

    /** Default constructor */
    @SuppressWarnings("unchecked")
    public ArrayGenericList(StepCounter counter) {
        super(counter);
        elements = (E[]) new Object[defaultvalue];
    }

    @SuppressWarnings("unchecked")
    public ArrayGenericList(int capacity, StepCounter counter) {
    	super(counter);
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        elements = (E[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size >= elements.length) {
            int newCap = elements.length * 2;
            elements = Arrays.copyOf(elements, newCap);
            counter.add(newCap);
        }
    }

    @Override
    public void add(E e) {
        ensureCapacity();
        elements[size++] = e;
        counter.inc();
    }

    @Override
    public void add(int index, E e) {
        checkPositionIndex(index);
        ensureCapacity();
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
            counter.inc();
        }
        elements[index] = e;
        size++;
        counter.inc();
    }
    
    @Override
    public E get(int index) {
        checkElementIndex(index);
        counter.inc();
        return elements[index];
    }

  
    public E set(int index, E e) {
        checkElementIndex(index);
        E old = elements[index];
        elements[index] = e;
        counter.inc();
        return old;
    }

    
    @Override
    public E remove(int index) {
        checkElementIndex(index);
        E old = elements[index];
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
            counter.inc();
        }
        elements[--size] = null; // clear reference for GC
        counter.inc();
        return old;
       
    }
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        int i = contains((E) o); //unsafe type, resolved by suppressing warning.
        if (i >= 0) {
            remove(i);
            return true;
        }
        return false;
    }

    
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) elements[i] = null;
        counter.inc();
        size = 0;
    }
    
    @Override
    public int contains(E element) {  //returns index
        for (int i = 0; i < size; i++) {
            counter.inc();
            if (element == null ? elements[i] == null : element.equals(elements[i])) return i;
        }
        return -1;}
  
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
