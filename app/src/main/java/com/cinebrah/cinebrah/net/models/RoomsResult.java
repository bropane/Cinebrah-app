package com.cinebrah.cinebrah.net.models;

import java.util.List;

/**
 * Created by Taylor on 3/2/2015.
 */
public class RoomsResult {
    int count;
    String next;
    String previous;
    List<Room> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Room> getResults() {
        return results;
    }
}
