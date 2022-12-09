package com.maydaymemory.ar.render;

import com.maydaymemory.ar.util.MatrixUtil;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.glUseProgram;

public class ARModel extends RenderComponent{
    private final List<ARNode> rootNodes = new ArrayList<>();
    private final AIScene scene;

    public ARModel(AIScene scene) {
        addRootNode(new ARNode(scene.mRootNode(), scene));
        this.scene = scene;
    }

    /**
     * @param rootNode Could be the node from this scene, or the node from other scenes.
     *                 The parent of the node will be set to current model.
     *                 So please call clone() before or else the origin model structure will be changed.
     * */
    public void addRootNode(ARNode rootNode){
        rootNode.setParent(this);
        rootNodes.add(rootNode);
    }

    public List<ARNode> getRootNodes(){
        return rootNodes;
    }

    public ARNode findFirstNodeByName(String name){
        for(ARNode node : rootNodes){
            ARNode result = node.findNode(name, true);
            if(result != null) return result;
        }
        return null;
    }

    public void load(){
        for(ARNode node : rootNodes){
            node.load();
        }
    }

    @Override
    public void render(ARShader shader, float[] projectionMatrix, float[] transform) {
        if(!isVisible()) return;
        int prevShader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        glUseProgram(shader.getProgramId());
        shader.setProjectionMatrix(projectionMatrix);
        float[] alpha = MatrixUtil.mul4x4(transform, getMatrix(), new float[16]);
        for(ARNode node : rootNodes){
            node.render(shader, projectionMatrix, alpha);
        }
        glUseProgram(prevShader);
    }

    /**
     * @return null
     * */
    @Override
    protected RenderComponent getParent() {
        return null;
    }

    public AIScene getScene() {
        return scene;
    }
}
