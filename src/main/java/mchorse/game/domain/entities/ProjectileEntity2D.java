package mchorse.game.domain.entities;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.colors.Colors;
import mchorse.game.render.Batcher2D;
import org.joml.Vector2f;

public class ProjectileEntity2D extends LivingEntity2D
{
    public Entity2D target;
    public float damage = 1F;
    public float speed = 4F;

    public Vector2f direction = new Vector2f();

    public ProjectileEntity2D(Entity2D target)
    {
        this.target = target;

        this.hitbox.setSize(2, 2);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.dying)
        {
            this.addPosition(this.direction.x, this.direction.y);

            return;
        }

        if (this.target.canBeRemoved())
        {
            this.addPosition(this.direction.x, this.direction.y);

            if (this.ticks >= 60)
            {
                this.kill();
            }
        }
        else if (this.target.hitbox.intersects(this.hitbox))
        {
            this.kill();

            if (this.target instanceof LivingEntity2D)
            {
                ((LivingEntity2D) this.target).damage(this.damage);
            }
        }
        else if (!this.dying)
        {
            Vector2f mul = this.direction.set(this.target.position).sub(this.position).normalize().mul(this.speed);

            this.addPosition(mul.x, mul.y);
        }

        for (Entity2D entity : this.state.getEntitiesInBox(this.hitbox, (e) -> e instanceof Mob2D))
        {
            this.kill();
            ((Mob2D) entity).damage(this.damage);
        }
    }

    @Override
    public void renderEntity(RenderingContext context, Batcher2D batcher)
    {
        int w = (int) (this.hitbox.w / 2F);
        int h = (int) (this.hitbox.h / 2F);

        batcher.box(context.stack, Colors.WHITE, -w, -h, w, h);
    }
}