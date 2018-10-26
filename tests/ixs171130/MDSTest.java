package ixs171130;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        l.add(340L);
        assertEquals(0, store.insert(2, new MDS.Money(30, 20), l));
        assertEquals(new MDS.Money(30, 20), store.find(2));
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
}