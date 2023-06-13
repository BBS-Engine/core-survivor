package mchorse.game.domain.spells;

import mchorse.game.domain.GameState;
import mchorse.game.domain.entities.CoreEntity2D;
import org.joml.Vector2f;

public abstract class BaseSpell implements ISpell
{
    protected GameState state;

    protected int cooldown;

    public BaseSpell(GameState state)
    {
        this.state = state;
    }

    public int getMaxCooldown()
    {
        return CoreEntity2D.processRate(this.getSpellMaxCooldown(), 2, this.state.getStats().cursorSpellsCooldown.get());
    }

    protected abstract int getSpellMaxCooldown();

    @Override
    public void cast(Vector2f cursor)
    {
        if (this.cooldown <= 0)
        {
            if (this.castSpell(this.state, cursor))
            {
                this.cooldown = this.getMaxCooldown();
            }
        }
    }

    protected abstract boolean castSpell(GameState state, Vector2f cursor);

    @Override
    public void update()
    {
        this.cooldown -= 1;
    }

    @Override
    public float getCooldown(float transition)
    {
        return (this.cooldown - transition) / (float) this.getMaxCooldown();
    }
}