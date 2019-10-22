package net.minecraft.util;

public class Vec3f implements Location{
    public final float x, y, z;

    public Vec3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }
}
