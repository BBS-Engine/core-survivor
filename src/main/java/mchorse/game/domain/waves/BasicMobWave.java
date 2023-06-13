package mchorse.game.domain.waves;

import mchorse.game.domain.GameState;
import mchorse.game.domain.entities.Mob2D;

public class BasicMobWave extends MobWave
{
    protected int wave;
    protected int health;
    protected int totalMobs;

    private int lastMobs;

    public BasicMobWave(GameState state, int wave, int length, int health, int totalMobs)
    {
        super(state, length);

        this.wave = wave;
        this.health = health;
        this.totalMobs = totalMobs;
    }

    @Override
    public void update()
    {
        super.update();

        int mobs = (int) ((this.ticks / (float) this.length) * this.totalMobs);
        int diff = mobs - this.lastMobs;

        if (this.ticks <= this.length && diff > 0)
        {
            for (int i = 0; i < diff; i++)
            {
                Mob2D mob = new Mob2D();
                float a = (float) (Math.random() * Math.PI * 2D);
                double l = 200 + (this.length / 10) + Math.random() * 4 - 2;
                float x = (float) (Math.cos(a) * l);
                float y = (float) (Math.sin(a) * l);
                int hp = this.health;
                double random = Math.random();

                if (random < 0.05 && this.wave >= 10)
                {
                    mob.hitbox.setSize(24, 24);
                    mob.color = 0xffff2200;
                    mob.speed *= 0.75F;

                    hp *= 4;
                }
                else if (random < 0.1 && this.wave >= 5)
                {
                    mob.hitbox.setSize(8, 8);
                    mob.color = 0xffffff00;
                    mob.speed *= 2F;

                    hp *= 0.8F;
                }

                mob.speed *= 1 + this.wave / 12F;
                mob.setHealth(hp);
                mob.teleport(x, y);

                this.state.addEntity(mob);
            }
        }

        this.lastMobs = mobs;
    }
}