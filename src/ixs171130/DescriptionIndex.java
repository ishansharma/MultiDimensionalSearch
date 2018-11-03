/*
 * Authors:
 * Ishan Sharma - ixs171130
 * Ravikiran Kolanpaka - rxk171530
 * Sharayu Mantri - ssm171330
 */
package ixs171130;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Class to hold the description index
 */
class DescriptionIndex {

    private Map<Long, HashSet<MDS.Product>> descriptionIndex = new HashMap<>();
    private HashSet<MDS.Product> t;

    /**
     * Add a product with given description to our index
     *
     * @param p Product to add to the index
     */
    public void add(MDS.Product p) {
        for (Long desc : p.description) {
            t = descriptionIndex.getOrDefault(desc, new HashSet<>());
            t.add(p);
            descriptionIndex.put(desc, t);
        }
    }


    /**
     * Add the product from index for a specific word
     *
     * @param p               Product to add
     * @param descriptionWord Word for which to add
     */
    public void addProductForAWord(MDS.Product p, Long descriptionWord) {
        t = descriptionIndex.getOrDefault(descriptionWord, new HashSet<>());
        t.add(p);
        descriptionIndex.put(descriptionWord, t);
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
                t.remove(p);
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
            t.remove(p);
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
        MDS.Money maxPrice = new MDS.Money();
        MDS.Product maxP = null;
        if (t != null) {
            for (MDS.Product p : t) {
                if (p.price.compareTo(maxPrice)>= 0) {
                    maxPrice = p.price;
                    maxP = p;
                }
            }
        }
        return maxP;
    }

    /**
     * Return product with minimum price
     *
     * @param desc Word to search for in the index
     * @return Product with minimum proce. Null if the product doesn't exist
     */
    public MDS.Product findMin(Long desc) {
        t = descriptionIndex.get(desc);
        boolean flag = true;
        MDS.Money minPrice = null;
        MDS.Product minP = null;
        if (t != null) {
            for (MDS.Product p : t) {
                if (flag) {
                    minP = p;
                    minPrice = p.price;
                    flag = false;
                }
                if (p.price.compareTo(minPrice) < 0) {
                    minPrice = p.price;
                    minP = p;
                }
            }
        }
        return minP;
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
        int count  = 0;
        if (t != null) {
            for (MDS.Product p : t) {
                if (p.price.compareTo(low) >= 0 && p.price.compareTo(high) <= 0) {
                    count += 1;
                }
            }
        }
        return count;
    }
}
