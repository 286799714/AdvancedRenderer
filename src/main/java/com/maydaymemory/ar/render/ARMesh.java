package com.maydaymemory.ar.render;

import com.maydaymemory.ar.util.MatrixUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;


public class ARMesh extends RenderComponent{
    private final List<Float> vertices = new ArrayList<>();
    private final List<Float> uv = new ArrayList<>();
    private final List<Integer> ind = new ArrayList<>();
    private ARMaterial material;
    private ARNode parent;

    private int vaoId = -1;

    protected ARMesh(AIMesh mesh, AIScene scene){
        init(mesh, scene);
    }

    protected void init(AIMesh mesh, AIScene scene){
        AIVector3D.Buffer vertices = mesh.mVertices();
        int numVertices = mesh.mNumVertices();
        for(int i = 0; i < numVertices; i++){
            AIVector3D ver = vertices.get(i);
            this.vertices.add(ver.x());
            this.vertices.add(ver.y());
            this.vertices.add(ver.z());
        }

        AIVector3D.Buffer texcoords = mesh.mTextureCoords(0);
        if(texcoords != null) while (texcoords.remaining() > 0) {
            AIVector3D textCoord = texcoords.get();
            uv.add(textCoord.x());
            uv.add(1 - textCoord.y());
        }

        AIFace.Buffer faces = mesh.mFaces();
        int numFaces = mesh.mNumFaces();
        for(int i = 0; i < numFaces; i++){
            AIFace face = faces.get(i);
            IntBuffer indices = face.mIndices();
            int numIndices = face.mNumIndices();
            for(int j = 0; j < numIndices; j++){
                this.ind.add(indices.get(j));
            }
        }

        PointerBuffer aiMaterials = scene.mMaterials();
        if(aiMaterials != null) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(mesh.mMaterialIndex()));
            material = new ARMaterial(aiMaterial, scene);
        }
    }

    public void load(){
        material.load();

        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        try {
            verticesBuffer = memAllocFloat(vertices.size());
            for(float position : vertices){
                verticesBuffer.put(position);
            }
            verticesBuffer.flip();
            indicesBuffer = MemoryUtil.memAllocInt(ind.size());
            for(int indice : ind){
                indicesBuffer.put(indice);
            }
            indicesBuffer.flip();
            textCoordsBuffer = MemoryUtil.memAllocFloat(uv.size());
            for(float coord : uv){
                textCoordsBuffer.put(coord);
            }
            textCoordsBuffer.flip();

            int currentVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            int currentVBO = GL11.glGetInteger(GL_ARRAY_BUFFER_BINDING);
            int currentEVBO = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            int vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            int idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            int texVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            GL30.glBindVertexArray(currentVAO);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentVBO);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, currentEVBO);
        } finally {
            if (verticesBuffer  != null) {
                memFree(verticesBuffer);
                memFree(indicesBuffer);
                memFree(textCoordsBuffer);
            }
        }
    }

    /**
     * @param transform When rendering, only the relative transformation of this mesh will be applied, and the global transformation will not be calculated.
     *                  See calculateGlobalTransform() if you want to get global transform. Wish you good!
     * */
    @Override
    protected void render(ARShader shader, float[] projectionMatrix, float[] transform) {
        if(!isVisible()) return;
        if(vaoId == -1) throw new RuntimeException("try to render an unloaded mesh.");
        float[] alpha = MatrixUtil.mul4x4(transform, getMatrix(), new float[16]);
        shader.setTransformMatrix(alpha);
        int currentVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(vaoId);
        {
            glActiveTexture(GL_TEXTURE0); //todo "Currently work with only one texture"
            shader.setTextureUnit(0);
            ARTexture diffuse = material.getDiffuse();
            if(diffuse != null) glBindTexture(GL_TEXTURE_2D, diffuse.getTextureId());
            else glBindTexture(GL_TEXTURE_2D, RenderSetting.INSTANCE.defaultNormalMap);
            boolean currentDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            boolean currentBlend = GL11.glGetBoolean(GL11.GL_BLEND);
            GL20.glVertexAttrib4f(1, 1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            glDrawElements(GL_TRIANGLES, ind.size(), GL_UNSIGNED_INT, 0);
            if(!currentDepthTest) GL11.glDisable(GL11.GL_DEPTH_TEST);
            if(!currentBlend) GL11.glDisable(GL11.GL_BLEND);
        }
        glBindVertexArray(currentVAO);
    }

    void setParent(ARNode parent) {
        this.parent = parent;
    }

    @Override
    protected ARNode getParent() {
        return parent;
    }
}
