package mchorse.game.domain.spells;

import mchorse.bbs.ui.utils.icons.Icon;
import org.joml.Vector2f;

public interface ISpell
{
    public Icon getIcon();

    public void cast(Vector2f cursor);

    public void update();

    public float getCooldown(float transition);
}