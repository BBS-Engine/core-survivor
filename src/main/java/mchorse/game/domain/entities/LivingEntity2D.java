package mchorse.game.domain.entities;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolation;

public abstract class LivingEntity2D extends Entity2D
{
    public boolean dying;
    public int deathTimer = 5;
    public int deathTimerMax = 5;

    private float health;
    private float originalHealth;

    public void kill()
    {
        if (this.dying)
        {
            return;
        }

        this.dying = true;
        this.health = 0F;
    }

    public float getHealth()
    {
        return this.health;
    }

    public float getOriginalHealth()
    {
        return this.originalHealth;
    }

    public void damage(float health)
    {
        if (this.dying)
        {
            return;
        }

        this.setHealth(this.health - health);

        this.state.postDamage(this, health);
    }

    public void setHealth(float health)
    {
        if (health <= 0)
        {
            this.kill();
        }
        else
        {
            this.health = health;
            this.originalHealth = health;
        }
    }

    @Override
    public void update()
    {
        super.update();

        if (this.dying)
        {
            this.deathTimer -= 1;

            if (this.deathTimer <= 0)
            {
                this.remove();
            }
        }
    }

    @Override
    protected float getScale(RenderingContext context)
    {
        if (this.dying)
        {
            return Math.max(0F, Interpolation.EXP_INOUT.interpolate(0, 1F,(this.deathTimer - context.getTransition()) / (float) this.deathTimerMax));
        }

        return super.getScale(context);
    }
}
