package com.atasoft.utilities;

/**
 * Created by ataboo on 2015-12-15.
 */
 public class AtaVector{
    public enum OriginMode{
        /** Angle measured clockwise from x-axis */
        X_AXIS_CLOCK,
        /** Angle measured anti-clockwise from x-axis */
        X_AXIS_ANTI,
        /** Angle measured clockwise from y-axis */
        Y_AXIS_CLOCK,
        /** Angle measured anti-clockwise from y-axis */
        Y_AXIS_ANTI
    }

    public static final AtaVector Zero = new AtaVector(0,0);

    public float x;
    public float y;

    public AtaVector(float x, float y){
        this.x = x;
        this.y = y;
    }

    public AtaVector(IntVector intVector){
        this((float)intVector.x, (float)intVector.y);
    }

    /** Finds the angle to a target gridPos from this gridPos */
    public float bearingToTarget(AtaVector targetPosition) {
        // Get vector pointing to target pos from origin
        AtaVector deltaVect = targetPosition.sub(this);

        // Swap x and y to switch from x+ axis origin anti-clockwise to y+ axis origin clockwise
        float angle = (float) Math.toDegrees(deltaVect.atan2());
        //Gdx.app.log("AtaVector", String.format("ref: (%.2f, %.2f), targ: (%.2f, %.2f), angle: %.2f", x, y, gridPosition.x, gridPosition.y, angle));
        //Gdx.app.log("AtaVector", String.format("deltaVect: (%.2f, %.2f)", deltaVect.x, deltaVect.y));
        return angle;
    }

    public AtaVector cpy(){
        return new AtaVector(x, y);
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if (!(obj instanceof AtaVector)){
            return false;
        }
        AtaVector ataVector = (AtaVector) obj;
        return x == ataVector.x && y == ataVector.y;
    }

    public int hashCode(){
        int hash = 23;
        hash = 42 * hash + Float.floatToIntBits(x);
        hash = 42 * hash + Float.floatToIntBits(y);
        return hash;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public AtaVector normalize(){
        float magnitude = magnitude();
        return this.scale(1f / magnitude);
    }

    public float magSqr(){
        return x * x + y * y;
    }
    public float magnitude(){
        return (float) Math.sqrt(magSqr());
    }

    public AtaVector sub(AtaVector subVector){
        return(new AtaVector(x - subVector.x, y - subVector.y));
    }
    public AtaVector sub(float subX, float subY){
        return new AtaVector(x - subX, y - subY);
    }

    public AtaVector add(AtaVector addVector){
        return new AtaVector(x + addVector.x, y + addVector.y);
    }

    public AtaVector add(float addX, float addY){
        return new AtaVector(x + addX, y + addY);
    }

    public AtaVector scale(float scalar){
        return new AtaVector(x * scalar, y * scalar);
    }

    public float atan2(OriginMode originMode) {
        double angle;
        switch (originMode){
            case Y_AXIS_CLOCK:default:
                //noinspection SuspiciousNameCombination
                angle = Math.atan2(x, y);
                break;
            case Y_AXIS_ANTI:
                //noinspection SuspiciousNameCombination
                angle = -Math.atan2(x, y);
                break;
            case X_AXIS_ANTI:
                angle = Math.atan2(y, x);
                break;
            case X_AXIS_CLOCK:
                angle =  -Math.atan2(y, x);
                break;
        }
        return (float) Math.toDegrees(angle);
    }

    public static AtaVector unitFromAngle(float angle, OriginMode originMode){
        double radians = Math.toRadians(angle);
        float sinComponent = (float) Math.sin(radians);
        float cosComponent = (float) Math.cos(radians);
        switch (originMode){
            case Y_AXIS_CLOCK:default:
                return new AtaVector(sinComponent, cosComponent);
            case Y_AXIS_ANTI:
                return new AtaVector(-sinComponent, cosComponent);
            case X_AXIS_ANTI:
                return new AtaVector(cosComponent, sinComponent);
            case X_AXIS_CLOCK:
                return new AtaVector(cosComponent, -sinComponent);
        }
    }

    public float atan2(){
        return atan2(OriginMode.Y_AXIS_CLOCK);
    }

    public static AtaVector Zero() {
        return new AtaVector(0, 0);
    }

}
