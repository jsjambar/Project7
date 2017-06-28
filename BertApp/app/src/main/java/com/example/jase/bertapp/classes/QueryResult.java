package com.example.jase.bertapp.classes;

/**
 * Created by Jeroen on 28-06-17.
 */

public class QueryResult {
    public Boolean success;
    public String message;

    public QueryResult(Boolean _success, String _message){
        this.success = _success;
        this.message = _message;
    }
}
