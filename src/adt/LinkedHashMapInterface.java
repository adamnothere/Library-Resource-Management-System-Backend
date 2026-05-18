package adt;

import java.util.Comparator;

public interface LinkedHashMapInterface<K, V> {
    // Map Operations
    void put(K key, V value);
    V get(K key);
    V remove(K key);
    boolean containsKey(K key);
    int size();
    boolean isEmpty();
    void clear();

    // Sequence/Order Operations
    V dequeue();
    V peek();
    V getLatest();
    boolean undo();

    // Presentation Operations
    Object[] toArray();
    void printInOrder();
    void mergeSort(V[] arr, int left, int right, Comparator<? super V> comparator);
}