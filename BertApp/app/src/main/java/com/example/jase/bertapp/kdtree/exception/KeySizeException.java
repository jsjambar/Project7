package com.example.jase.bertapp.kdtree.exception;

/**
* KeySizeException is thrown when a KDTree method is invoked on a
* key whose size (array length) mismatches the one used in the that
* KDTree's constructor.
*/

public class KeySizeException extends KDException {

    public KeySizeException() {
	super("Key size mismatch");
    }
    
    //Arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 2L;
    
}
