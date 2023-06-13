package mchorse.game.domain.entities;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.game.render.Batcher2D;
import org.joml.Vector2f;

import java.util.List;

public class CoreEntity2D extends Entity2D
{
    public static Vector2f getOrb(float offset, int ticks, float w, float h, float transition)
    {
        float a = (ticks + transition) / 40F + offset;
        float size = (float) Math.sin(a * 5) * 0.075F + 0.65F;
        float sx = (float) (Math.cos(a) * w * size);
        float sy = (float) (Math.sin(a) * h * size);

        return new Vector2f(sx, sy);
    }

    public static int processRate(int base, float powerBase, float percentage)
    {
        float value = (float) Math.pow(powerBase, -(percentage - 1));
        int output = Math.round(base * value);

        if (output <= 0)
        {
            output = 1;
        }

        return output;
    }

    public CoreEntity2D()
    {
        super();

        this.hitbox.w = 60;
        this.hitbox.h = 60;
    }

    @Override
    public void update()
    {
        super.update();

        if (this.ticks % 20 == 0)
        {
            this.state.addPerSecondEnergy();
        }

        int rate = processRate(30, 2, this.state.getStats().coreFireRate.get());

        if (this.ticks % rate == 0)
        {
            List<Entity2D> closest = this.state.getClosest(this.position, (e) -> e instanceof Mob2D && !((Mob2D) e).dying);

            if (!closest.isEmpty())
            {
                int c = this.state.getStats().coreProjectiles.get();

                for (int i = 0; i < c; i++)
                {
                    Entity2D mob = CollectionUtils.inRange(closest, i) ? closest.get(i) : closest.get(closest.size() - 1);
                    Vector2f orb = getOrb(MathUtils.PI * 2 / c * i, this.ticks, this.hitbox.w, this.hitbox.h, 0F);
                    ProjectileEntity2D projectile = new ProjectileEntity2D(mob);

                    projectile.teleport(orb.x, orb.y);
                    projectile.damage = this.state.getStats().coreDamage.get();
                    projectile.speed = 8F;

                    this.state.post(() -> this.state.addEntity(projectile));
                }

                this.state.postSound(Link.create("game:sounds/core_projectile.ogg"), 0.8F);
            }
        }
    }

    @Override
    public void renderEntity(RenderingContext context, Batcher2D batcher)
    {
        /* Draw glow */
        batcher.dropCircleShadow(
            context.stack, 0, 0,
            (int) (this.hitbox.w * 0.6F), 24,
            0xcc0088ff, 0x0088ff
        );

        /* Draw projectile(s) */
        for (int i = 0, c = this.state.getStats().coreProjectiles.get(); i < c; i++)
        {
            Vector2f orb = getOrb(MathUtils.PI * 2 / c * i, this.ticks, this.hitbox.w, this.hitbox.h, context.getTransition());

            context.stack.push();
            context.stack.translate(orb.x, orb.y, 0);

            batcher.icon(context.stack, Icons.SPHERE, Colors.WHITE, -8, -8, 16, 16);

            context.stack.pop();
        }

        /* Draw giant block */
        context.stack.push();
        context.stack.translate(0, (float) Math.sin((this.ticks + context.getTransition()) / 15F) * 5 - 2F, 0);
        context.stack.scale(3.5F, 3.5F, 3.5F);

        batcher.icon(context.stack, Icons.BLOCK, Colors.WHITE, -8, -8, 16, 16);
        context.stack.pop();
    }
}