package mchorse.game.domain.spells;

import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.game.domain.GameState;
import mchorse.game.domain.entities.Entity2D;
import mchorse.game.domain.entities.Mob2D;
import mchorse.game.domain.utils.Hitbox;
import mchorse.game.events.IconParticleEvent;
import org.joml.Vector2f;

public class FreezeSpell extends BaseSpell
{
    public FreezeSpell(GameState state)
    {
        super(state);
    }

    @Override
    public Icon getIcon()
    {
        return Icons.SNOWFLAKE;
    }

    @Override
    protected int getSpellMaxCooldown()
    {
        return 40;
    }

    @Override
    protected boolean castSpell(GameState state, Vector2f cursor)
    {
        Hitbox hitbox = new Hitbox(cursor.x - 40, cursor.y - 40, 80, 80);
        boolean result = false;

        for (Entity2D entity2D : state.getEntitiesInBox(hitbox, (e) -> e instanceof Mob2D))
        {
            ((Mob2D) entity2D).freeze = 40;

            result = true;
        }

        for (int i = 0; i < 10; i++)
        {
            float x = (float) (Math.random() * (hitbox.w) + hitbox.x);
            float y = (float) (Math.random() * (hitbox.h) + hitbox.y);

            state.eventBus.post(new IconParticleEvent(Icons.SNOWFLAKE, 0xff4488ff, 1F, new Vector2f(x, y)));
        }

        state.postSound(Link.create("game:sounds/freeze.ogg"), 0.5F);

        return result;
    }
}