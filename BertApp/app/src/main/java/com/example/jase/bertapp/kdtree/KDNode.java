package com.example.jase.bertapp.kdtree;


import com.example.jase.bertapp.kdtree.exception.KeyDuplicateException;

import java.io.Serializable;
import java.util.List;

class KDNode<T> implements Serializable {

    private HPoint hPoint;
    private T value;
    private KDNode<T> left, right;
    private boolean deleted;

    protected static <T> int edit(HPoint key, Editor<T> editor, KDNode<T> node, int lev, int K) throws KeyDuplicateException {
        KDNode<T> nextNode;
        int nextLev = (lev+1) % K;

        synchronized (node) {
            if (key.equals(node.getHPoint())) {
                boolean wasDeleted = node.isDeleted();
                node.setValue(editor.edit(node.isDeleted() ? null : node.getValue() ));
                node.setDeleted(node.getValue() == null);

                if (node.isDeleted() == wasDeleted) {
                    //If I was and still am deleted or was and still am alive
                    return 0;
                } else if (wasDeleted) {
                    //If I was deleted => I am now undeleted
                    return 1;
                }
                //I was not deleted, but I am now deleted
                return -1;
            } else if (key.getCoord()[lev] > node.hPoint.getCoord()[lev]) {
                nextNode = node.getRight();
                if (nextNode == null) {
                    node.setRight(create(key, editor));
                    return node.getRight().isDeleted() ? 0 : 1;
                }
            }
            else {
                nextNode = node.getLeft();
                if (nextNode == null) {
                    node.setLeft(create(key, editor));
                    return node.getLeft().isDeleted() ? 0 : 1;
                }
            }
        }

        return edit(key, editor, nextNode, nextLev, K);
    }

    protected static <T> KDNode<T> create(HPoint key, Editor<T> editor) throws KeyDuplicateException {
        KDNode<T> node = new KDNode<>(key, editor.edit(null));
        if (node.getValue() == null) {
            node.setDeleted(true); 
        }
        return node;
    }

    protected static <T> boolean delete(KDNode<T> node) {
        synchronized (node) {
            if (!node.isDeleted()) {
                node.setDeleted(true);
                return true;
            }
        }
        return false;
    }

    protected static <T> KDNode<T> search(HPoint key, KDNode<T> node, int k) {
        for (int lev = 0; node != null; lev = (lev+1) % k) {
            if (!node.isDeleted() && key.equals(node.getHPoint())) {
                return node;
            } else if (key.getCoord()[lev] > node.getHPoint().getCoord()[lev]) {
                node = node.getRight();
            } else {            
                node = node.getLeft();
            }
        }

        return null;
    }

    protected static <T> void rSearch(HPoint lowPoint, HPoint upPoint, KDNode<T> node, int lev, int k, List<KDNode<T>> nodes) {
        if (node == null)
            return;
        if (lowPoint.getCoord()[lev] <= node.getHPoint().getCoord()[lev]) {
            rSearch(lowPoint, upPoint, node.getLeft(), (lev+1) % k, k, nodes);
        }
        if (!node.isDeleted()) {
            int j = 0;
            while (j < k && lowPoint.getCoord()[j] <= node.getHPoint().getCoord()[j] && upPoint.getCoord()[j] >= node.getHPoint().getCoord()[j]) {
                j++;
            }
            if (j == k)
                nodes.add(node);
        }
        if (upPoint.getCoord()[lev] > node.getHPoint().getCoord()[lev]) {
            rSearch(lowPoint, upPoint, node.getRight(), (lev+1) % k, k, nodes);
        }
    }

