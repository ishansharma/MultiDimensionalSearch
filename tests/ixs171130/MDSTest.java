package ixs171130;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MDSTest {
    @Test
    void insert() {
        MDS store = new MDS();
        List<Long> l = new LinkedList<>();

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
}