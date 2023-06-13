package mchorse.game.domain.entities;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.game.domain.GameState;
import mchorse.game.domain.utils.Hitbox;
import mchorse.game.render.Batcher2D;
import org.joml.Vector2f;

public abstract class Entity2D
{
    public final Vector2f position = new Vector2f();
    public final Vector2f prevPosition = new Vector2f();
    public final Hitbox hitbox = new Hitbox();
    public final Vector2f velocity = new Vector2f();

    public int ticks;

    public GameState state;

    private boolean remove;

    public Entity2D()
    {
        this.hitbox.setSize(20, 20);
    }

    public void addPosition(float x, float y)
    {
        this.setPosition(this.position.x + x, this.position.y + y);
    }

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
        this.hitbox.setPos(x - this.hitbox.w / 2, y - this.hitbox.h / 2);
    }

    public void teleport(float x, float y)
    {
        this.setPosition(x, y);
        this.prevPosition.set(this.position);
    }

    public boolean canBeRemoved()
    {
        return this.remove;
    }

    public void remove()
    {
        this.remove = true;
    }

    public void update()
    {
        this.prevPosition.set(this.position);
        this.setPosition(this.position.x, this.position.y);

        this.position.add(this.velocity);
        this.velocity.mul(0.95F);

        this.ticks += 1;
    }

    public void render(RenderingContext context, Batcher2D batcher)
    {
        Vector2f vec = Vectors.TEMP_2F.set(this.prevPosition).lerp(this.position, context.getTransition());
        float scale = this.getScale(context);

        context.stack.push();
        context.stack.translate(vec.x, vec.y, 0);

        this.renderPreScale(context, batcher);

        context.stack.scale(scale, scale, scale);

        this.renderEntity(context, batcher);

        batcher.box(context.stack, 0, 0, this.hitbox.w, this.hitbox.h, 0xff8800ff);

        context.stack.pop();

        context.stack.push();
        context.stack.translate(this.hitbox.x, this.hitbox.y, 0);



        context.stack.pop();
    }

    protected float getScale(RenderingContext context)
    {
        return Interpolation.ELASTIC_OUT.interpolate(0F, 1F, Math.min((this.ticks + context.getTransition()) / 15F, 1F));
    }

    protected void renderPreScale(RenderingContext context, Batcher2D batcher)
    {}

    public abstract void renderEntity(RenderingContext context, Batcher2D batcher);
}