package LFU_CES;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


public class LfuCache<KeyType, DataType> {
    // Instance Fields
    private Map<KeyType, Item> mapCache; // called bykey in pdf
    private FreqNode headFreqNode; // called freq_head in pdf
    private final int capacity; // maximum capacity of cache
    private final int evictNumber; // number of Items to evictLfuItems if cache is at capacity and an additional item is being inserted

    // Constructors

    /**
     * Constructor with eviction factor as a parameter (the eviction factor is the ratio between the capacity and the
     * number of items evicted when capacity is reached)
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
     * Constructor with eviction factor set to a default value of 0.05
     */
    public LfuCache(int capacity) {
        this(capacity, 0.05);
    }

    // Public Methods

    /**
     * Accesses (fetches) an element from the LFU cache, simultaneously incrementing its usage count
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
     * Inserts a new element into the LFU cache
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
     */
    public int size() {
        return mapCache.size();
    }

    /**
     * Returns the maximum capacity of the cache
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the number of items that get evicted when the evict method gets called
     */
    public int getEvictNumber() {
        return evictNumber;
    }

    // Private Helper Methods

    /**
     * Creates a new frequency node with an access frequency value of 0 (zero), and whose prev and next fields point to
     * itself. This node will act as the head of the frequency node list.
     */
    private FreqNode getHeadFreqNode() {
        FreqNode headFreqNode = new FreqNode(0);
        headFreqNode.prev = headFreqNode;
        headFreqNode.next = headFreqNode;

        return headFreqNode;
    }

    /**
     * Creates a new node and set its previous and next pointers to prev and next
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
     * Removes (unlinks) a node from the linked list
     */
    private void deleteNode(FreqNode freqNode) {
        freqNode.prev.next = freqNode.next;
        freqNode.next.prev = freqNode.prev;
    }

    /**
     * Evicts least frequently used items from the cache. The number of items removed is based on the evictNumber
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
     * Private Inner Class FreqNode
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
     * Private Inner Class Item
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