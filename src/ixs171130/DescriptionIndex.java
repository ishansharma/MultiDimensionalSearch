package ixs171130;

import java.util.*;

/**
 * Class to hold the description index
 */
class DescriptionIndex {

    private Map<Long, TreeSet<MDS.Product>> descriptionIndex = new HashMap<>();
    private Comparator<MDS.Product> PriceComparator = Comparator.comparing(o -> o.price);
    private TreeSet<MDS.Product> t;

    /**
     * Add a product with given description to our index
     *
     * @param p Product to add to the index
     */
    public void add(MDS.Product p) {
        for (Long desc : p.description) {
            t = descriptionIndex.get(desc);
            if (t == null) {  // this we are seeing this description for the first time
                t = new TreeSet<>(PriceComparator);
                t.add(p);
                descriptionIndex.put(desc, t);
            } else {
                t.add(p);
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
        t = descriptionIndex.get(descriptionWord);
        if (t != null) {
            t.add(p);
        }
        else {
            t = new TreeSet<>(PriceComparator);
            t.add(p);
            descriptionIndex.put(descriptionWord, t);
        }
    }

    public boolean findProductForWord(MDS.Product p, Long descriptionWord) {
        t = descriptionIndex.get(descriptionWord);
        if (t.contains(p)) {
            return true;
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
        if (t == null || t.size() == 0) {
            return null;
        } else {
            return t.last();
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
            return t.first();
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

        MDS.Product lowerProduct, upperProduct, lowerDummy, upperDummy;
        boolean lowerInclusive = false, upperInclusive = false;
        lowerDummy = new MDS.Product(0, low);
        upperDummy = new MDS.Product(0, high);
        lowerProduct = t.floor(lowerDummy);
        upperProduct = t.ceiling(upperDummy);

        // if there's no product smaller than given product, it's the smallest!
        if (lowerProduct == null) {
            lowerProduct = t.first();
            lowerInclusive = true;
        }

        if (upperProduct == null) {
            upperProduct = t.last();
            upperInclusive = true;
        }

        if (lowerDummy.price.equals(lowerProduct.price)) {
            lowerInclusive = true;
        }

        if (upperDummy.price.equals(upperProduct.price)) {
            upperInclusive = true;
        }

        //
        if (lowerProduct.equals(upperProduct)) {
            return 1;
        }

        // TODO: Remove the extra variable creation after testing
        NavigableSet<MDS.Product> s = t.subSet(lowerProduct, lowerInclusive, upperProduct, upperInclusive);
        return s.size();
    }
}
