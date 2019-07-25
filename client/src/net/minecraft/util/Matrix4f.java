package net.minecraft.util;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f {

	public Matrix4f(float[] f) {
		this.m00 = f[0];
		this.m01 = f[1];
		this.m02 = f[2];
		this.m03 = f[3];
		this.m10 = f[4];
		this.m11 = f[5];
		this.m12 = f[6];
		this.m13 = f[7];
		this.m20 = f[8];
		this.m21 = f[9];
		this.m22 = f[10];
		this.m23 = f[11];
		this.m30 = f[12];
		this.m31 = f[13];
		this.m32 = f[14];
		this.m33 = f[15];
	}

	public Matrix4f() {
		this.m00 = this.m01 = this.m02 = this.m03 =
				this.m10 = this.m11 = this.m12 = this.m13 =
						this.m20 = this.m21 = this.m22 = this.m23 =
								this.m30 = this.m31 = this.m32 = this.m33 = 0.0F;
	}

}
