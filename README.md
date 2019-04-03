This project is done in Java, based on the scheme outlined in the paper below.

### An O(1) algorithm for implementing the LFU cache eviction scheme
#### Prof. Ketan Shah Anirban Mitra Dhruv Matani August 16, 2010

### Abstract

Cache eviction algorithms are used widely in operating systems, databases and other systems that use caches to speed up execution by caching data that is used by the application. 
There are many policies such as MRU (Most Recently Used), MFU (Most Frequently Used), LRU (Least Recently Used) and LFU (Least Frequently Used) which each have their advantages and drawbacks and are hence used in specific scenarios. 
By far, the most widely used algorithm is LRU, both for its O(1) speed of operation as well as its close resemblance to the kind of behaviour that is expected by most applications. The LFU algorithm also has behaviour desirable by many real world workloads. However, in many places, the LRU algorithm is is preferred over the LFU algorithm because of its lower run time complexity of O(1) versus O(logn). 
We present here an LFU cache eviction algorithm that has a runtime complexity of O(1) for all of its operations, which include insertion, access and deletion(eviction).
