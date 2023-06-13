package mchorse.game.render;

import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.colors.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class Batcher2D
{
    private static final Color c1 = new Color();
    private static final Color c2 = new Color();
    private static final Color c3 = new Color();
    private static final Color c4 = new Color();

    private RenderingContext context;
    private VAOBuilder builder;

    private VBOAttributes attributes;
    private Link texture;

    private Vector4f vector4 = new Vector4f();
    private Vector2f vector2 = new Vector2f();

    public Batcher2D(RenderingContext context)
    {
        this.context = context;
    }

    public Vector2f process(MatrixStack stack, float x, float y)
    {
        this.vector4.set(x, y, 0F, 1F);

        stack.getModelMatrix().transform(this.vector4);

        return this.vector2.set(this.vector4.x, this.vector4.y);
    }

    public void icon(MatrixStack stack, Icon icon, int color, float x, float y, float w, float h)
    {
        this.texturedBox(stack, icon.link, color, x, y, w, h, icon.x, icon.y, icon.x + icon.w, icon.y + icon.h, icon.textureW, icon.textureH);
    }

    public void texturedBox(MatrixStack stack, Link texture, int color, float x, float y, float w, float h, float u1, float v1, float u2, float v2, int textureW, int textureH)
    {
        this.begin(VBOAttributes.VERTEX_UV_RGBA_2D, texture);

        c1.set(color);

        Vector2f p = this.process(stack, x, y);
        float x1 = p.x;
        float y1 = p.y;

        p = this.process(stack, x + w, y);
        float x2 = p.x;
        float y2 = p.y;

        p = this.process(stack, x, y + h);
        float x3 = p.x;
        float y3 = p.y;

        p = this.process(stack, x + w, y + h);
        float x4 = p.x;
        float y4 = p.y;

        /* 0, 1, 2, 0, 2, 3 */
        this.builder.xy(x3, y3).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x4, y4).uv(u2, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x2, y2).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x3, y3).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x2, y2).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x1, y1).uv(u1, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
    }

    public void box(MatrixStack stack, int color, float x, float y, float w, float h)
    {
        this.begin(VBOAttributes.VERTEX_RGBA_2D, null);

        c1.set(color);
        c2.set(color);
        c3.set(color);
        c4.set(color);

        Vector2f p = this.process(stack, x, y);
        float x1 = p.x;
        float y1 = p.y;

        p = this.process(stack, x + w, y);
        float x2 = p.x;
        float y2 = p.y;

        p = this.process(stack, x, y + h);
        float x3 = p.x;
        float y3 = p.y;

        p = this.process(stack, x + w, y + h);
        float x4 = p.x;
        float y4 = p.y;

        /* c1 ---- c2
         * |        |
         * c3 ---- c4 */
        this.builder.xy(x1, y1).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x3, y3).rgba(c3.r, c3.g, c3.b, c3.a);
        this.builder.xy(x4, y4).rgba(c4.r, c4.g, c4.b, c4.a);
        this.builder.xy(x1, y1).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x4, y4).rgba(c4.r, c4.g, c4.b, c4.a);
        this.builder.xy(x2, y2).rgba(c2.r, c2.g, c2.b, c2.a);
    }

    public void text(MatrixStack stack, FontRenderer font, String label, float x, float y, int color, boolean shadow)
    {
        this.begin(VBOAttributes.VERTEX_UV_RGBA_2D, font.texture);

        font.build(stack, this.builder, label, (int) x, (int) y, 0, color, shadow);
    }

    private void begin(VBOAttributes attributes, Link texture)
    {
        if (attributes == this.attributes && this.texture == texture)
        {
            return;
        }

        this.render();

        this.builder = this.context.getVAO().setup(this.context.getShaders().get(attributes));
        this.attributes = attributes;
        this.texture = texture;

        this.builder.begin();
    }

    public void render()
    {
        if (this.attributes == null)
        {
            return;
        }

        if (this.texture != null)
        {
            this.context.getTextures().bind(this.texture);
        }

        this.builder.render();

        this.attributes = null;
        this.texture = null;
    }

    public void dropCircleShadow(MatrixStack stack, int x, int y, int radius, int segments, int opaque, int shadow)
    {
        c1.set(opaque);
        c2.set(shadow);

        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));
        Vector2f p = this.process(stack, x, y);

        builder.begin();
        builder.xy(p.x, p.y).rgba(c1.r, c1.g, c1.b, c1.a);

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            p = this.process(stack, (float) (x - Math.cos(a) * radius), (float) (y + Math.sin(a) * radius));

            builder.xy(p.x, p.y).rgba(c2.r, c2.g, c2.b, c2.a);
        }

        builder.render(GL11.GL_TRIANGLE_FAN);
    }
}