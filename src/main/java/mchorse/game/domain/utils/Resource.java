package mchorse.game.domain.utils;

public class Resource
{
    private int amount;
    private int animation = -1;

    public void reset()
    {
        this.amount = 0;
    }

    public int get()
    {
        return this.amount;
    }

    public boolean canConsume(int amount)
    {
        return this.amount >= amount;
    }

    private void resetTimer()
    {
        this.animation = 10;
    }

    public void set(int amount)
    {
        this.amount = amount;

        this.resetTimer();
    }

    public void add(int amount)
    {
        this.amount += amount;

        this.resetTimer();
    }

    public void consume(int amount)
    {
        this.amount -= amount;
    }

    public float getTimer(float transition)
    {
        return (this.animation < 0 ? 0 : this.animation - transition) / 10F;
    }

    public void update()
    {
        this.animation -= 1;
    }
}