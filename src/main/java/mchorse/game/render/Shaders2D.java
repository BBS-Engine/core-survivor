package mchorse.game.render;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.ubo.ProjectionViewUBO;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import org.joml.Matrix4f;

public class Shaders2D
{
    /**
     * Projection view UBO (32 bits, projection and view matrices)
     */
    public ProjectionViewUBO ubo;

    /**
     * Orthographic (flat/2D) projection matrix
     */
    public Matrix4f ortho = new Matrix4f();

    /**
     * Colored 2D shader that supports {@link VBOAttributes#VERTEX_RGBA_2D} layout
     */
    public Shader vertexRGBA2D;

    /**
     * Textured 2D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA_2D} layout
     */
    public Shader vertexUVRGBA2D;

    public Shaders2D()
    {
        this.ubo = new ProjectionViewUBO(1);
        this.ubo.init();
        this.ubo.bind();

        this.vertexRGBA2D = new Shader(Link.assets("shaders/ui/vertex_rgba_2d.glsl"), VBOAttributes.VERTEX_RGBA_2D);
        this.vertexUVRGBA2D = new Shader(Link.assets("shaders/ui/vertex_uv_rgba_2d.glsl"), VBOAttributes.VERTEX_UV_RGBA_2D);

        this.vertexRGBA2D.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexUVRGBA2D.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
    }

    public void resize(int width, int height)
    {
        int scale = BBSSettings.getScale();

        this.ortho.setOrtho(0, width / scale, height / scale, 0, -100, 100);
    }
}