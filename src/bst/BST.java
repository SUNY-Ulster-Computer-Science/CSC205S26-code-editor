package bst;

import genericlist.StepCounter;

/*
 * creates tree for binary search
 */
public class BST<E extends Comparable<E>> implements BSTInterface<E> {

	/*
	 * creates two nodes and inserts Element to compare
	 */
    private static class TreeNode<E> {
      /*represents element being searched for*/
    	E element;
      /*represents left branch*/
        TreeNode<E> left;
      /*represents right branch*/
        TreeNode<E> right;

        TreeNode(E e) {
            element = e;
            left = null;
            right = null;
        }
    }
    /*represents origin of search*/
    private TreeNode<E> root;
    /*represents starting index value*/
    private int size = 0;

    /*
     * inserts Element value into root
     * @param value - Element being searched
     * @return none
     */
    public void insert(E value) {
        root = insert(root, value, null);
    }

    /*
     * checks if value is contained in root
     * @param value - Element being searched
     * @return boolean
     */
    public boolean contains(E value) {
        return contains(root, value, null);
    }
    
    /*
     * checks size of TreeNode
     * @param none
     * @return int size
     */
    public int size() {
        return size;
    }
    
    /*
     * checks if TreeNode contains any Elements
     * @param none
     * @return boolean
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /*
     * inserts Element value into root at index counter
     * @param value - Element being inserted
     * @param - counter - stepcounter index value
     * @return none
     */
    public void insert(E value, StepCounter counter) {
        root = insert(root, value, counter);
    }
    
    /*
     * inserts Element value into node at index counter
     * @param node - node being inserted into
     * @param value - Element being inserted
     * @param - counter - stepcounter index value
     * @return none
     */
    private TreeNode<E> insert(TreeNode<E> node, E value, StepCounter counter) {
        if (node == null) {
            size++;
            return new TreeNode<>(value);
        }

        if (counter != null) counter.inc();

        int cmp = value.compareTo(node.element);
        counter.inc();

        if (cmp < 0)
            node.left = insert(node.left, value, counter);
        else if (cmp > 0)
            node.right = insert(node.right, value, counter);

        return node;
    }

    /*
     * checks if value is contained at counter
     * @param value - Element being searched
     * @param counter - stepcounter index value
     * @return boolean
     */
    public boolean contains(E value, StepCounter counter) {
        return contains(root, value, counter);
    }
    /*
     * checks if value is contained at counter in node
     * @param node - TreeNode being searched
     * @param value - Element being searched for
     * @param counter - stepcounter index value
     * @return boolean
     */
    private boolean contains(TreeNode<E> node, E value, StepCounter counter) {
        if (node == null) return false;

        if (counter != null) counter.inc();

        int cmp = value.compareTo(node.element);
        counter.inc();

        if (cmp == 0) return true;
        else if (cmp < 0) return contains(node.left, value, counter);
        else return contains(node.right, value, counter);
    }
}
