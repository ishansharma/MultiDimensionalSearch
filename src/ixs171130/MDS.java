package ixs171130;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;

/**
 * Class for Multi-Dimensional Search
 */
public class MDS {
    private IdIndex<Long, Product> idIndex;
    private DescriptionIndex descriptionIndex;
    private Money zeroMoney;
    // static variables for priceHike so we don't have to declare again and again
    private static BigDecimal one = new BigDecimal(1), hundred = new BigDecimal(100);

    /**
     * Default constructor. Just initializes the indices
     */
    public MDS() {
        idIndex = new IdIndex<>();
        descriptionIndex = new DescriptionIndex();
        zeroMoney = new Money();
    }

    /**
     * Insert a new item
     *
     * @param id    ID of the item. If item with this id is not present, insert item. Else, replace the price and description.
     * @param price Price for the object
     * @param list  Description - This is a list that needs to be case to LinkedList for internal use
     * @return 1 if ID is new. 0 if ID is old and has been updated
     */
    public int insert(long id, Money price, java.util.List<Long> list) {
        Product p;
        if (idIndex.containsKey(id)) {
            p = idIndex.get(id);
            // TODO: Look for a more efficient approach here

            if (!price.equals(p.price)) {
                descriptionIndex.delete(p);
                p.updatePriceAndDescription(price, list);
                descriptionIndex.add(p);
                return 0;
            }
            /**
             * Need to find the diff between prev and new and delete only diff and update only diff
             */
            diffBetweenOldAndNewDescription(p, p.description, list);
            p.updatePriceAndDescription(price, list);
            return 0;

        } else {
            p = new Product(id, price, list);
            idIndex.put(id, p);
            descriptionIndex.add(p);
            return 1;
        }
    }

    /**
     * Need to find the diff between prev description list and new description list
     * Delete from description Index old products not in new
     * Add in Description Index new products
     * @param p Product
     * @param oldSet old description set
     * @param newList new description set
     */
    public void diffBetweenOldAndNewDescription(Product p, HashSet<Long> oldSet, List<Long> newList) {
        List<Long> deleteList = new LinkedList<>();
        List<Long> addList = new LinkedList<>();
        for (Long in : newList) {
            if (!oldSet.contains(in)) {
                addList.add(in);
            }
            else {
                oldSet.remove(in);
            }
        }
        deleteList.addAll(oldSet);

        for (Long desc: deleteList) {
            descriptionIndex.deleteProductForAWord(p, desc);
        }

        for (Long desc : addList) {
            descriptionIndex.addProductForAWord(p, desc);
        }
    }

    /**
     * Return price of item with given id. If not found, returns 0
     *
     * @param id ID to search for
     * @return Money object which is either price of the object or 0
     */
    public Money find(long id) {
        Product p = idIndex.get(id);

        if (p != null) {
            return p.price;
        }

        return new Money();
    }

    public Product findProduct(long id) {
        return idIndex.get(id);
    }

    /**
     * Delete item from storage. Returns the sum of long ints that are in the description of the deleted item.
     * Returns 0 if id doesn't exist
     *
     * @param id ID of item to delete
     * @return Sum of long ints in the deleted item
     */
    public long delete(long id) {
        Product p = idIndex.get(id);

        if (p == null) {
            return 0;
        }

        long sum = 0;
        for (long num :
                p.description) {
            sum += num;
        }
        descriptionIndex.delete(p);
        idIndex.remove(id);
        return sum;

    }

    /**
     * Give a long int, find items whose description contains that number, return lowest price of those items
     *
     * @param n ID to search for
     * @return The minimum price of products with given n in description
     */
    public Money findMinPrice(long n) {
        Product p = descriptionIndex.findMin(n);

        if (p == null) {
            return zeroMoney;
        }

        return p.price;
    }

