package mchorse.game.domain.spells;

import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.game.domain.GameState;
import mchorse.game.domain.entities.Entity2D;
import mchorse.game.domain.entities.Mob2D;
import mchorse.game.domain.utils.Hitbox;
import mchorse.game.events.IconParticleEvent;
import org.joml.Vector2f;

public class PushSpell extends BaseSpell
{
    public PushSpell(GameState state)
    {
        super(state);
    }

    @Override
    public Icon getIcon()
    {
        return Icons.DOWNLOAD;
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
        Entity2D core = state.getCore();
        boolean result = false;

        for (Entity2D entity2D : state.getEntitiesInBox(hitbox, (e) -> e instanceof Mob2D))
        {
            entity2D.velocity.add(new Vector2f(entity2D.position).sub(core.position).normalize().mul(10F));

            result = true;
        }

        state.eventBus.post(new IconParticleEvent(Icons.OUTLINE_SPHERE, Colors.WHITE, 80F / 16F, cursor));
        state.postSound(Link.create("game:sounds/push.ogg"), 1F);

        return result;
    }
}