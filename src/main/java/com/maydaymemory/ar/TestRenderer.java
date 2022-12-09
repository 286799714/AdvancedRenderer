package com.maydaymemory.ar;

import com.maydaymemory.ar.util.MatrixUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

public enum TestRenderer {
    INSTANCE;

    float angle = 0;

    public void render(PoseStack poseStack){
        float[] projectionMatrix = MatrixUtil.fromMatrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0F,0f,-1.8f);
        matrix4f.multiply(new Matrix4f(Vector3f.YP.rotationDegrees(angle)));
        float[] worldMatrix = MatrixUtil.fromMatrix4f(matrix4f);
        angle += 0.5;
        if(angle > 360) angle = 0;
        AdvancedRendererMod.renderer.render(projectionMatrix, worldMatrix, AdvancedRendererMod.testModel);
    }
}
