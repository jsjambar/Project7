package com.example.jase.bertapp.kdtree;

import com.example.jase.bertapp.kdtree.distance.DistanceMetric;
import com.example.jase.bertapp.kdtree.distance.EuclideanDistance;
import com.example.jase.bertapp.kdtree.distance.HammingDistance;
import com.example.jase.bertapp.kdtree.exception.KeyDuplicateException;
import com.example.jase.bertapp.kdtree.exception.KeyMissingException;
import com.example.jase.bertapp.kdtree.exception.KeySizeException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * KDTree is a class supporting KD-tree insertion, deletion, equality
 * search, range search, and nearest neighbor(s) using double-precision
 * floating-point keys.  Splitting dimension is chosen naively, by
 * depth modulo K.  Semantics are as follows:
 *
 *
 * Two different keys containing identical numbers should retrieve the
 * same value from a given KD-tree.  Therefore keys are cloned when a
 * node is inserted.
 *
 * As with Hashtables, values inserted into a KD-tree are not
 * cloned. Modifying a value between insertion and retrieval will
 * therefore modify the value stored in the tree.
 */

public class KDTree<T> implements Serializable {
    // number of milliseconds
    final long mTimeout;

    // K = number of dimensions
    final private int nDim;

    // root of KD-tree
    private KDNode<T> root;

    // count of nodes
    private int nCount;

    /**
     * Creates a KD-tree with specified number of dimensions.
     *
     * @param nDim number of dimensions
     */

    public KDTree(int nDim) {
        this(nDim, 0);
    }

    public KDTree(int nDim, long timeout) {
        this.mTimeout = timeout;
        this.nDim = nDim;
        root = null;
    }


    /**
     * Insert a node in a KD-tree.  Uses algorithm translated from 352.ins.c of
     *
     * @param key key for KD-tree node
     * @param value value at that key
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyDuplicateException if key already in tree
     */

    public void insert(double[] key, T value) throws KeySizeException, KeyDuplicateException {
        this.edit(key, new Editor.Inserter<>(value));
    }

    /**
     * Edit a node in a KD-tree
     *
     * @param key key for KD-tree node
     * @param editor object to edit the value at that key
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyDuplicateException if key already in tree
     */

    public void edit(double[] key, Editor<T> editor) throws KeySizeException, KeyDuplicateException {
        if (key.length != nDim) {
            throw new KeySizeException();
        }

        synchronized (this) {
            // the first insert has to be synchronized
            if (null == root) {
                root = KDNode.create(new HPoint(key), editor);
                nCount = root.isDeleted() ? 0 : 1;
                return;
            }
        }

        nCount += KDNode.edit(new HPoint(key), editor, root, 0, nDim);
    }

    /**
     * Find  KD-tree node whose key is identical to key.  Uses algorithm
     * translated from 352.srch.c of Gonnet & Baeza-Yates.
     *
     * @param key key for KD-tree node
     *
     * @return object at key, or null if not found
     *
     * @throws KeySizeException if key.length mismatches K
     */
    public T search(double[] key) throws KeySizeException {
        if (key.length != nDim) {
            throw new KeySizeException();
        }

        KDNode<T> node = KDNode.search(new HPoint(key), root, nDim);
        return (node == null ? null : node.getValue());
    }

    public boolean contains(double[] key) throws KeySizeException {
        return search(key) != null;
    }

    public void delete(double[] key) throws KeySizeException, KeyMissingException {
        delete(key, false);
    }
    /**
     * Delete a node from a KD-tree.  Instead of actually deleting node and
     * rebuilding tree, marks node as deleted.  Hence, it is up to the caller
     * to rebuild the tree as needed for efficiency.
     *
     * @param key key for KD-tree node
     * @param optional if false and node not found, throw an exception
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyMissingException if no node in tree has key
     */
    public void delete(double[] key, boolean optional) throws KeySizeException, KeyMissingException {
        if (key.length != nDim) {
            throw new KeySizeException();
        }
        KDNode<T> node = KDNode.search(new HPoint(key), root, nDim);
        if (node == null) {
            if (!optional) {
                throw new KeyMissingException();
            }
        }
        else {
            if (KDNode.delete(node)) {
                nCount--;
            }
        }
    }

    /**
     * Find KD-tree node whose key is nearest neighbor to
     * key.
     *
     * @param key key for KD-tree node
     *
     * @return object at node nearest to key, or null on failure
     *
     * @throws KeySizeException if key.length mismatches K

     */
    public T nearest(double[] key) throws KeySizeException {
        List<T> nbrs = nearest(key, 1, null);
        return nbrs.get(0);
    }

    /**
     * Find KD-tree nodes whose keys are nearest neighbors to
     * key.
     *
     * @param key key for KD-tree node
     * @param n number of nodes to return
     *
     * @return objects at nodes nearest to key, or null on failure
     *
     * @throws KeySizeException if key.length mismatches K

     */
    public List<T> nearest(double[] key, int n) throws KeySizeException, IllegalArgumentException {
        return nearest(key, n, null);
    }

