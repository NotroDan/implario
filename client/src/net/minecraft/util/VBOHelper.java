package net.minecraft.util;

import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

@UtilityClass
public class VBOHelper {

	public int create(float[] vertices, int dim) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		GL20.glVertexAttribPointer(0, dim, GL11.GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		return vaoId;
	}

	public int create(double[] vertices, int dim) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		DoubleBuffer verticesBuffer = BufferUtils.createDoubleBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		GL20.glVertexAttribPointer(0, dim, GL11.GL_DOUBLE, false, 0, 0);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		return vaoId;
	}

	public void draw(int vaoid, int method, int from, int size) {

		// Bind to the VAO that has all the information about the quad vertices
		GL30.glBindVertexArray(vaoid);
		GL20.glEnableVertexAttribArray(0);

		// Draw the vertices
		GL11.glDrawArrays(method, from, size);

		// Put everything back to default (deselect)
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);

	}



}