    // Method Nearest Neighbor from Andrew Moore's thesis. Numbered
    // comments are direct quotes from there.   NearestNeighborList solution
    // courtesy of Bjoern Heckel.
    protected static <T> void nnbr(KDNode<T> node, HPoint target, HRect hRect, double maxDistSqd, int lev, int k,
                                   NearestNeighborList<KDNode<T>> nnList, Checker<T> checker, long timeout) {

        // 1. if node is empty then set dist-sqd to infinity and exit.
        if (node == null) {
            return;
        }

        if ((timeout > 0) && (timeout < System.currentTimeMillis())) {
            return;
        }
        // 2. s := split field of node
        int s = lev % k;

        // 3. pivot := dom-elt field of node
        HPoint pivot = node.getHPoint();
        double pivotToTarget = HPoint.sqrDistance(pivot, target);

        // 4. Cut hr into to sub-hyperrectangles left-hr and right-hr.
        //    The cut plane is through pivot and perpendicular to the s
        //    dimension.
        HRect leftHr = hRect; // optimize by not cloning
        HRect rightHr = (HRect) hRect.clone();
        leftHr.getMax().getCoord()[s] = pivot.getCoord()[s];
        rightHr.getMin().getCoord()[s] = pivot.getCoord()[s];

        // 5. target-in-left := target_s <= pivot_s
        boolean targetInLeft = target.getCoord()[s] < pivot.getCoord()[s];

        KDNode<T> nearerNode;
        HRect nearerHr;
        KDNode<T> furtherNode;
        HRect furtherHr;

        // 6. if target-in-left then
        //    6.1. nearer-node := left field of node and nearer-hr := left-hr
        //    6.2. further-node := right field of node and further-hr := right-hr
        if (targetInLeft) {
            nearerNode = node.getLeft();
            nearerHr = leftHr;
            furtherNode = node.getRight();
            furtherHr = rightHr;
        }
        //
        // 7. if not target-in-left then
        //    7.1. nearer-node := right field of node and nearer-hr := right-hr
        //    7.2. further-node := left field of node and further-hr := left-hr
        else {
            nearerNode = node.getRight();
            nearerHr = rightHr;
            furtherNode = node.getLeft();
            furtherHr = leftHr;
        }

        // 8. Recursively call Nearest Neighbor with paramters
        //    (nearer-kd, target, nearer-hr, max-dist-sqd), storing the
        //    results in nearest and dist-sqd
        nnbr(nearerNode, target, nearerHr, maxDistSqd, lev + 1, k, nnList, checker, timeout);

        KDNode<T> nearest = nnList.getHighest();
        double distSqd;

        if (!nnList.isCapacityReached()) {
            distSqd = Double.MAX_VALUE;
        }
        else {
            distSqd = nnList.getMaxPriority();
        }

        // 9. max-dist-sqd := minimum of max-dist-sqd and dist-sqd
        maxDistSqd = Math.min(maxDistSqd, distSqd);

        // 10. A nearer point could only lie in further-kd if there were some
        //     part of further-hr within distance max-dist-sqd of
        //     target.
        HPoint closest = furtherHr.closest(target);
        if (HPoint.sqrDistance(closest, target) < maxDistSqd) {

            // 10.1 if (pivot-target)^2 < dist-sqd then
            if (pivotToTarget < distSqd) {

                // 10.1.1 nearest := (pivot, range-elt field of kd)
                nearest = node;

                // 10.1.2 dist-sqd = (pivot-target)^2
                distSqd = pivotToTarget;

                // add to nnList
                if (!node.isDeleted() && ((checker == null) || checker.usable(node.getValue()))) {
                    nnList.insert(node, distSqd);
                }

                // 10.1.3 max-dist-sqd = dist-sqd
                // maxDistSqd = dist_sqd;
                if (nnList.isCapacityReached()) {
                    maxDistSqd = nnList.getMaxPriority();
                }
                else {
                    maxDistSqd = Double.MAX_VALUE;
                }
            }

            // 10.2 Recursively call Nearest Neighbor with parameters
            //      (further-kd, target, further-hr, max-dist_sqd),
            //      storing results in temp-nearest and temp-dist-sqd
            nnbr(furtherNode, target, furtherHr, maxDistSqd, lev + 1, k, nnList, checker, timeout);
        }
    }


    // constructor is used only by class; other methods are static
    private KDNode(HPoint key, T val) {
        hPoint = key;
        value = val;
        left = null;
        right = null;
        deleted = false;
    }

    protected String toString(int depth) {
        String s = hPoint + "  " + value + (deleted ? "*" : "");
        if (left != null) {
            s = s + "\n" + pad(depth) + "L " + left.toString(depth+1);
        }
        if (right != null) {
            s = s + "\n" + pad(depth) + "R " + right.toString(depth+1);
        }
        return s;
    }

    private String pad(int n) {
        String s = "";
        for (int i=0; i<n; ++i) {
            s += " ";
        }
        return s;
    }

    private void hrcopy(HRect hr_src, HRect hr_dst) {
        hpcopy(hr_src.getMin(), hr_dst.getMin());
        hpcopy(hr_src.getMax(), hr_dst.getMax());
    }

    private void hpcopy(HPoint hp_src, HPoint hp_dst) {
        for (int i=0; i<hp_dst.getCoord().length; ++i) {
            hp_dst.getCoord()[i] = hp_src.getCoord()[i];
        }
    }

    public void setLeft(KDNode<T> left) {
        this.left = left;
    }

    public KDNode<T> getLeft() {
        return left;
    }

    public void setRight(KDNode<T> right) {
        this.right = right;
    }

    public KDNode<T> getRight() {
        return right;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setHPoint(HPoint hPoint) {
        this.hPoint = hPoint;
    }

    public HPoint getHPoint() {
        return hPoint;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }
}

