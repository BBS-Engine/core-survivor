package mchorse.game.domain.properties;

import java.util.function.Function;

public class FloatLevelProperty extends LevelProperty<Float>
{
    public boolean percent;

    public FloatLevelProperty(String id, Function<Integer, Integer> cost, Function<Integer, Float> lazyValue)
    {
        super(id, cost, lazyValue);
    }

    public FloatLevelProperty percent()
    {
        this.percent = true;

        return this;
    }

    @Override
    public String stringifyValue(Float value)
    {
        return this.percent ? Math.round(value * 100F) + "%" : value.toString();
    }

    @Override
    public String toString()
    {
        return this.stringifyValue(this.getLevel());
    }
}