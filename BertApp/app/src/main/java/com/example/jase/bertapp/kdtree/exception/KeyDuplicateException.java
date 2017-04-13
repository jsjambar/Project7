package com.example.jase.bertapp.kdtree.exception;

/**
* KeyDuplicateException is thrown when the <TT>KDTree.insert</TT> method
* is invoked on a key already in the KDTree.
*/

public class KeyDuplicateException extends KDException {

    public KeyDuplicateException() {
	    super("Key already in tree");
    }
    
    // Arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 1L;
}
