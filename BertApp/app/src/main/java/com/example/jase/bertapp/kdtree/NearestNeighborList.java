package com.example.jase.bertapp.kdtree;

import java.util.PriorityQueue;

class NearestNeighborList<T> {

    private static class NeighborEntry<T> implements Comparable<NeighborEntry<T>> {

        private final T data;
        private final double value;

        public NeighborEntry(final T data, final double value) {
            this.data = data;
            this.value = value;
        }

        public int compareTo(NeighborEntry<T> t) {
            //Note that the positions are reversed!
            return Double.compare(t.value, this.value);
        }
    }

    private PriorityQueue<NeighborEntry<T>> mQueue;
    private int mCapacity = 0;

    // constructor
    public NearestNeighborList(int capacity) {
        mCapacity = capacity;
        mQueue = new PriorityQueue<>(mCapacity);
    }

    public double getMaxPriority() {
        NeighborEntry p = mQueue.peek();
        return (p == null) ? Double.POSITIVE_INFINITY : p.value ;
    }

    public boolean insert(T object, double priority) {
        if (isCapacityReached()) {
            if (priority > getMaxPriority()) {
                // do not insert - all elements in queue have lower priority
                return false;
            }
            mQueue.add(new NeighborEntry<>(object, priority));
            // remove object with highest priority
            mQueue.poll();
        } else {
            mQueue.add(new NeighborEntry<>(object, priority));
        }
        return true;
    }

    public boolean isCapacityReached() {
        return mQueue.size() >= mCapacity;
    }

    public T getHighest() {
        NeighborEntry<T> p = mQueue.peek();
        return (p == null) ?  null : p.data ;
    }

    public boolean isEmpty() {
        return mQueue.size() == 0;
    }

    public int getSize() {
        return mQueue.size();
    }

    public T removeHighest() {
        // remove object with highest priority
        NeighborEntry<T> p = mQueue.poll();
        return (p == null) ?  null : p.data ;
    }

}