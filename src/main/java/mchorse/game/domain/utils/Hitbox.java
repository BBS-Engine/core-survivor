package mchorse.game.domain.utils;

public class Hitbox
{
    public float x;
    public float y;
    public float w;
    public float h;

    public Hitbox()
    {}

    public Hitbox(float x, float y, float w, float h)
    {
        this.set(x, y, w, h);
    }

    public void set(float x, float y, float w, float h)
    {
        this.setPos(x, y);
        this.setSize(w, h);
    }

    public void setPos(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void setSize(float w, float h)
    {
        this.w = w;
        this.h = h;
    }

    public boolean intersects(Hitbox hitbox)
    {
        return this.x < hitbox.x + hitbox.w && this.y < hitbox.y + hitbox.h
            && hitbox.x < this.x + this.w && hitbox.y < this.y + this.h;
    }

    public boolean contains(float x, float y)
    {
        return x >= this.x && x <= this.x + this.w
            && y >= this.y && y <= this.y + this.h;
    }
}