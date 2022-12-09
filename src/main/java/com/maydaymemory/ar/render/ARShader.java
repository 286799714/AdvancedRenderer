package com.maydaymemory.ar.render;

import com.maydaymemory.ar.util.MatrixUtil;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDetachShader;

/**
 * Unfinished!
 * */
public class ARShader {
    public record ShaderModuleData(String program, int shaderType) {}

    private int programId;

    private final Map<String, Integer> uniforms = new HashMap<>();

    private final List<ShaderModuleData> shaderModuleDataList;

    public ARShader(List<ShaderModuleData> shaderModuleDataList){
        this.shaderModuleDataList = shaderModuleDataList;
    }

    public void load(){
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }

        List<Integer> shaderModules = new ArrayList<>();
        shaderModuleDataList.forEach(s -> shaderModules.add(createShader(s.program, s.shaderType)));
        link(shaderModules);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private void link(List<Integer> shaderModules) {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        shaderModules.forEach(s -> glDetachShader(programId, s));
        shaderModules.forEach(GL30::glDeleteShader);
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId,
                uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" +
                    uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public int getProgramId() {
        return programId;
    }

    public void setTransformMatrix(float[] matrix){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            MatrixUtil.store(matrix, fb);
            glUniformMatrix4fv(uniforms.get("extraMatrix"), false, fb); //todo
        }
    }

    public void setProjectionMatrix(float[] matrix){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            MatrixUtil.store(matrix, fb);
            glUniformMatrix4fv(uniforms.get("projectionMatrix"), false, fb); //todo
        }
    }

    public void setTextureUnit(int i){
        glUniform1i(uniforms.get("texture_sampler"), i);
    }
}
