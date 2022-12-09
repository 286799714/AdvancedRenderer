package com.maydaymemory.ar.render;

import com.maydaymemory.ar.util.MatrixUtil;
import java.util.Stack;

public abstract class RenderComponent {
    private boolean isVisible = true;

    private final float[] matrix = new float[16];
    {
        for(int i = 0; i < 16; i++){
            matrix[i] = 0;
        }
        matrix[0] = 1;
        matrix[5] = 1;
        matrix[10] = 1;
        matrix[15] = 1;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * @return the transform matrix relative to the parent component.
     * */
    public float[] getMatrix() {
        return matrix;
    }

    /**
     * @param matrix the transform matrix relative to the parent component.
     * */
    public void setMatrix(float[] matrix){
        if(matrix.length != 16) throw new  RuntimeException("matrix size is required: 16");
        System.arraycopy(matrix, 0, this.matrix, 0, 16);
    }

    public float[] computeGlobalMatrix(){
        RenderComponent iterator = this;
        Stack<float[]> stack = new Stack<>();
        while (iterator != null){
            stack.push(iterator.getMatrix());
            iterator = iterator.getParent();
        }
        float[] matrix = stack.pop();
        while (!stack.empty()){
            matrix = MatrixUtil.mul4x4(matrix, stack.pop(), new float[16]);
        }
        return matrix;
    }

    protected abstract void render(ARShader shader, float[] projectionMatrix, float[] transform);

    protected abstract RenderComponent getParent();
}
