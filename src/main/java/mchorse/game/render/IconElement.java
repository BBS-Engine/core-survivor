package mchorse.game.render;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector2f;

public class IconElement extends RenderElement
{
    public Icon icon = Icons.SHARD;
    public int color = Colors.A100 | 0xcc00ff;
    public float scale = 1F;

    public IconElement(Vector2f position)
    {
        super(position);
    }

    public IconElement(Icon icon, int color, float scale, Vector2f position)
    {
        super(position);

        this.position.set(position);
        this.icon = icon;
        this.color = color;
        this.scale = scale;
    }

    @Override
    protected float getScale(float transition)
    {
        return super.getScale(transition) * this.scale;
    }

    @Override
    protected void renderElement(RenderingContext context, Batcher2D batcher)
    {
        batcher.icon(context.stack, this.icon, this.color, -8, -8, 16, 16);
    }
}