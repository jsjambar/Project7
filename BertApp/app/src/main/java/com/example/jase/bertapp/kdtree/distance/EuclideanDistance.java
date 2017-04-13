package com.example.jase.bertapp.kdtree.distance;

public class EuclideanDistance extends DistanceMetric {

	@Override
    public double distance(double[] a, double[] b)  {
		return Math.sqrt(sqrDistance(a, b));
    }

	private double sqrDistance(double[] a, double[] b) {
		double dist = 0;

		for (int i = 0; i < a.length; ++i) {
			double diff = (a[i] - b[i]);
			dist += diff*diff;
		}
		return dist;
    }
}
