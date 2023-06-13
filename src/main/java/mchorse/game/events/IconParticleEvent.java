package mchorse.game.events;

import mchorse.bbs.ui.utils.icons.Icon;
import org.joml.Vector2f;

public class IconParticleEvent
{
    public Icon icon;
    public int color;
    public float scale;
    public Vector2f position;

    public IconParticleEvent(Icon icon, int color, float scale, Vector2f position)
    {
        this.icon = icon;
        this.color = color;
        this.scale = scale;
        this.position = position;
    }
}