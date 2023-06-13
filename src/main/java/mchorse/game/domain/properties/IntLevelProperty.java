package mchorse.game.domain.properties;

import java.util.function.Function;

public class IntLevelProperty extends LevelProperty<Integer>
{
    public IntLevelProperty(String id, Function<Integer, Integer> cost, Function<Integer, Integer> lazyValue)
    {
        super(id, cost, lazyValue);
    }

    @Override
    public String stringifyValue(Integer value)
    {
        return value.toString();
    }

    @Override
    public String toString()
    {
        return this.stringifyValue(this.getLevel());
    }
}