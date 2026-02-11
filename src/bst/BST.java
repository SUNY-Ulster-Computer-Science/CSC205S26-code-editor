package bst;

import genericlist.StepCounter;

public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    private static class TreeNode<E> {
        E element;
        TreeNode<E> left;
        TreeNode<E> right;

        TreeNode(E e) {
            element = e;
            left = null;
            right = null;
        }
    }

    private TreeNode<E> root;
    private int size = 0;

    public void insert(E value) {
        root = insert(root, value, null);
    }

    public boolean contains(E value) {
        return contains(root, value, null);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(E value, StepCounter counter) {
        root = insert(root, value, counter);
    }

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

    public boolean contains(E value, StepCounter counter) {
        return contains(root, value, counter);
    }

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
