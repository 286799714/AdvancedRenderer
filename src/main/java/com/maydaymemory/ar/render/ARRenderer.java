package com.maydaymemory.ar.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

/**
 * Unfinished!
 * */
public class ARRenderer {
    private ARShader shader;

    public void load() throws Exception {
        List<ARShader.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ARShader.ShaderModuleData(RenderSetting.INSTANCE.vertexProgram, GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ARShader.ShaderModuleData(RenderSetting.INSTANCE.fragmentProgram, GL_FRAGMENT_SHADER));
        shader = new ARShader(shaderModuleDataList);
        shader.load();

        shader.createUniform("projectionMatrix");
        shader.createUniform("extraMatrix");
        shader.createUniform("texture_sampler");
    }

    public void render(float[] projectionMatrix, float[] worldMatrix, ARModel model){
        int prevShader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        glUseProgram(shader.getProgramId());
        model.render(shader, projectionMatrix, worldMatrix);
        glUseProgram(prevShader);
    }
}
