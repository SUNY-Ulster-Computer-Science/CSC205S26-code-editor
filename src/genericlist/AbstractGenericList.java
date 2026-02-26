package genericlist;

/*
 * Creates abstract class from GenericList
 */
public abstract class AbstractGenericList<E> implements GenericList<E> {

	/*represents size of List*/
    protected int size;
    /*represents location within the list*/
    protected StepCounter counter;
    
    /*
     * Creates protected list with StepCounter as the element
     * @param counter
     */
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
    /*
     * checks Element at index
     * @param index - location of Element
     * @return none
     * @throws IndexOutOfBoundsException
     */
    protected void checkElementIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        counter.inc();
    }

    /*
     * checks Position  of index
     * @param index - location of Position
     * @return none
     * @throws IndexOutOfBoundsException
     */
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
