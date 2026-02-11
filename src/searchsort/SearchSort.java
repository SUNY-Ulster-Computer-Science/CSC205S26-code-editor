package searchsort;

import genericlist.GenericList;
import genericlist.StepCounter;

public class SearchSort {

    public static <E extends Comparable<E>>
    void bubbleSort(GenericList<E> list, StepCounter counter) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                counter.inc();
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    E right = list.remove(j + 1);
                    list.add(j, right);
                }
            }
        }
    }

    public static <E extends Comparable<E>>
    void selectionSort(GenericList<E> list, StepCounter counter) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                counter.inc();
                if (list.get(j).compareTo(list.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                E temp = list.get(minIndex);
                list.remove(minIndex);
                list.add(i, temp);
            }
        }
    }

    public static <E extends Comparable<E>>
    void insertionSort(GenericList<E> list, StepCounter counter) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            E key = list.get(i);
            int j = i - 1;
            while (j >= 0) {
                counter.inc();
                if (list.get(j).compareTo(key) > 0) {
                    E temp = list.remove(j);
                    list.add(j + 1, temp);
                } else break;
                j--;
            }
            E removed = list.remove(i);
            list.add(j + 1, removed);
        }
    }

    public static <E extends Comparable<E>>
    void mergeSort(GenericList<E> list, StepCounter counter) {
        if (list.size() <= 1) return;
        int mid = list.size() / 2;

        GenericList<E> left = list.newList();
        GenericList<E> right = list.newList();

        for (int i = 0; i < mid; i++) left.add(list.get(i));
        for (int i = mid; i < list.size(); i++) right.add(list.get(i));

        mergeSort(left, counter);
        mergeSort(right, counter);

        merge(left, right, list, counter);
    }

    private static <E extends Comparable<E>>
    void merge(GenericList<E> left, GenericList<E> right,
               GenericList<E> output, StepCounter counter) {

        output.clear();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            counter.inc();
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                output.add(left.get(i));
                i++;
            } else {
                output.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            output.add(left.get(i));
            i++;
        }

        while (j < right.size()) {
            output.add(right.get(j));
            j++;
        }
    }

    public static <E>
    int linearSearch(GenericList<E> list, E target, StepCounter counter) {
        for (int i = 0; i < list.size(); i++) {
            counter.inc();
            if (target == null) {
                if (list.get(i) == null) return i;
            } else if (target.equals(list.get(i))) return i;
        }
        return -1;
    }

    public static <E extends Comparable<E>>
    int binarySearch(GenericList<E> list, E target, StepCounter counter) {
        int low = 0, high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            counter.inc();
            int cmp = target.compareTo(list.get(mid));

            if (cmp == 0) return mid;
            else if (cmp < 0) high = mid - 1;
            else low = mid + 1;
        }
        return -1;
    }
}