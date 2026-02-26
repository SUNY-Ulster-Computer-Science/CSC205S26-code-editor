package arraylist;

/*
 * simple methods for ListQueue
 */
public interface Queue<E> {
	
	/* adds element to queue
	 * @param e - Element
	 * @return none
	 */
    void enqueue(E e);
    
    /* removes element from queue
     * @param none
	 * @return Element
	 */
    E dequeue();
    
    /* gets last element in queue
	 * @param none
	 * @return Element
	 */
    E peek();
    
    /* checks to see if queue contains Elements
	 * @param none
	 * @return Boolean
	 */
    boolean isEmpty();
    
    /* checks size of queue
	 * @param none
	 * @return int size - size of queue
	 */
    int size();
}

