package net.minecraft.util;

import lombok.Data;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static java.lang.Math.*;

@UtilityClass
public class VBOHelper {

	public int create(short[] vertices, int dim) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		ShortBuffer verticesBuffer = BufferUtils.createShortBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		glVertexAttribPointer(0, dim, GL_SHORT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);
		return vaoId;
	}

	@Data
	public static class VBO {
		public final int vaoId;
		public final int vboId;
	}

	public VBO create2Dtextured(float[] data) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(data.length);
		verticesBuffer.put(data);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		int lol = 8;
//		glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * lol, 0);
//		glTexCoordPointer(2, GL_FLOAT, 2 * lol, lol);
		// Deselect (bind to 0) the VBO

		// Deselect (bind to 0) the VAO
//		glBindVertexArray(0);
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return new VBO(vaoId, vboId);
	}


	public void draw2DTextured(VBO vbo, int method, int from, int size) {

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		// Bind to the VAO that has all the information about the quad vertices
		glBindBuffer(GL_ARRAY_BUFFER, vbo.vboId);
		glTexCoordPointer(2, GL_FLOAT, 16, 8);
		glVertexPointer(2, GL_FLOAT, 16, 0);
		glBindVertexArray(vbo.vaoId);
		glDrawArrays(method, from, size);
//		glEnableVertexAttribArray(0);


		// Draw the vertices
//		GL12.glDrawRangeElements(method, from, from + size, size, GL_FLOAT, 0);


		// Put everything back to default (deselect)
//		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}

	public int create(float[] vertices, int dim) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		glVertexAttribPointer(0, dim, GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);
		return vaoId;
	}

	public int create(double[] vertices, int dim) {

		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		DoubleBuffer verticesBuffer = BufferUtils.createDoubleBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		glVertexAttribPointer(0, dim, GL_DOUBLE, false, 0, 0);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);
		return vaoId;
	}

	public void draw(int vaoid, int method, int from, int size) {

		// Bind to the VAO that has all the information about the quad vertices
		glBindVertexArray(vaoid);
		glEnableVertexAttribArray(0);

		// Draw the vertices
		glDrawArrays(method, from, size);

		// Put everything back to default (deselect)
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);

	}

	public int circle(int verts) {

		double radius1 = 0.2f, radius2 = 0.16f;

		double[] array = new double[verts * 8 + 4];

		int a = 0;
		for (double i = 0; i <= 2 * PI; i += PI / (double) verts) {
			array[a] = cos(i) * radius2;
			array[a + 1] = -sin(i) * radius2;
			array[a + 2] = cos(i) * radius1;
			array[a + 3] = -sin(i) * radius1;
			a += 4;
		}
		int l = array.length;
		array[l - 4] = radius2;
		array[l - 3] = 0;
		array[l - 2] = radius1;
		array[l - 1] = 0;

		return VBOHelper.create(array, 2);
	}


	public static void clear(int vao) {
		glDeleteBuffers(vao);
	}

}
