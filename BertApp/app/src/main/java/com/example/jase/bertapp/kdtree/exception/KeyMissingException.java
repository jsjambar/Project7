package com.example.jase.bertapp.kdtree.exception;

public class KeyMissingException extends KDException {

    public KeyMissingException() {
	super("Key not found");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 3L;
    
}
