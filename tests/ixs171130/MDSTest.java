package ixs171130;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MDSTest {
    private MDS store;
    private List<Long> l;

    @BeforeEach
    void setUp() {
        store = new MDS();
        l = new LinkedList<>();
    }

    @Test
    void insert() {
        l.add(100L);
        l.add(200L);
        l.add(300L);

        assertEquals(1, store.insert(1, new MDS.Money(10, 20), l));

        l.clear();
        l.add(120L);
        l.add(430L);
        assertEquals(1, store.insert(2, new MDS.Money(24, 30), l));

        l.clear();
        l.add(120L);
        l.add(430L);
        l.add(340L);
        MDS.Product p = new MDS.Product(2, new MDS.Money(30, 20), l);
        assertEquals(0, store.insert(2, new MDS.Money(30, 20), l));
        assertEquals(new MDS.Money(30, 20), store.find(2));
        System.out.println("Input " + Arrays.toString(l.toArray()));
        HashSet<Long> descs = store.findProduct(2).description;
        for (long desc : l) {
            assertEquals(true, descs.contains(desc));
            assertEquals(true, store.descriptionHasProduct(p, desc));
        }
        System.out.println("Result : " +  Arrays.toString(descs.toArray()));

        l.remove(340L);
        l.add(350L);
        p = new MDS.Product(2, new MDS.Money(30, 20), l);
        assertEquals(0, store.insert(2, new MDS.Money(30, 20), l));
        assertEquals(new MDS.Money(30, 20), store.find(2));
        System.out.println("Input " + Arrays.toString(l.toArray()));
        descs = store.findProduct(2).description;
        for (long desc : l) {
            assertEquals(true, descs.contains(desc));
            assertEquals(true, store.descriptionHasProduct(p, desc));
        }
        System.out.println("Result : " +  Arrays.toString(descs.toArray()));

    }

    @Test
    void find() {
        l.add(100L);
        l.add(200L);

        assertEquals(1, store.insert(1, new MDS.Money(10, 20), l));

        assertEquals(new MDS.Money(10, 20), store.find(1));
        assertEquals(new MDS.Money(), store.find(2));  // this product does not exist
    }

    @Test
    void delete() {
        l.add(200L);
        l.add(500L);
        l.add(400L);

        store.insert(1, new MDS.Money(23, 12), l);

        assertEquals(0L, store.delete(2));
        assertEquals(1100L, store.delete(1));
    }

    @Test
    void removeNames() {
        l.add(475L);
        l.add(475L);
        l.add(210L);
        l.add(125L);
        l.add(600L);

        List<Long> removeList = new LinkedList<>();
        removeList.add(475L);
        removeList.add(200L);
        removeList.add(100L);

        store.insert(1, new MDS.Money(10, 20), l);

        assertEquals(0L, store.removeNames(2, removeList));
        assertEquals(475L, store.removeNames(1, removeList));

        store.insert(1, new MDS.Money(10, 50), l);
        removeList.add(125L);
        assertEquals(600L, store.removeNames(1, removeList));
    }

    @Test
    void findMin() {
        l.add(100L);
        l.add(467L);
        l.add(200L);

        store.insert(1, new MDS.Money(10, 20), l);
        store.insert(2, new MDS.Money(20, 10), l);
        store.insert(3, new MDS.Money(12, 20), l);

        assertEquals("10.20", store.findMinPrice(100).toString());
        assertEquals("0.0", store.findMinPrice(213).toString());

        // make sure additional/removal doesn't affect these operations
        l.clear();
        l.add(100L);
        l.add(125L);

        store.insert(4, new MDS.Money(5, 10), l);

        l.add(130L);
        store.insert(5, new MDS.Money(7, 10), l);

        assertEquals("5.10", store.findMinPrice(100).toString());
        assertEquals("5.10", store.findMinPrice(125).toString());
        assertEquals("7.10", store.findMinPrice(130).toString());


        store.delete(5);
        assertEquals("0.0", store.findMinPrice(130).toString());
        System.out.println("Min price " + store.findMinPrice(100));

        store.delete(4);
        assertEquals("10.20", store.findMinPrice(100).toString());
    }

    @Test
    void findMax() {
        l.add(100L);
        l.add(200L);
        l.add(300L);

        store.insert(1, new MDS.Money(10, 50), l);

        l.add(400L);
        store.insert(2, new MDS.Money(5, 49), l);

        l.remove(200L);
        store.insert(3, new MDS.Money(19, 99), l);

        l.clear();
        l.add(500L);
        l.add(600L);
        store.insert(4, new MDS.Money(29, 99), l);

        l.add(700L);
        store.insert(5, new MDS.Money(9, 29), l);

        assertEquals("19.99", store.findMaxPrice(400).toString());
        assertEquals("10.50", store.findMaxPrice(200).toString());

        l.clear();
        l.add(100L);
        l.add(700L);
        store.insert(6, new MDS.Money(99, 99), l);

        assertEquals("99.99", store.findMaxPrice(100).toString());
        assertEquals("99.99", store.findMaxPrice(700).toString());

        store.delete(6);
        assertEquals("19.99", store.findMaxPrice(100).toString());
        assertEquals("9.29", store.findMaxPrice(700).toString());

        assertEquals("0.0", store.findMaxPrice(1000).toString());
    }

    @Test
    void findPriceRange() {
        l.add(100L);
        l.add(200L);
        l.add(300L);

        MDS.Money lower, upper;
        lower = new MDS.Money(0, 0);
        upper = new MDS.Money(100, 0);

        store.insert(1, new MDS.Money(0, 99), l);
        assertEquals(1, store.findPriceRange(100, lower, upper));

        l.add(400L);
        store.insert(2, new MDS.Money(1, 99), l);
        assertEquals(2, store.findPriceRange(100, lower, upper));

        assertEquals(0, store.findPriceRange(500, lower, upper));

        l.add(500L);
        store.insert(3, new MDS.Money(2, 99), l);

        assertEquals(1, store.findPriceRange(500, lower, upper));

        l.remove(400L);
        l.remove(300L);
        store.insert(4, new MDS.Money(3, 99), l);

        assertEquals(4, store.findPriceRange(100, lower, upper));
        assertEquals(3, store.findPriceRange(300, lower, upper));

        store.delete(1);
        assertEquals(3, store.findPriceRange(100, lower, upper));
        assertEquals(2, store.findPriceRange(300, lower, upper));

        l.clear();
        l.add(1000L);
        l.add(2000L);
        store.insert(5, new MDS.Money(5, 99), l);
        assertEquals(1, store.findPriceRange(1000, lower, upper));

        store.insert(6, new MDS.Money(199, 99), l);
        assertEquals(1, store.findPriceRange(1000, lower, upper));

        l.clear();
        l.add(100L);
        store.removeNames(2, l);
        assertEquals(2, store.findPriceRange(100, lower, upper));
    }

    @Test
    void priceHike() {
        l.add(100L);
        l.add(200L);
        l.add(300L);

        store.insert(1, new MDS.Money(10, 0), l);

        l.add(400L);
        store.insert(2, new MDS.Money(20, 0), l);

        l.clear();
        l.add(500L);
        l.add(600L);
        store.insert(3, new MDS.Money(30, 0), l);

        assertEquals("6.0", store.priceHike(1L, 3L, 10.00).toString());
        assertEquals("11.0", store.find(1).toString());
        assertEquals("22.0", store.find(2).toString());
        assertEquals("33.0", store.find(3).toString());

        assertEquals("6.60", store.priceHike(1L, 2L, 20.00).toString());
        assertEquals("13.20", store.find(1).toString());
        assertEquals("26.40", store.find(2).toString());
        assertEquals("33.0", store.find(3).toString());
    }
}