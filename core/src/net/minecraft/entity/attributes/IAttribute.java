package net.minecraft.entity.attributes;

public interface IAttribute
{
    String getAttributeUnlocalizedName();

    double clampValue(double p_111109_1_);

    double getDefaultValue();

    boolean getShouldWatch();

    IAttribute func_180372_d();
}
