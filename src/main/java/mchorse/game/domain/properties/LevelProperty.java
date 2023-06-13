package mchorse.game.domain.properties;

import java.util.function.Function;

public abstract class LevelProperty <T>
{
    private final String id;
    private int level = 1;
    private Function<Integer, Integer> cost;
    private Function<Integer, T> lazyValue;
    private T value;

    public LevelProperty(String id, Function<Integer, Integer> cost, Function<Integer, T> lazyValue)
    {
        this.id = id;
        this.cost = cost;
        this.lazyValue = lazyValue;

        this.resetValue();
    }

    public String getId()
    {
        return this.id;
    }

    public int getLevel()
    {
        return this.level;
    }

    public void setLevel(int level)
    {
        this.level = Math.max(level, 1);

        this.resetValue();
    }

    public void levelUp()
    {
        this.setLevel(this.level + 1);
    }

    public int getCost()
    {
        return this.getCost(this.level);
    }

    public int getCost(int level)
    {
        return this.cost == null ? 0 : this.cost.apply(level);
    }

    public void resetValue()
    {
        this.value = this.calculateValue();
    }

    public T calculateValue()
    {
        return this.calculateValue(this.level);
    }

    public T calculateValue(int level)
    {
        return this.lazyValue == null ? null : this.lazyValue.apply(level);
    }

    public String stringifyValue(int level)
    {
        return this.stringifyValue(this.calculateValue(level));
    }

    public abstract String stringifyValue(T value);

    public T get()
    {
        return this.value;
    }

    public void set(T newValue)
    {
        this.value = newValue;
    }
}