package mchorse.game.render;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;
import org.joml.Vector2f;

public abstract class RenderElement
{
    public Vector2f position = new Vector2f();
    public int ticks = 10;

    public RenderElement(Vector2f position)
    {
        this.position.set(position);
        this.position.add((float) (Math.random() * 16 - 8), (float) (Math.random() * 16 - 8));
    }

    public boolean canBeRemoved()
    {
        return this.ticks < 0;
    }

    public void update()
    {
        this.ticks -= 1;
    }

    public void render(RenderingContext context, Batcher2D batcher)
    {
        float s = this.getScale(context.getTransition());

        context.stack.push();
        context.stack.translate(this.position.x, this.position.y, 0);
        context.stack.scale(s, s, s);

        this.renderElement(context, batcher);

        context.stack.pop();
    }

    protected float getScale(float transition)
    {
        return Interpolation.EXP_INOUT.interpolate(0F, 1F, Interpolations.envelope(this.ticks - transition, 0, 3, 7, 10));
    }

    protected abstract void renderElement(RenderingContext context, Batcher2D batcher);
}
