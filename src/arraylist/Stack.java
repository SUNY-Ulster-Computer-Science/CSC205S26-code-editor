package arraylist;

/*
 * simple methods for ListStack
 */
public interface Stack<E> {
	/* adds element to stack
	 * @param e - Element
	 * @return none
	 */
    void push(E e);
    
	/*removes element from stack
	 *@param none
	 *@return element E
	 *@throws IllegalStateException 
	 */
    E pop();
    
	/* gets last element in stack
	 * @param none
	 * @return element E
	 * @throws IllegalStateException
	 */
    E peek();
    
	/*checks to see if Stack is empty
	 * @param none 
	 * @return boolean
	 */
    boolean isEmpty();
    
	/*checks size of stack
	 * @param none
	 * @return int size - size of stack
	 */
    int size();
}