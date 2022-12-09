package com.maydaymemory.ar.util;

import com.mojang.math.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

import java.nio.FloatBuffer;

public class MatrixUtil {
    /**
     * Fills the given result matrix with the product of the given matrices.
     *
     * @param a The first matrix
     * @param b The second matrix
     * @param m The result matrix
     * @return The result matrix
     */
    public static float[] mul4x4(float a[], float b[], float m[]) //todo just for testing
    {
        Matrix4f m1 = new Matrix4f(a);
        Matrix4f m2 = new Matrix4f(b);
        m1.multiply(m2);
        return fromMatrix4f(m1);
    }

    public static float[] fromMatrix4x4(AIMatrix4x4 matrix4x4){
        float[] matrix= new float[16];
        matrix[0] = matrix4x4.a1();
        matrix[1] = matrix4x4.a2();
        matrix[2] = matrix4x4.a3();
        matrix[3] = matrix4x4.a4();
        matrix[4] = matrix4x4.b1();
        matrix[5] = matrix4x4.b2();
        matrix[6] = matrix4x4.b3();
        matrix[7] = matrix4x4.b4();
        matrix[8] = matrix4x4.c1();
        matrix[9] = matrix4x4.c2();
        matrix[10] = matrix4x4.c3();
        matrix[11] = matrix4x4.c4();
        matrix[12] = matrix4x4.d1();
        matrix[13] = matrix4x4.d2();
        matrix[14] = matrix4x4.d3();
        matrix[15] = matrix4x4.d4();
        return matrix;
    }

    public static float[] fromMatrix4f(Matrix4f matrix4f){
        FloatBuffer pBuffer = FloatBuffer.allocate(16);
        matrix4f.store(pBuffer);
        return load(pBuffer);
    }

    public static void store(float[] matrix, FloatBuffer pBuffer) {
        pBuffer.put(bufferIndex(0, 0), matrix[0]);
        pBuffer.put(bufferIndex(0, 1), matrix[1]);
        pBuffer.put(bufferIndex(0, 2), matrix[2]);
        pBuffer.put(bufferIndex(0, 3), matrix[3]);
        pBuffer.put(bufferIndex(1, 0), matrix[4]);
        pBuffer.put(bufferIndex(1, 1), matrix[5]);
        pBuffer.put(bufferIndex(1, 2), matrix[6]);
        pBuffer.put(bufferIndex(1, 3), matrix[7]);
        pBuffer.put(bufferIndex(2, 0), matrix[8]);
        pBuffer.put(bufferIndex(2, 1), matrix[9]);
        pBuffer.put(bufferIndex(2, 2), matrix[10]);
        pBuffer.put(bufferIndex(2, 3), matrix[11]);
        pBuffer.put(bufferIndex(3, 0), matrix[12]);
        pBuffer.put(bufferIndex(3, 1), matrix[13]);
        pBuffer.put(bufferIndex(3, 2), matrix[14]);
        pBuffer.put(bufferIndex(3, 3), matrix[15]);
    }

    public static float[] load(FloatBuffer pBuffer){
        float[] matrix= new float[16];
        matrix[0] = pBuffer.get(bufferIndex(0, 0));
        matrix[1] = pBuffer.get(bufferIndex(0, 1));
        matrix[2] = pBuffer.get(bufferIndex(0, 2));
        matrix[3] = pBuffer.get(bufferIndex(0, 3));
        matrix[4] = pBuffer.get(bufferIndex(1, 0));
        matrix[5] = pBuffer.get(bufferIndex(1, 1));
        matrix[6] = pBuffer.get(bufferIndex(1, 2));
        matrix[7] = pBuffer.get(bufferIndex(1, 3));
        matrix[8] = pBuffer.get(bufferIndex(2, 0));
        matrix[9] = pBuffer.get(bufferIndex(2, 1));
        matrix[10] = pBuffer.get(bufferIndex(2, 2));
        matrix[11] = pBuffer.get(bufferIndex(2, 3));
        matrix[12] = pBuffer.get(bufferIndex(3, 0));
        matrix[13] = pBuffer.get(bufferIndex(3, 1));
        matrix[14] = pBuffer.get(bufferIndex(3, 2));
        matrix[15] = pBuffer.get(bufferIndex(3, 3));
        return matrix;
    }

    private static int bufferIndex(int pX, int pY) {
        return pY * 4 + pX;
    }
}
