package genericlist;

public abstract class AbstractGenericList<E> implements GenericList<E> {

    protected int size;
    protected StepCounter counter;
    
    protected AbstractGenericList(StepCounter counter) {
        this.counter = counter;
        this.size = 0; }

    @Override
    public int size() { 
    	counter.inc();
    	return size; }

    @Override
    public boolean isEmpty() { 
    	counter.inc();
    	return size == 0; }

    protected void checkElementIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        counter.inc();
    }

    protected void checkPositionIndex(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        counter.inc();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size * 4 + 2);
        sb.append('[');
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(get(i));
        }
        sb.append(']');
        return sb.toString();
    }
}
