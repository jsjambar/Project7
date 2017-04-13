package com.example.jase.bertapp.kdtree;


import com.example.jase.bertapp.kdtree.exception.KeyDuplicateException;

public interface Editor<T> {

    T edit(T current) throws KeyDuplicateException;

    abstract class BaseEditor<T> implements Editor<T> {
        final T val;
        public BaseEditor(T val) {
            this.val = val;
        }
        public abstract T edit(T current) throws KeyDuplicateException;
    }

    class Inserter<T> extends BaseEditor<T> {

        public Inserter(T val) {
            super(val);
        }

        public T edit(T current) throws KeyDuplicateException {
            if (current == null) {
                return this.val;
            }
            throw new KeyDuplicateException();
        }

    }

    class OptionalInserter<T> extends BaseEditor<T> {
        public OptionalInserter(T val) {
            super(val);
        }
        public T edit(T current) {
            return (current == null) ? this.val : current;
        }
    }

    class Replacer<T> extends BaseEditor<T> {
        public Replacer(T val) {
            super(val);
        }
        public T edit(T current) {
            return this.val;
        }
    }

}