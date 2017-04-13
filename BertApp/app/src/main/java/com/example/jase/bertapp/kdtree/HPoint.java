package com.example.jase.bertapp.kdtree;


import com.example.jase.bertapp.kdtree.distance.EuclideanDistance;

import java.io.Serializable;

class HPoint implements Serializable {

    private double[] coord;

    protected HPoint(int n) {
        coord = new double[n];
    }

    protected HPoint(double[] x) {

        coord = new double[x.length];
        for (int i = 0; i < x.length; ++i)
            coord[i] = x[i];
    }

    protected Object clone() {
        return new HPoint(coord);
    }

    protected boolean equals(HPoint p) {
        for (int i = 0; i < coord.length; ++i)
            if (coord[i] != p.coord[i])
                return false;
        return true;
    }

    protected static double sqrDistance(HPoint x, HPoint y) {
        return new EuclideanDistance().distance(x.coord, y.coord);
    }

    public String toString() {
        String s = "";
        for (double aCoord : coord) {
            s = s + aCoord + " ";
        }
        return s;
    }

    public double[] getCoord() {
        return coord;
    }
}
