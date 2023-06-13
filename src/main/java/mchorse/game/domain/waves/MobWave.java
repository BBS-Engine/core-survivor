package mchorse.game.domain.waves;

import mchorse.game.domain.GameState;

public abstract class MobWave
{
    protected GameState state;

    protected int ticks;
    protected int length;

    public MobWave(GameState state, int length)
    {
        this.state = state;
        this.length = length;
    }

    public void update()
    {
        this.ticks += 1;
    }

    public boolean isFinished()
    {
        return this.ticks >= this.length;
    }

    public int getSeconds()
    {
        return Math.max(0, (this.length - this.ticks) / 20);
    }
}