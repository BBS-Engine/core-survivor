package mchorse.game.domain;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.game.domain.entities.CoreEntity2D;
import mchorse.game.domain.entities.Entity2D;
import mchorse.game.domain.entities.LivingEntity2D;
import mchorse.game.domain.entities.Mob2D;
import mchorse.game.domain.properties.LevelProperty;
import mchorse.game.domain.spells.ISpell;
import mchorse.game.domain.upgrades.IUpgrade;
import mchorse.game.domain.upgrades.Upgrades;
import mchorse.game.domain.utils.Hitbox;
import mchorse.game.domain.utils.Resource;
import mchorse.game.domain.waves.BasicMobWave;
import mchorse.game.domain.waves.MobWave;
import mchorse.game.events.CrystalDropEvent;
import mchorse.game.events.DamageEntityEvent;
import mchorse.game.events.GameOverEvent;
import mchorse.game.events.SoundEvent;
import mchorse.game.events.WaveEndedEvent;
import org.greenrobot.eventbus.EventBus;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class GameState
{
    public final EventBus eventBus = new EventBus();
    public final Upgrades upgrades = new Upgrades();

    private Resource energy = new Resource();
    private Resource crystals = new Resource();
    private List<ISpell> spells = new ArrayList<ISpell>();

    private CoreEntity2D core;
    private List<Entity2D> entitites = new ArrayList<Entity2D>();

    private PlayerStats stats = new PlayerStats();

    private List<Runnable> postUpdate = new ArrayList<Runnable>();
    private List<Entity2D> closest = new ArrayList<Entity2D>();

    private int waveCount;
    private MobWave wave;
    private int finishDelay;

    public void post(Runnable runnable)
    {
        this.postUpdate.add(runnable);
    }

    public PlayerStats getStats()
    {
        return this.stats;
    }

    public List<ISpell> getSpells()
    {
        return this.spells;
    }

    public Resource getEnergy()
    {
        return this.energy;
    }

    public Resource getCrystals()
    {
        return this.crystals;
    }

    public int getWave()
    {
        return this.waveCount;
    }

    /* Health */

    public int getHealth()
    {
        return this.stats.coreHealth.get();
    }

    public void reduceHealth()
    {
        this.stats.coreHealth.set(this.stats.coreHealth.get() - 1);

        if (this.getHealth() == 0)
        {
            this.post(() -> this.eventBus.post(new GameOverEvent()));
        }
    }

    public void postDamage(LivingEntity2D entity, float damage)
    {
        this.eventBus.post(new DamageEntityEvent(entity, damage));
    }

    public void postCrystalDrop(LivingEntity2D entity)
    {
        this.crystals.add(1);

        this.eventBus.post(new CrystalDropEvent(entity));
        this.postSound(Link.create("game:sounds/crystal.ogg"), 0.5F);
    }

    public void postSound(Link link, float volume)
    {
        this.eventBus.post(new SoundEvent(link, volume));
    }

    /* Game management */

    public int getSeconds()
    {
        return this.wave.getSeconds();
    }

    public void startGame()
    {
        this.stats.reset();
        this.entitites.clear();

        this.energy.reset();
        this.spells.clear();

        CoreEntity2D core = new CoreEntity2D();

        core.setPosition(0, 0);

        this.addEntity(core);

        this.waveCount = 0;
        this.core = core;

        this.setupWave();
    }

    public void setupWave()
    {
        this.waveCount += 1;

        int length = Math.min(this.waveCount * 100, 90 * 20);
        int hp = 1 + this.waveCount + (int) Math.pow(1.325, this.waveCount - 5);
        int frequency = Math.max(15 - this.waveCount, 2);
        int mobs = 1 + this.waveCount / 10;
        int totalMobs = length / frequency * mobs;

        this.wave = new BasicMobWave(this, this.waveCount, length, hp, totalMobs);
        this.finishDelay = 30;
    }

    public void stopGame()
    {
        this.entitites.clear();
        this.core = null;
        this.energy.reset();
    }

    public void endWave()
    {
        this.eventBus.post(new WaveEndedEvent());
    }

    public void applyUpgrade(IUpgrade upgrade)
    {
        upgrade.apply(this);
    }

    /* Energy management */

    public void addClickEnergy()
    {
        this.addEnergy(this.stats.energyPerClick.get());
    }

    public void addPerSecondEnergy()
    {
        this.addEnergy(this.stats.energyPerSecond.get());
    }

    public void addEnergy(int energy)
    {
        energy = (int) Math.ceil(energy * this.stats.energyPercentage.get());

        if (energy > 0)
        {
            this.energy.add(energy);
        }
    }

    /* Entity management */

    public CoreEntity2D getCore()
    {
        return this.core;
    }

    public void addEntity(Entity2D entity)
    {
        entity.state = this;

        this.entitites.add(entity);
    }

    public List<Entity2D> getEntitites()
    {
        return this.entitites;
    }

    public List<Entity2D> getClosest(Vector2f position, Predicate<Entity2D> predicate)
    {
        this.closest.clear();

        for (Entity2D entity2D : this.entitites)
        {
            if (predicate == null || predicate.test(entity2D))
            {
                this.closest.add(entity2D);
            }
        }

        this.closest.sort((a, b) -> (int) (a.position.distanceSquared(position) - b.position.distanceSquared(position)));

        return this.closest;
    }

    public List<Entity2D> getEntitiesInBox(Hitbox hitbox, Predicate<Entity2D> predicate)
    {
        List<Entity2D> entities = new ArrayList<Entity2D>();

        for (Entity2D entity2D : this.entitites)
        {
            if (entity2D.hitbox.intersects(hitbox) && (predicate == null || predicate.test(entity2D)))
            {
                entities.add(entity2D);
            }
        }

        return entities;
    }

    public void update()
    {
        this.energy.update();
        this.crystals.update();

        for (ISpell spell : this.spells)
        {
            spell.update();
        }

        Iterator<Entity2D> it = this.entitites.iterator();

        while (it.hasNext())
        {
            Entity2D entity = it.next();

            entity.update();

            if (entity.canBeRemoved())
            {
                it.remove();
            }
        }

        this.wave.update();

        if (this.wave.isFinished() && this.hasNoMobs())
        {
            this.finishDelay -= 1;

            if (this.finishDelay <= 0)
            {
                this.endWave();
            }
        }

        for (Runnable runnable : this.postUpdate)
        {
            runnable.run();
        }

        this.postUpdate.clear();
    }

    private boolean hasNoMobs()
    {
        for (Entity2D entity2D : this.entitites)
        {
            if (entity2D instanceof Mob2D)
            {
                return false;
            }
        }

        return true;
    }

    /* (De)serialization */

    public void write(File file)
    {
        MapType map = new MapType();

        for (LevelProperty property : this.stats.properties.values())
        {
            map.putInt(property.getId(), property.getLevel());
        }

        map.putInt("crystals", this.crystals.get());

        try
        {
            DataToString.write(file, map, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void read(File file)
    {
        MapType data = new MapType();

        try
        {
            if (file.exists())
            {
                data = (MapType) DataToString.read(file);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.crystals.set(data.getInt("crystals"));

        for (LevelProperty property : this.stats.properties.values())
        {
            property.setLevel(data.getInt(property.getId(), 1));
        }
    }
}