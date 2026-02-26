package genericlist;
/*
 * Provides simple list methods
 */
public interface GenericList<E> {
/*
 * adds Element to list
 * @param element E
 * @returns none
 */
void add(E element);

/*
 *  adds Element to list at index
 *  @param element E
 *  @param int index - location of insert
 *  @return none
 */
void add(int index, E element);

/*
 * returns Element at index
 * @param index - location of Element
 * @return Element 
 */
E get(int index);

/*
 * removes Element at index
 * @param index - location of Element
 * @returns Element
 */
E remove(int index);

/*
 * return index of Element
 * @param element E - desired Element
 * @returns int index - location of Element
 */
int contains(E element); // index of first match, or -1

/*
 * size of list
 * @param none
 * @return int - size of list
 */
int size();

/* 
 * checks if list contains Elements
 * @param none
 * @return boolean isEmpty
 */
boolean isEmpty();

/*
 * clears list
 * @param none
 * @return none
 */
void clear();

@Override String toString();

/*
 * constructs new list
 * @param none
 * @return none 
 */
GenericList<E> newList();
}