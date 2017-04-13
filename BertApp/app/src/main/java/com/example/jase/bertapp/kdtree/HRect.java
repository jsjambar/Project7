package com.example.jase.bertapp.kdtree;

import java.io.Serializable;

class HRect implements Serializable {

    private HPoint min;
    private HPoint max;

    protected HRect(int var1) {
        this.min = new HPoint(var1);
        this.max = new HPoint(var1);
    }

    protected HRect(HPoint var1, HPoint var2) {
        this.min = (HPoint) var1.clone();
        this.max = (HPoint) var2.clone();
    }

    protected Object clone() {
        return new HRect(this.min, this.max);
    }

    protected HPoint closest(HPoint var1) {
        HPoint var2 = new HPoint(var1.getCoord().length);

        for(int var3 = 0; var3 < var1.getCoord().length; ++var3) {
            if(var1.getCoord()[var3] <= this.min.getCoord()[var3]) {
                var2.getCoord()[var3] = this.min.getCoord()[var3];
            } else if(var1.getCoord()[var3] >= this.max.getCoord()[var3]) {
                var2.getCoord()[var3] = this.max.getCoord()[var3];
            } else {
                var2.getCoord()[var3] = var1.getCoord()[var3];
            }
        }
        return var2;
    }

    protected static HRect infiniteHRect(int var0) {
        HPoint var1 = new HPoint(var0);
        HPoint var2 = new HPoint(var0);

        for(int var3 = 0; var3 < var0; ++var3) {
            var1.getCoord()[var3] = -Integer.MAX_VALUE;
            var2.getCoord()[var3] = Integer.MAX_VALUE;
        }

        return new HRect(var1, var2);
    }

    protected HRect intersection(HRect var1) {
        HPoint var2 = new HPoint(this.min.getCoord().length);
        HPoint var3 = new HPoint(this.min.getCoord().length);

        for(int var4 = 0; var4 < this.min.getCoord().length; ++var4) {
            var2.getCoord()[var4] = Math.max(this.min.getCoord()[var4], var1.min.getCoord()[var4]);
            var3.getCoord()[var4] = Math.min(this.max.getCoord()[var4], var1.max.getCoord()[var4]);
            if(var2.getCoord()[var4] >= var3.getCoord()[var4]) {
                return null;
            }
        }

        return new HRect(var2, var3);
    }

    protected double area() {
        double var1 = 1.0D;

        for(int var3 = 0; var3 < this.min.getCoord().length; ++var3) {
            var1 *= this.max.getCoord()[var3] - this.min.getCoord()[var3];
        }

        return var1;
    }

    public String toString() {
        return this.min + "\n" + this.max + "\n";
    }

    public HPoint getMin() {
        return min;
    }

    public HPoint getMax() {
        return max;
    }
}
