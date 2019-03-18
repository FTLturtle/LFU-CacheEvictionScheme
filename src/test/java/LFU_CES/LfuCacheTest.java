package LFU_CES;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class LfuCacheTest {

    @Test
    public void constructor1CapacityTest() {
        // Given
        int expectedCapacity = 20;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(expectedCapacity);

        // When
        int actualCapacity = lfuCache.getCapacity();

        // Then
        Assert.assertEquals(expectedCapacity, actualCapacity);
    }

    @Test
    public void constructor1InitialSizeTest() {
        // Given
        LfuCache<Integer, String> lfuCache = new LfuCache<>(1);

        // When
        int initialSize = lfuCache.size();

        // Then
        Assert.assertEquals(0, initialSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor1NegativeCapacityTest() {
        // Given
        int capacity = -1;

        // When
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity);
    }

    @Test
    public void constructor2CapacityTest() {
        // Given
        int expectedCapacity = 20;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(expectedCapacity, 0.5);

        // When
        int actualCapacity = lfuCache.getCapacity();

        // Then
        Assert.assertEquals(expectedCapacity, actualCapacity);
    }

    @Test
    public void constructor2InitialSizeTest() {
        // Given
        LfuCache<String, CharSequence> lfuCache = new LfuCache<>(1, 0.5);

        // When
        int initialSize = lfuCache.size();

        // Then
        Assert.assertEquals(0, initialSize);
    }

    @Test
    public void constructor2EvictNumberTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        int expectedEvictNumber = (int) Math.ceil(capacity * evictFactor);
        LfuCache<Byte, Short> lfuCache = new LfuCache<>(capacity, evictFactor);

        // When
        int actualEvictNumber = lfuCache.getEvictNumber();

        // Then
        Assert.assertEquals(expectedEvictNumber, actualEvictNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2NegativeCapacityTest() {
        // Given
        int capacity = -1;
        double evictFactor = 0.1;

        // When
        LfuCache<Short, HashMap> lfuCache = new LfuCache<>(capacity, evictFactor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2NegativeEvictFactorTest() {
        // Given
        int capacity = 40;
        double evictFactor = -0.1;

        // When
        LfuCache<Long, Date> lfuCache = new LfuCache<>(capacity, evictFactor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2EvictFactorAbove1Test() {
        // Given
        int capacity = 40;
        double evictFactor = 1.1;

        // When
        LfuCache<String, Integer> lfuCache = new LfuCache<>(capacity, evictFactor);
    }

    @Test
    public void accessInsertedDataTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        Integer key = 0;
        String insertedData = "Test String";
        lfuCache.insert(key, insertedData);

        // When
        String accessedData = lfuCache.access(0);

        // Then
        Assert.assertEquals(insertedData, accessedData);
    }

    @Test(expected = RuntimeException.class)
    public void accessNonExistentDataTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);

        // When
        String accessedData = lfuCache.access(8475692);
    }

    @Test
    public void sizeAfterInsertTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        Integer key = 0;
        String insertedData = "Test String";

        // When
        lfuCache.insert(key, insertedData);

        // Then
        int sizeAfterInsertion = lfuCache.size();
        Assert.assertEquals(1, sizeAfterInsertion);
    }

    @Test(expected = RuntimeException.class)
    public void insertUsingUnavailableKeyTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        Integer key = 0;
        String insertedData = "Test String";

        // When
        lfuCache.insert(key, insertedData);
        lfuCache.insert(key, insertedData);
    }

    @Test
    public void sizeAfterMultipleInsertTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);

        // When
        lfuCache.insert(0, "asdf");
        lfuCache.insert(1, "fdasdf");
        lfuCache.insert(2, "oiaushgiu");
        int sizeAfterInsertion = lfuCache.size();

        // Then
        Assert.assertEquals(3, sizeAfterInsertion);
    }

    @Test
    public void getLfuDataTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        String expectedData = "LeastFrequentlyUsedString";
        lfuCache.insert(0, expectedData);
        lfuCache.insert(1, "fdasdf");
        lfuCache.insert(2, "oiaushgiu");
        lfuCache.access(2);
        lfuCache.access(1);

        // When
        String lfu = lfuCache.getLfuData();

        // Then
        Assert.assertEquals(expectedData, lfu);
    }

    @Test(expected = RuntimeException.class)
    public void getLfuDataEmptyCacheTest() {
        // Given
        int capacity = 40;
        double evictFactor = 0.1;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);

        // When
        String lfu = lfuCache.getLfuData();
    }

    @Test
    public void sizeAfterEvictTest1() {
        // Given
        int capacity = 10;
        double evictFactor = 0.5;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        int expectedSize = 6; // after evict, size goes down to 5, after insert, size back up to 6

        // When
        for (int i = 0; i < capacity; i++) {
            lfuCache.insert(i, "test");
        }

        lfuCache.insert(10, "thisOneWillCauseAnEviction");

        // Then
        int sizeAfterEviction = lfuCache.size();
        Assert.assertEquals(expectedSize, sizeAfterEviction);
    }

    @Test
    public void sizeAfterEvictTest2() {
        // Given
        Random random = new Random();
        int capacity = 10;
        double evictFactor = 0.5;
        LfuCache<Integer, String> lfuCache = new LfuCache<>(capacity, evictFactor);
        int expectedSize = 6; // after evict, size goes down to 5, after insert, size back up to 6

        // When
        for (int i = 0; i < capacity; i++) {
            lfuCache.insert(i, "test");
            for (int j = 0; j < random.nextInt(5); j++) {
                lfuCache.access(i); // accessed a random number of times
            }
        }

        lfuCache.insert(10, "thisOneWillCauseAnEviction");

        // Then
        int sizeAfterEviction = lfuCache.size();
        Assert.assertEquals(expectedSize, sizeAfterEviction);
    }


}