package LFU_CES;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

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

    @Test
    public void accessNonExistentDataTest() {
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

    @Test
    public void insertTest() {
    }

    @Test
    public void getLfuItemTest() {
    }


}