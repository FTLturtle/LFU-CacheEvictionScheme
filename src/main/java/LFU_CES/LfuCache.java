package LFU_CES;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


public class LfuCache<KeyType, DataType> {
    // Instance Fields
    private Map<KeyType, Item> mapCache; // Map containing data elements mapped to keys. Called bykey in LFU paper
    private FreqNode headFreqNode; // Head frequency node in frequency linked list. Called freq_head in LFU paper
    private final int capacity; // maximum capacity of cache
    private final int evictNumber; // number of Items to evict if cache is at capacity and an additional item is being inserted

    // Constructors

    /**
     * Constructs an empty cache with the specified maximum capacity and evict factor.
     *
     * @param capacity sets maximum number of entries the cache will hold
     * @param evictFactor the ratio, from 0 to 1, between the capacity and the number of items evicted when capacity is reached
     */
    public LfuCache(int capacity, double evictFactor) {
        if (capacity <= 0 || evictFactor <= 0 || evictFactor > 1) {
            throw new IllegalArgumentException("Capacity or eviction factor is invalid");
        }
        this.mapCache = new HashMap<>();
        this.headFreqNode = getHeadFreqNode();
        this.capacity = capacity;
        this.evictNumber = Math.min(capacity, (int) Math.ceil(capacity * evictFactor));
        // Based on the above formula, the minimum evictNumber is 1, and the maximum evictNumber is the capacity.
    }

    /**
     * Constructs an empty cache with the specified maximum capacity and the default evict factor (0.05).
     *
     * @param capacity the maximum number of entries the cache will hold
     */
    public LfuCache(int capacity) {
        this(capacity, 0.05);
    }

    // Public Methods

    /**
     * Accesses (fetches) a data element from the LFU cache, simultaneously incrementing its frequency.
     * Returns the data element to which the specified key is mapped, or throws a RuntimeException if the cache does not
     * contain a mapping for the key
     *
     * @param key the key whose associated value is to be returned
     * @return the data element to which the specified key is mapped
     */
    public synchronized DataType access(KeyType key) {
        Item item = mapCache.get(key);
        if (item == null) {
            throw new RuntimeException("No such key");
        }
        FreqNode parentNode = item.parent;
        FreqNode nextFreqNode = parentNode.next;

        if (nextFreqNode.equals(headFreqNode) || nextFreqNode.frequency != parentNode.frequency + 1) {
            nextFreqNode = getNewFreqNode(parentNode.frequency + 1, parentNode, nextFreqNode);
        }
        nextFreqNode.items.add(key);
        item.parent = nextFreqNode;

        parentNode.items.remove(key);
        if (parentNode.items.size() == 0) {
            deleteNode(parentNode);
        }

        return item.data;
    }

    /**
     * Inserts a new data element into the LFU cache, initializing its frequency to 1.
     * Associates the specified data element with the specified key in the cache. If the cache previously contained a
     * mapping for the key, a RuntimeException is thrown.
     *
     * @param key key with which the data element is to be associated
     * @param data data element to be associated with the specified key
     */
    public synchronized void insert(KeyType key, DataType data) {
        if (mapCache.containsKey(key)) {
            throw new RuntimeException("Key already exists");
        }

        if (mapCache.size() >= capacity) {
            evictLfuItems();
        }

        FreqNode firstFreqNode = headFreqNode.next;
        if (firstFreqNode.frequency != 1) {
            firstFreqNode = getNewFreqNode(1, headFreqNode, firstFreqNode);
        }

        firstFreqNode.items.add(key);
        mapCache.put(key, new Item(data, firstFreqNode));
    }

    /**
     * Fetches an item with the least usage count (the least frequently used item) in the cache
     *
     * @return the least frequently used data element in the cache
     */
    public synchronized DataType getLfuData() {
        if (mapCache.size() == 0) {
            throw new RuntimeException("The set is empty");
        }

        return mapCache.get(headFreqNode.next.items.iterator().next()).data;
    }

    // Public Getter Methods

    /**
     * Returns the current size (i.e., number of items) in the cache
     *
     * @return the current size (i.e., number of data elements) in the cache
     */
    public int size() {
        return mapCache.size();
    }

    /**
     * Returns the maximum capacity of the cache
     *
     * @return the maximum capacity of the cache
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the number of items that get evicted when the evict method gets called
     *
     * @return the number of items that get evicted when the evict method gets called
     */
    public int getEvictNumber() {
        return evictNumber;
    }

    // Private Helper Methods

    /**
     * Creates and returns a new frequency node with an access frequency value of 0 (zero), and whose prev and next fields point to
     * itself. This node will act as the head of the frequency node list.
     *
     * @return a new frequency node with an access frequency value of 0 (zero), and whose prev and next fields point to
     * itself.
     */
    private FreqNode getHeadFreqNode() {
        FreqNode headFreqNode = new FreqNode(0);
        headFreqNode.prev = headFreqNode;
        headFreqNode.next = headFreqNode;

        return headFreqNode;
    }

    /**
     * Creates and returns a new frequency node with the specified frequency and sets its previous and next pointers to the
     * specified prev and next nodes.
     *
     * @param frequency frequency of the new node being created
     * @param prev previous node to the node being created
     * @param next next node to the node being created
     * @return a new frequency node with the specified frequency and sets its previous and next pointers to the
     * specified prev and next nodes
     */
    private FreqNode getNewFreqNode(int frequency, FreqNode prev, FreqNode next) {
        FreqNode freqNode = new FreqNode(frequency);
        freqNode.prev = prev;
        freqNode.next = next;
        prev.next = freqNode;
        next.prev = freqNode;

        return freqNode;
    }

    /**
     * Removes (unlinks) the specified node from the frequency linked list.
     * @param freqNode node to be removed (unlinked)
     */
    private void deleteNode(FreqNode freqNode) {
        freqNode.prev.next = freqNode.next;
        freqNode.next.prev = freqNode.prev;
    }

    /**
     * Evicts the least frequently used items from the cache. The number of items removed is based on the evictNumber
     */
    private void evictLfuItems() {
        KeyType key;
        FreqNode lfuNode;
        for (int i = 0; i < evictNumber; i++) {
            lfuNode = headFreqNode.next;
            key = lfuNode.items.iterator().next();
            mapCache.remove(key);
            headFreqNode.next.items.remove(key);

            if (lfuNode.items.size() == 0) {
                deleteNode(lfuNode);
            }
        }
    }

    // Private Inner Classes

    /**
     * Private Inner Class FreqNode.
     *
     * This frequency node will hold a frequency value, and holds a set of all the items
     * that have been accessed a number of times equal to the frequency value.
     */
    private class FreqNode {
        int frequency;
        FreqNode prev;
        FreqNode next;
        Set<KeyType> items;

        FreqNode(int frequency) {
            this.frequency = frequency;
            items = new HashSet<>();
        }
    }

    /**
     * Private Inner Class Item.
     *
     * This object holds a data element, and points to the frequency node that holds the
     * frequency value equal to the number of times the data element has been accessed.
     */
    private class Item {
        DataType data;
        FreqNode parent;

        Item(DataType data, FreqNode parent) {
            this.data = data;
            this.parent = parent;
        }
    }

}