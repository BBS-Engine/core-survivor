package mchorse.game.domain.entities;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.game.render.Batcher2D;
import org.joml.Vector2f;

public class Mob2D extends LivingEntity2D
{
    public float speed = 1F;
    public int color = 0xffff8800;
    public int freeze;

    public Mob2D()
    {
        super();

        this.hitbox.setSize(14, 14);
    }

    public boolean isFreezing()
    {
        return this.freeze > 0;
    }

    @Override
    public void kill()
    {
        boolean dying = this.dying;

        super.kill();

        if (!dying && Math.random() < this.state.getStats().crystalDropRate.get())
        {
            this.state.postCrystalDrop(this);
        }

        this.state.addEnergy((int) this.getOriginalHealth());

        this.state.postSound(Link.create("game:sounds/mob_death.ogg"), 0.25F);
    }

    @Override
    public void update()
    {
        if (this.isFreezing())
        {
            this.velocity.set(0, 0);
            this.freeze -= 1;
        }

        super.update();

        if (this.dying)
        {
            return;
        }

        CoreEntity2D core = this.state.getCore();

        if (core != null)
        {
            if (this.hitbox.intersects(core.hitbox))
            {
                this.kill();
                this.state.reduceHealth();
            }
            else if (!this.isFreezing())
            {
                Vector2f direction = new Vector2f(core.position).sub(this.position).normalize().mul(this.speed);

                this.position.add(direction);
            }
        }
    }

    @Override
    protected float getScale(RenderingContext context)
    {
        return super.getScale(context) * (this.hitbox.w / 14F);
    }

    @Override
    public void renderEntity(RenderingContext context, Batcher2D batcher)
    {
        batcher.icon(context.stack, Icons.CYLINDER, this.isFreezing() ? 0xff44aaff : this.color, -8, -8, 16, 16);
    }
}