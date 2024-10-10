package com.flightpoint.service;

/**
 * Created by following a blog post by Dan Hales, linked below
 * https://medium.com/swlh/programming-linear-algebra-in-java-vector-operations-6ba08fdd5a1a
 * I have only included the vector operations necessary to make my app run.
 */
public class Vector {
    private double[] v;

    public Vector(double ... v) {
        this.v = new double[v.length];
            for (int i = 0; i < v.length; i++) {
                this.v[i] = v[i];
            }
    }

    @Override
    public String toString() {
        String str = "[";
        String sep = ",\n";

        for (int i = 0; i < this.v.length; i++) {
            str += this.v[i];

            if (i < (this.v.length - 1)) {
                str += sep;
            }
        }

        return str + "]";
    }

    public int length() {
        return v.length;
    }
 
    public double get(int position) {
        return v[position];
    }

    public double[] getV() {
        return v.clone();
    }

    public void setV(double[] v) {
        this.v = v;
    }

    public boolean set(int index, double value) {
        if(index > v.length - 1) {
            return false;
        } else {
            v[index] = value;
            return true;
        }
    }

    public static boolean isZero(Vector u) {
        for (int i = 0; i < u.length(); i ++) {
            if (u.get(i) != 0.0) {
                return false;
            }
        }
        return true;
    }

    public static void checkLengths(Vector u1, Vector u2) {
        if (u1.length() != u2.length()) { 
            throw new IllegalArgumentException("Vectors are different lengths");
        }
    }

    public Vector multiply(double scalar) {
        return Vector.product(this, scalar);
    }

    public static Vector product(Vector u, double scalar) {
        double[] products = new double[u.length()];

        for (int i = 0; i < products.length; i ++) {
            products[i] = scalar * u.get(i);
        }

        return new Vector(products);
    }

    public double dot(Vector u) { 
        return Vector.dotProduct(this, u);
    }

    public static double dotProduct(Vector u1, Vector u2) {
        Vector.checkLengths(u1, u2);

        double sum = 0;

        for (int i = 0; i < u1.length(); i++) {
            sum += (u1.get(i) * u2.get(i));
        }

        return sum;
    }

    public static double pnorm(Vector u, double p) {
        if (p < 1) {
            throw new IllegalArgumentException("p must be >= 1");
        }

        double sum = 0;

        for (int i = 0; i < u.length(); i++) {
            sum += Math.pow(Math.abs(u.get(i)), p);
        }

        return Math.pow(sum, 1/p);
    }

    public double magnitude() {
        return Vector.pnorm(this, 2);
    }
}
