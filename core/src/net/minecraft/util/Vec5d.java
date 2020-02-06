package net.minecraft.util;

import oogle.util.byteable.Decoder;
import oogle.util.byteable.Encoder;

public class Vec5d implements Location{
    public final double x, y, z;
    public final float yaw, pitch;

    public Vec5d(double x, double y, double z, float yaw, float pitch){
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Vec5d(Decoder decoder){
        x = decoder.readDouble();
        y = decoder.readDouble();
        z = decoder.readDouble();
        yaw = decoder.readFloat();
        pitch = decoder.readFloat();
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

    @Override
    public float yaw() {
        return yaw;
    }

    @Override
    public float pitch() {
        return pitch;
    }

    public void encode(Encoder encoder){
        encoder.writeDouble(x).writeDouble(y).writeDouble(z).writeFloat(yaw).writeFloat(pitch);
    }
}
