package ixs171130;

// If you want to create additional classes, place them in this file as subclasses of MDS

import java.util.*;

/**
 * Class for Multi-Dimensional Search
 */
public class MDS {
    private Map<Long, Product> idIndex;

    /**
     * Default constructor. Just initializes the indices
     */
    public MDS() {
        idIndex = new TreeMap<>();
    }

    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated.
       Returns 1 if the item is new, and 0 otherwise.
    */

    /**
     * Insert a new item
     * @param id ID of the item. If item with this id is not present, insert item. Else, replace the price and description.
     * @param price Price for the object
     * @param list Description - This is a list that needs to be case to LinkedList for insernal use
     * @return 1 if ID is new. 0 if ID is old and has been updated
     */
    public int insert(long id, Money price, java.util.List<Long> list) {
        Product p;
        if(idIndex.containsKey(id)) {
            p = idIndex.get(id);
            p.updatePriceAndDescription(price, (LinkedList<Long>) list);
            return 0;
        } else {
            p = new Product(id, price, (LinkedList<Long>) list);
            idIndex.put(id, p);
            return 1;
        }
    }

    /**
     * Return price of item with given id. If not found, returns 0
     * @param id ID to search for
     * @return Money object which is either price of the object or 0
     */
    public Money find(long id) {
        Product p = idIndex.get(id);

        if(p != null) {
            return p.price;
        }

        return new Money();
    }

    /*
       c. Delete(id): delete item from storage.  Returns the sum of the
       long ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */

    /**
     * Delete item from storage. Returns the sum of long ints that are in the description of the deleted item.
     * Returns 0 if id doesn't exist
     * @param id ID of item to delete
     * @return Sum of long ints in the deleted item
     */
    public long delete(long id) {
        Product p = idIndex.get(id);

        if(p == null) {
            return 0;
        }

        long sum = 0;
        for (long num :
                p.description) {
            sum += num;
        }

        return sum;

    }

    /*
       d. FindMinPrice(n): given a long int, find items whose description
       contains that number (exact match with one of the long ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMinPrice(long n) {
        return new Money();
    }

    /*
       e. FindMaxPrice(n): given a long int, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMaxPrice(long n) {
        return new Money();
    }

    /*
       f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */
    public int findPriceRange(long n, Money low, Money high) {
        return 0;
    }

    /*
       g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
       prices of items.  Returns the sum of the net increases of the prices.
    */
    public Money priceHike(long l, long h, double rate) {
        return new Money();
    }

    /**
     * Given an id and a list, remove the elements of list from product's description
     * @param id ID of product
     * @param list List of longs to remove
     * @return Sum of longs removed
     */
    public long removeNames(long id, java.util.List<Long> list) {
        long res = 0;
        Product p = idIndex.get(id);
        if(p == null) {
            return res;
        }

        for(Long n: list) {
            if(p.description.remove(n)) {
                res += n;
            }
        }
        return res;
    }

    /**
     * Class for products
     */
    public static class Product implements Comparable {
        final long id;  // making it final because ID should never be changed
        Money price;
        HashSet<Long> description;

        /**
         * Constructor for Products
         *
         * @param id ID of the product. We are assuming that this will be unique.
         * @param price Money object containing the price
         * @param desc An array of long values, can be of arbitrary length
         */
        public Product(long id, Money price, LinkedList<Long> desc) {
            this.id = id;
            this.price = price;
            this.description = new HashSet<>();
            description.addAll(desc);
        }

        /**
         * Update price and description of product
         * If description is null or list of length 0, don't update
         * @param price New price
         * @param desc New description
         */
        public void updatePriceAndDescription(Money price, LinkedList<Long> desc) {
            this.price = price;

            if(desc != null && desc.size() > 0) {
                this.description.clear();
                this.description.addAll(desc);
            }
        }

        /**
         * For comparison, we compare by ID
         *
         * @param o Other product
         * @return True if the IDs are equal, false otherwise
         */
        @Override
        public int compareTo(Object o) {
            Product p = (Product) o;
            return Long.compare(this.id, p.id);
        }
    }

    // Do not modify the Money class in a way that breaks LP3Driver.java
    public static class Money implements Comparable<Money> {
        long d;
        int c;

        public Money() {
            d = 0;
            c = 0;
        }

        public Money(long d, int c) {
            this.d = d;
            this.c = c;
        }

        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if (len < 1) {
                d = 0;
                c = 0;
            } else if (part.length == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
                d = Long.parseLong(part[0]);
                c = Integer.parseInt(part[1]);
            }
        }

        public long dollars() {
            return d;
        }

        public int cents() {
            return c;
        }

        @Override
        public int compareTo(Money o) {
            if(this.d == o.d) {
                if(this.c == o.c) {
                    return 0;
                } else if(this.c < o.d) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if(this.d > o.d) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            Money o = (Money) obj;
            return this.d == o.d && this.c == o.c;
        }

        public String toString() {
            return d + "." + c;
        }
    }

}