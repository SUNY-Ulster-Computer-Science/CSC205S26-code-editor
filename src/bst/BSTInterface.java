package bst;

/*
 * provides simple binary search methods
 */
public interface BSTInterface<E extends Comparable<E>> {
	
	/*
	 * inserts element to be compared
	 * @param value - compared element
	 * @returns none
	 */
    void insert(E value);
    /**
     * Checks to see if Element is contained
     * @param value - Element being checked
     * @return boolean
     */
    boolean contains(E value);
    
    /*
     * checks size
     * @param none
     * @return size
     */
    int size();

    /*
     * checks to see if any elements are contained
     * @param none
     * @return boolean
     */
    boolean isEmpty();
}
