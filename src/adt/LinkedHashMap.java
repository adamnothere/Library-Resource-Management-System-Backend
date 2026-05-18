package adt;

import java.util.Comparator;

/**
 * ADT Implementation: LinkedHashMap
 * * Description: 
 * A custom hybrid data structure combining a Hash Table for O(1) average-time complexity 
 * look-ups and a Doubly Linked List to maintain the insertion order of elements with Queue(FIFO)
 * and Stack(LIFO) behavior.
 * * Data Specifications:
 * - Table: Array of Node buckets using Chaining to handle hash collisions.
 * - Head/Tail: Pointers to the oldest and newest entries for Queue functionality.
 * - History: A stack-based pointer to store deleted nodes for Undo functionality.
 * @param <K> The type of keys (e.g., String BookID)
 * @param <V> The type of values (e.g., Book Object)
 */
public class LinkedHashMap<K, V> implements LinkedHashMapInterface<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;           // For hash collision chaining
        Node<K, V> before, after; // For doubly linked list ordering

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] table;
    private int capacity = 16;
    private int size = 0;

    private Node<K, V> head;    // Head of the insertion-order list
    private Node<K, V> tail;    // Tail of the insertion-order list
    private Node<K, V> history; // Head of the deleted nodes stack

    @SuppressWarnings("unchecked")
    public LinkedHashMap() {
        table = new Node[capacity];
    }

    /**
     * Generates a hash index for a given key within the table capacity.
     */
    private int hash(K key) {
        return Math.abs(key.hashCode() % capacity);
    }

    /**
     * Associates the specified value with the specified key.
     * If the key exists, the value is updated. If new, it is linked at the end.
     * 
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    @Override
    public void put(K key, V value) {
        int bucket = hash(key);
        Node<K, V> existing = table[bucket];

        while (existing != null) {
            if (existing.key.equals(key)) {
                existing.value = value;
                return;
            }
            existing = existing.next;
        }

        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[bucket];
        table[bucket] = newNode;

        linkAtEnd(newNode);
        size++;
    }

    /**
     * Appends a node to the end of the doubly linked list (chronological order).
     */
    private void linkAtEnd(Node<K, V> node) {
        if (head == null) {
            head = tail = node;
        } else {
            tail.after = node;
            node.before = tail;
            node.after = null; // Safety: new tail points to null
            tail = node;
        }
    }

    /**
     * Removes the entry for the specified key.
     * The removed node is pushed onto the history stack for undo support.
     * 
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V remove(K key) {
        int bucket = hash(key);
        Node<K, V> prev = null;
        Node<K, V> current = table[bucket];

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[bucket] = current.next;
                } else {
                    prev.next = current.next;
                }

                unlinkFromOrder(current);
                
                // Store in history (Stack LIFO logic)
                current.after = history;
                history = current;

                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    /**
     * Adjusts before/after pointers of neighboring nodes when a node is removed.
     */
    private void unlinkFromOrder(Node<K, V> node) {
        if (node.before != null) {
            node.before.after = node.after;
        } else {
            head = node.after;
        }

        if (node.after != null) {
            node.after.before = node.before;
        } else {
            tail = node.before;
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     * or null if this map contains no mapping for the key.
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if not found
     */
    @Override
    public V get(K key) {
        int bucket = hash(key);
        Node<K, V> current = table[bucket];
        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        return null;
    }

    /**
     * Removes and returns the oldest entry in the map, following 
     * First-In-First-Out (FIFO) order.
     *
     * @return the value of the oldest entry, or null if the map is empty
     */
    @Override
    public V dequeue() {
        if (head == null) return null;
        V oldestValue = head.value;
        remove(head.key);
        return oldestValue;
    }

    /**
     * Returns the oldest entry (head) without removing it (non-destructive peek).
     * 
     * @return the value of the first entry but not removing it.
     */
    @Override
    public V peek() {
        return (head == null) ? null : head.value;
    }

    /**
     * Restores the most recently removed entry to the map.
     * 
     * @return true if undo operation is valid and completed, otherwise return false 
     */
    @Override
    public boolean undo() {
        if (history == null) return false;

        Node<K, V> restoredNode = history;
        history = restoredNode.after; 

        int bucket = hash(restoredNode.key);
        restoredNode.next = table[bucket];
        table[bucket] = restoredNode;

        restoredNode.before = null;
        restoredNode.after = null;
        linkAtEnd(restoredNode);

        size++;
        return true;
    }

    /**
     * Returns the value of the most recently added entry.
     * 
     * @return the last entry in the LinkedHashMap (LIFO)
     */
    @Override
    public V getLatest() {
        return (tail == null) ? null : tail.value;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Clears all entries, pointers, and history.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        table = new Node[capacity];
        head = tail = history = null;
        size = 0;
    }
    
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() { 
        return size; 
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public boolean isEmpty() { 
        return size == 0; 
    }

    /**
     * Prints entries in their chronological insertion order.
     */
    @Override
    public void printInOrder() {
        Node<K, V> current = head;
        while (current != null) {
            System.out.print("[" + current.key + ": " + current.value + "] -> ");
            current = current.after;
        }
        System.out.println("null");
    }
    
    /**
     * Utility for traversing the map to print a formatted table
     */
    public void printTableFormat() {

        Node<K, V> current = head; 

        while (current != null) {

            System.out.println(current.value.toString()); 
            current = current.after; 

        }
    }

    //Merge Sort Algorithm
   /**
     * Returns an array containing all of the values in this map in 
     * the order they were inserted.
     *
     * @return an array containing all the values in this map
     */ 
    @Override
    public Object[] toArray() {
        Object[] copy = new Object[size];
        Node<K, V> current = head;
        int i = 0;
        while (current != null) {
            copy[i++] = current.value;
            current = current.after;
        }
        return copy;
    }

   /**
     * Sorts the specified array using the Merge Sort algorithm.
     * This implementation is stable and offers O(n log n) performance.
     *
     * @param arr        the array to be sorted
     * @param left       the index of the first element (usually 0)
     * @param right      the index of the last element (usually arr.length - 1)
     * @param comparator the comparator to determine the order of the elements
     * @param <V[]>        the type of elements in the array
     */
    @Override
    public void mergeSort(V[] arr, int left, int right, Comparator<? super V> comparator) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            mergeSort(arr, left, middle, comparator);
            mergeSort(arr, middle + 1, right, comparator);
            merge(arr, left, middle, right, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    private void merge(V[] arr, int left, int middle, int right, Comparator<? super V> comparator) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        Object[] L = new Object[n1];
        Object[] R = new Object[n2];

        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[middle + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (comparator.compare((V) L[i], (V) R[j]) <= 0) {
                arr[k++] = (V) L[i++];
            } else {
                arr[k++] = (V) R[j++];
            }
        }

        while (i < n1) arr[k++] = (V) L[i++];
        while (j < n2) arr[k++] = (V) R[j++];
    }
    
}

