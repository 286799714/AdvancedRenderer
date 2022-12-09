package com.maydaymemory.ar.render;

import com.maydaymemory.ar.util.MatrixUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ARNode extends RenderComponent implements Cloneable{
    private List<ARNode> children;
    private List<ARMesh> meshes;
    private RenderComponent parent;
    private String name;

    protected ARNode(AINode node, AIScene scene){
        init(node, scene);
    }

    protected void init(AINode node, AIScene scene){
        children = new ArrayList<>();
        meshes = new ArrayList<>();

        IntBuffer meshesIndexes = node.mMeshes();
        PointerBuffer meshes = scene.mMeshes();
        if(meshesIndexes != null && meshes != null) {
            int numMesh = node.mNumMeshes();
            for (int i = 0; i < numMesh; i++) {
                int meshIndex = meshesIndexes.get(i);
                AIMesh mesh = AIMesh.create(meshes.get(meshIndex));
                ARMesh arMesh = new ARMesh(mesh, scene);
                arMesh.setParent(this);
                this.meshes.add(arMesh);
            }
        }

        PointerBuffer children = node.mChildren();
        int childrenNum = node.mNumChildren();
        for(int i = 0; i < childrenNum; i++){
            assert children != null;
            AINode child = AINode.create(children.get(i));
            ARNode childNode = new ARNode(child, scene);
            childNode.parent = this;
            this.children.add(childNode);
        }

        float[] matrix= MatrixUtil.fromMatrix4x4(node.mTransformation());
        this.setMatrix(matrix);

        name = node.mName().dataString();
    }

    public void load(){
        for(ARMesh mesh : meshes){
            mesh.load();
        }
        for(ARNode child : children){
            child.load();
        }
    }

    void setParent(RenderComponent parent) {
        this.parent = parent;
    }

    /**
     * @param transform When rendering, only the relative transformation of this node will be applied, and the global transformation will not be calculated.
     *                  See calculateGlobalTransform() if you want to get global transform. Wish you good!
     * */
    @Override
    public void render(ARShader shader, float[] projectionMatrix, float[] transform) {
        if(!isVisible()) return;
        float[] alpha = MatrixUtil.mul4x4(transform, getMatrix(), new float[16]);
        for(ARMesh mesh : meshes){
            mesh.render(shader, projectionMatrix, alpha);
        }
        for(ARNode child : children){
            child.render(shader, projectionMatrix, alpha);
        }
    }

    /**
     * @return maybe an ARNode, or an ARModel if this node is root.
     * */
    public RenderComponent getParent() {
        return parent;
    }

    /**
     * @return Return the first node(itself or its children) found that the name is mapped.
     * @param deep if false, search the immediate children only.
     * */
    public ARNode findNode(String name, boolean deep){
        if(this.name.equals(name)) return this;
        if(deep){
            for(ARNode child : children){
                ARNode result = child.findNode(name, true);
                if(result!=null) return result;
            }
        }else {
            for(ARNode child : children){
                if(child.name.equals(name)) return child;
            }
        }
        return null;
    }

    /**
     * @param node Could be the node from this scene, or the node from other scenes.
     *             The parent of the node will be set to current node.
     *             So please call clone() before or else the origin model structure will be changed.
     * */
    public void addChild(ARNode node){
        node.parent = this;
        children.add(node);
    }

    public String getName() {
        return name;
    }

    /**
     * Almost shallow copy. The children node and children mesh will not be copied. But references to them will be detached.
     * */
    @Override
    public ARNode clone() {
        try {
            ARNode clone = (ARNode) super.clone();
            clone.children = new ArrayList<>(this.children);
            clone.parent = this.parent;
            clone.meshes = new ArrayList<>(this.meshes);
            clone.name = this.name;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
