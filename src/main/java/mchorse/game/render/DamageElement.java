package mchorse.game.render;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.FontRenderer;
import org.joml.Vector2f;

public class DamageElement extends RenderElement
{
    public String label;

    public DamageElement(String damage, Vector2f position)
    {
        super(position);

        this.label = damage;
    }

    @Override
    protected float getScale(float transition)
    {
        return super.getScale(transition) * 1.5F;
    }

    protected void renderElement(RenderingContext context, Batcher2D batcher)
    {
        FontRenderer font = context.getFont();
        int w = font.getWidth(this.label);

        batcher.text(context.stack, context.getFont(), this.label, -w / 2, -font.getHeight() / 2, 0xffaaffff, true);
    }
}