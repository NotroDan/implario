package net.minecraft.util;

public interface Location {
    double x();

    double y();

    double z();

    default float yaw(){
        return 0;
    }

    default float pitch(){
        return 0;
    }
}
