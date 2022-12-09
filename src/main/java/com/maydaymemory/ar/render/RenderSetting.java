package com.maydaymemory.ar.render;

/*
 * The following code is from project MCgLTF, using MIT license
 * url: https://github.com/ModularMods/MCglTF
 */
import com.maydaymemory.ar.util.Buffers;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public enum RenderSetting {
    INSTANCE;

    public final String vertexProgram =
                    """
                    #version 330

                    layout (location=0) in vec3 position;
                    layout (location=1) in vec2 texCoord;

                    out vec2 outTexCoord;

                    uniform mat4 projectionMatrix;
                    uniform mat4 extraMatrix;

                    void main()
                    {
                        gl_Position = projectionMatrix * extraMatrix * vec4(position, 1.0);
                        outTexCoord = texCoord;
                    }
                    """;

    public final String fragmentProgram =
                    """
                    #version 330

                    in  vec2 outTexCoord;
                    out vec4 fragColor;

                    uniform sampler2D texture_sampler;

                    void main()
                    {
                        fragColor = texture(texture_sampler, outTexCoord);
                    }
                    """;

    public final int defaultColorMap;
    {
        defaultColorMap = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, defaultColorMap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 2, 2, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Buffers.create(new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
    }

    public final int defaultNormalMap;
    {
        defaultNormalMap = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, defaultNormalMap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 2, 2, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Buffers.create(new byte[]{-128, -128, -1, -1, -128, -128, -1, -1, -128, -128, -1, -1, -128, -128, -1, -1}));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
    }

    public final int defaultSpecularMap = 0;
}