    /**
     * Given a long int, find items whose description contains that number, return highest price of those items
     *
     * @param n ID to search for
     * @return The maximum price of products with given n in description
     */
    public Money findMaxPrice(long n) {
        Product p = descriptionIndex.findMax(n);

        if (p == null) {
            return zeroMoney;
        }

        return p.price;
    }

    /**
     * Find number of products containing a number (n) in the description and where range is between low and high
     *
     * @param n    Description word to search for
     * @param low  lower bound
     * @param high upper bound
     * @return Number of products
     */
    public int findPriceRange(long n, Money low, Money high) {
        if (low.compareTo(high) > 0) {
            return 0;
        }
        return descriptionIndex.findPriceRange(n, low, high);
    }

    /**
     * Increase price of every product with ID between l and h by r%
     *
     * @param l    Lowest ID
     * @param h    Highest ID
     * @param rate Percentage Increase
     * @return Sum of total price hike
     */
    public Money priceHike(long l, long h, double rate) {
        if (l > h || Math.abs(rate) <= 0.001) {
            return zeroMoney;
        }
        NavigableMap<Long, Product> n = idIndex.subMap(l, true, h, true);

        if (n == null || n.size() == 0) {
            return zeroMoney;
        }

        BigDecimal newPrice, oldPrice, rateIncrease, totalIncrease;
        rateIncrease = new BigDecimal(rate);
        totalIncrease = new BigDecimal(0);

        for (Product p : n.values()) {
            oldPrice = new BigDecimal(p.price.dollars() * 100 + p.price.cents()).divide(hundred).setScale(2, RoundingMode.DOWN);
            newPrice = oldPrice.multiply(rateIncrease.divide(hundred).add(one)).setScale(2, RoundingMode.DOWN);
            p.price = new Money(newPrice.toString());
            totalIncrease = totalIncrease.add(newPrice.subtract(oldPrice));
        }

        return new Money(totalIncrease.toString());
    }

    /**
     * Given an id and a list, remove the elements of list from product's description
     *
     * @param id   ID of product
     * @param list List of longs to remove
     * @return Sum of longs removed
     */
    public long removeNames(long id, java.util.List<Long> list) {
        long res = 0;
        Product p = idIndex.get(id);
        if (p == null) {
            return res;
        }

        for (Long n : list) {
            if (p.description.remove(n)) {
                res += n;
                descriptionIndex.deleteProductForAWord(p, n);
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
        static final Money zeroMoney = new Money(0, 0);

        /**
         * Constructor with only ID
         *
         * @param id ID of the product
         */
        public Product(long id) {
            this.id = id;
            this.price = zeroMoney;
            this.description = new HashSet<>();
        }

        /**
         * Constructor without description list
         *
         * @param id    ID of the product.
         * @param price Money object containing the price
         */
        public Product(long id, Money price) {
            this.id = id;
            this.price = price;
            this.description = new HashSet<>();
        }

        /**
         * Constructor for Products
         *
         * @param id    ID of the product. We are assuming that this will be unique.
         * @param price Money object containing the price
         * @param desc  An array of long values, can be of arbitrary length
         */
        public Product(long id, Money price, List<Long> desc) {
            this.id = id;
            this.price = price;
            this.description = new HashSet<>();
            description.addAll(desc);
        }

        /**
         * Update price and description of product
         * If description is null or list of length 0, don't update
         *
         * @param price New price
         * @param desc  New description
         */
        public void updatePriceAndDescription(Money price, List<Long> desc) {
            this.price = price;

            if (desc != null && desc.size() > 0) {
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

        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", price=" + price +
                    ", description=" + description +
                    ", zeroMoney=" + zeroMoney +
                    '}';
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
            if (this.d == o.d) {
                if (this.c == o.c) {
                    return 0;
                } else if (this.c < o.c) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if (this.d > o.d) {
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

        public boolean equals(Long o) {
            return this.toLong() == o;
        }

        public String toString() {
            return d + "." + c;
        }

        public Long toLong() {
            return d * 100 + c;
        }
    }

}