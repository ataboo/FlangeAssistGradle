package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;


import android.graphics.Rect;

/**
 * Created by ataboo on 2015-12-19.
 */
public class IntVector implements Comparable<IntVector> {
    public enum RoundMode {
        FLOOR,
        CEILING,
        ROUND;

        public static int round(float val, RoundMode roundMode){
            switch (roundMode){
                case FLOOR:
                    return (int) Math.floor(val);
                case CEILING:
                    return (int) Math.ceil(val);
                case ROUND:
                    return Math.round(val);
                default:
                    return 0;
            }
        }
    }

    public int x;
    public int y;
    public int sortPriority = Integer.MAX_VALUE;

    public IntVector(){
        this.x = this.y = 0;
    }

    public IntVector(int x, int y){
        this.x = x;
        this.y = y;
    }

    public IntVector(float x, float y, RoundMode roundMode){
        this.x = RoundMode.round(x, roundMode);
        this.y = RoundMode.round(y, roundMode);
    }

    public IntVector(float x, float y){
        this(x, y, RoundMode.ROUND);
    }

    public IntVector(double x, double y){
        this((float) x, (float) y, RoundMode.ROUND);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            throw new NullPointerException();
        }

        if(obj == this){
            return true;
        }

        if(!(obj instanceof IntVector)){
            return false;
        }

        IntVector intVect = (IntVector) obj;
        return (intVect.x == x) && (intVect.y == y);
    }

    @Override
    public int hashCode(){
        int hash = 19;
        hash = 13 * hash + x;
        hash = 13 * hash + y;
        return hash;
    }

    @Override
    public int compareTo(IntVector intVector){
        if(intVector == null){
            throw new NullPointerException();
        }

        return Float.compare(this.magSqr(), intVector.magSqr());
    }

    public IntVector sub(IntVector subVector){
        if(subVector == null){
            throw new NullPointerException();
        }

        return sub(subVector.x, subVector.y);
    }

    public IntVector sub(int x, int y){
        return new IntVector(this.x - x, this.y - y);
    }

    public IntVector add(IntVector addVector){
        if(addVector == null){
            throw new NullPointerException();
        }

        return add(addVector.x, addVector.y);
    }

    public IntVector add(int xAdd, int yAdd){
        return new IntVector(x + xAdd, y + yAdd);
    }

    public IntVector scl(float scalar){
        float xFl = ((float) x) * scalar;
        float yFl = ((float) y) * scalar;

        return new IntVector(xFl, yFl, RoundMode.ROUND);
    }

    public float magnitude(){
        return (float) Math.sqrt(this.magSqr());
    }

    public float magSqr(){
        return (x * x) + (y * y);
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }

    public IntVector cpy(){
        return new IntVector(x, y);
    }

    public void set(float x, float y){
        this.x = RoundMode.round(x, RoundMode.ROUND);
        this.y = RoundMode.round(y, RoundMode.ROUND);
    }

    public boolean sharePrimaryAxis(IntVector targetVect){
        if(targetVect == null){
            throw new NullPointerException();
        }

        return (this.x == targetVect.x || this.y == targetVect.y);
    }

    public boolean inBoundsInclusive(IntVector lower, IntVector upper){
        return (lower.x <= x && x <= upper.x) &&
                (lower.y <= y && y <= upper.y);
    }

    public static IntVector zero(){
        return new IntVector(0, 0);
    }

    /** Returns looped index when out of range of array.
     * ex: length + 1 == 0, length - 1 == last index*/
    public static int loopIndex(int index, Object[] array){
        if(array == null || array.length == 0){
            return 0;
        }
        int length = array.length;
        return index > 0 ? index % (length) : Math.abs((length + index) % length);
    }

    public static int loopIndex(int index, int length){
        return index > 0 ? index % (length) : Math.abs((length + index) % length);
    }

    public IntVector north(){
        return new IntVector(x, y + 1);
    }

    public IntVector northEast(){
        return new IntVector(x + 1, y + 1);
    }

    public IntVector east(){
        return new IntVector(x + 1, y);
    }

    public IntVector southEast(){
        return new IntVector(x + 1, y - 1);
    }

    public IntVector south(){
        return new IntVector(x, y - 1);
    }

    public IntVector southWest(){
        return new IntVector(x - 1, y - 1);
    }

    public IntVector west(){
        return new IntVector(x - 1, y);
    }

    public IntVector northWest(){
        return new IntVector(x - 1, y + 1);
    }

    public IntVector[] primaryNeighbors(){
        return new IntVector[]{north(), east(), south(), west()};
    }

    public IntVector[] eightNeighbors(){
        return new IntVector[]{north(), northEast(), east(), southEast(),
                south(), southWest(), west(), northWest()};
    }

    public IntVector[] diagonalNeighbors(){
        return new IntVector[]{northEast(), southEast(), southWest(), northWest()};
    }

    public Rect originRect(){
        return new Rect(0, 0, x, y);
    }

}