    /**
     * Find KD-tree nodes whose keys are within a given Euclidean distance of
     * a given key.
     *
     * key key for KD-tree node
     * d Euclidean distance
     *
     * @return objects at nodes with distance of key, or null on failure
     *
     * @throws KeySizeException if key.length mismatches K

     */
    public List<T> nearestEuclidean(double[] key, double dist) throws KeySizeException {
        return nearestDistance(key, dist, new EuclideanDistance());
    }

    /**
     * Find KD-tree nodes whose keys are within a given Hamming distance of
     * a given key.
     *
     * key key for KD-tree node
     * Hamming distance
     *
     * @return objects at nodes with distance of key, or null on failure
     *
     * @throws KeySizeException if key.length mismatches K

     */
    public List<T> nearestHamming(double[] key, double dist) throws KeySizeException {
        return nearestDistance(key, dist, new HammingDistance());
    }


    /**
     * Find KD-tree nodes whose keys are n nearest neighbors to
     * key. Uses algorithm above. Neighbors are returned in ascending
     * order of distance to key.
     *
     * @param key key for KD-tree node
     * @param n how many neighbors to find
     * @param checker an optional object to filter matches
     *
     * @return objects at node nearest to key, or null on failure
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws IllegalArgumentException if <I>n</I> is negative or
     * exceeds tree size
     */
    public List<T> nearest(double[] key, int n, Checker<T> checker) throws KeySizeException, IllegalArgumentException {
        if (n <= 0) {
            return new LinkedList<>();
        }
        NearestNeighborList<KDNode<T>> nnList = getNeighbours(key, n, checker);

        n = nnList.getSize();
        Stack<T> nbrs = new Stack<>();

        for (int i = 0; i < n; ++i) {
            KDNode<T> node = nnList.removeHighest();
            nbrs.push(node.getValue());
        }

        return nbrs;
    }

    public List<T> rangeSearch(double[] searchPoints, double distance) throws KeySizeException {
        double[] low = {searchPoints[0] - distance, searchPoints[1] - distance};
        double[] up = {searchPoints[0] + distance, searchPoints[1] + distance};
        return rangeSearch(low, up);
    }

    /**
     * Range search in a KD-tree.  Uses algorithm translated from
     * 352.range.c of Gonnet & Baeza-Yates.
     *
     * @param lowk lower-bounds for key
     * @param uppk upper-bounds for key
     *
     * @return array of Objects whose keys fall in range [lowk,uppk]
     *
     * @throws KeySizeException on mismatch among lowk.length, uppk.length, or K
     */

    public List<T> rangeSearch(double[] lowk, double[] uppk)  throws KeySizeException {
        if (lowk.length != uppk.length) {
            throw new KeySizeException();
        } else if (lowk.length != nDim) {
            throw new KeySizeException();
        } else {
            List<KDNode<T>> nodesFound = new LinkedList<>();
            KDNode.rSearch(new HPoint(lowk), new HPoint(uppk),  root, 0, nDim, nodesFound);

            List<T> list = new LinkedList<>();
            for (KDNode<T> node : nodesFound) {
                list.add(node.getValue());
            }
            return list;
        }
    }

    public int size() { /* added by MSL */
        return nCount;
    }

    public String toString() {
        return root.toString(0);
    }

    private NearestNeighborList<KDNode<T>> getNeighbours(double[] key) throws KeySizeException {
        return getNeighbours(key, nCount, null);
    }


    private NearestNeighborList<KDNode<T>> getNeighbours(double[] key, int n, Checker<T> checker) throws KeySizeException {
        if (key.length != nDim) {
            throw new KeySizeException();
        }

        NearestNeighborList<KDNode<T>> nnList = new NearestNeighborList<>(n);

        //Initial call is with infinite hyper-rectangle and max distance
        HRect hr = HRect.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;
        HPoint keyPoint = new HPoint(key);

        if (nCount > 0) {
            long timeout = (this.mTimeout > 0) ? (System.currentTimeMillis() + this.mTimeout) : 0;
            KDNode.nnbr(root, keyPoint, hr, max_dist_sqd, 0, nDim, nnList, checker, timeout);
        }

        return nnList;
    }

    private  List<T> nearestDistance(double[] key, double dist, DistanceMetric metric) throws KeySizeException {
        NearestNeighborList<KDNode<T>> nnList = getNeighbours(key);
        int size = nnList.getSize();
        Stack<T> nbrs = new Stack<>();

        for (int i = 0; i < size; ++i) {
            KDNode<T> node = nnList.removeHighest();
            HPoint p = node.getHPoint();
            if (metric.distance(node.getHPoint().getCoord(), key) < dist) {
                nbrs.push(node.getValue());
            }
        }

        return nbrs;
    }


}

