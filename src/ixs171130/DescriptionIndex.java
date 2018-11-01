package ixs171130;

import java.util.*;

/**
 * Class to hold the description index
 */
class DescriptionIndex {

    //Map<description, Map<money, set<product>>>
    private Map<Long, TreeMap<MDS.Money, HashSet<MDS.Product>>> descriptionIndex = new HashMap<>();
    private TreeMap<MDS.Money, HashSet<MDS.Product>> t;

    /**
     * Add a product with given description to our index
     *
     * @param p Product to add to the index
     */
    public void add(MDS.Product p) {
        HashSet<MDS.Product> set;
        for (Long desc : p.description) {
            t = descriptionIndex.get(desc);
            if (t == null) {  // this we are seeing this description for the first time
                t = new TreeMap<>();
                set = new HashSet<>();
                set.add(p);
                t.put(p.price, set);
                descriptionIndex.put(desc, t);

            } else {
                //TO DO : Optimize the way of adding
                set = t.getOrDefault(p.price, new HashSet<>());
                set.add(p);
                t.put(p.price, set);
            }
        }
    }


    /**
     * Add the product from index for a specific word
     *
     * @param p               Product to add
     * @param descriptionWord Word for which to add
     */
    public void addProductForAWord(MDS.Product p, Long descriptionWord) {
        HashSet<MDS.Product> set;
        t = descriptionIndex.get(descriptionWord);
        if (t != null) {
//            t.add(p);
            set = t.getOrDefault(p.price, new HashSet<>());
            set.add(p);
            t.put(p.price, set);
        }
        else {
            t = new TreeMap<>();
//            t.add(p);
            set = new HashSet<>();
            set.add(p);
            t.put(p.price, set);
            descriptionIndex.put(descriptionWord, t);
        }
    }

    public boolean findProductForWord(MDS.Product p, Long descriptionWord) {
        t = descriptionIndex.get(descriptionWord);
        HashSet<MDS.Product> set = t.get(p.price);
        for (MDS.Product p1 : set) {
            if (p1.id == p.id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the product from index
     *
     * @param p Product
     */
    public void delete(MDS.Product p) {
        for (Long desc : p.description) {
            t = descriptionIndex.get(desc);
            if (t != null) {
                HashSet<MDS.Product> set = t.get(p.price);
                if (set != null) {
                    set.remove(p);
                    if (set.isEmpty()) {
                        t.remove(p.price);
                    }
                }
            }
            if (t.size() == 0) {
                descriptionIndex.remove(desc);
            }
            else {
                descriptionIndex.put(desc, t);
            }
        }
    }

    /**
     * Remove the product from index for a specific word
     *
     * @param p               Product to remove
     * @param descriptionWord Word for which to remove
     */
    public void deleteProductForAWord(MDS.Product p, Long descriptionWord) {
        t = descriptionIndex.get(descriptionWord);
        if (t != null) {
            HashSet<MDS.Product> set = t.get(p.price);
            set.remove(p);
            if (set.isEmpty()) {
                t.remove(p.price);
            }
            descriptionIndex.put(descriptionWord, t);
        }
    }

    /**
     * Return Product with maximum price. May be null
     *
     * @param desc Word to search for in the index
     * @return Product with maximum price. Null if the product doesn't exist
     */
    public MDS.Product findMax(Long desc) {
        t = descriptionIndex.get(desc);
        if (t == null || t.size() == 0) {
            return null;
        } else {
//            return t.last();
            HashSet<MDS.Product> set = t.lastEntry().getValue();
            if (!set.isEmpty()) {
                for (MDS.Product p : set) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * Return product with minimum price
     *
     * @param desc Word to search for in the index
     * @return Product with minimum proce. Null if the product doesn't exist
     */
    public MDS.Product findMin(Long desc) {
        t = descriptionIndex.get(desc);
        if (t == null || t.size() == 0) {
            return null;
        } else {
//            return t.first();
            HashSet<MDS.Product> set = t.firstEntry().getValue();
            if (!set.isEmpty()) {
                for (MDS.Product p : set) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * Give a description word and lower and upper bound on price, find number of products between those prices
     *
     * @param desc Description word to search for
     * @param low  lower bound on price
     * @param high upper bound on price
     * @return Number of products that have desc in their description and have price between low and high
     */
    public int findPriceRange(Long desc, MDS.Money low, MDS.Money high) {
        t = descriptionIndex.get(desc);
        if (t == null || t.size() == 0) {
            return 0;
        }

        MDS.Product lowerDummy, upperDummy, lProduct = null, uProduct = null;
        boolean lowerInclusive = false, upperInclusive = false;

        Map.Entry<MDS.Money, HashSet<MDS.Product>> lowerMoneyMap, upperMoneyMap;
        lowerMoneyMap = t.lowerEntry(low);
        upperMoneyMap = t.ceilingEntry(high);

        // if there's no product smaller than given product, it's the smallest!
        if (lowerMoneyMap == null) {
            lowerMoneyMap = t.firstEntry();
            lowerInclusive = true;
        }

        if (upperMoneyMap == null) {
            upperMoneyMap = t.lastEntry();
            upperInclusive = true;
        }


        if (low.equals(lowerMoneyMap.getKey())) {
            lowerInclusive = true;
        }

        if (high.equals(upperMoneyMap.getKey())) {
            upperInclusive = true;
        }


        // TODO: Remove the extra variable creation after testing
//        NavigableSet<MDS.Product> s = t.subSet(lowerProduct, lowerInclusive, upperProduct, upperInclusive);
        NavigableMap<MDS.Money, HashSet<MDS.Product>> s =
                t.subMap(lowerMoneyMap.getKey(), lowerInclusive, upperMoneyMap.getKey(), upperInclusive);
//        return s.size();
        HashSet<MDS.Product> set;
        int size = 0;
        for (MDS.Money m : s.keySet()) {
            set = s.get(m);
            size += set.size();
        }
        return size;
    }
}
