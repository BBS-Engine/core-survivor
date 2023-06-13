package mchorse.game.domain.upgrades;

import mchorse.game.domain.GameState;
import mchorse.game.domain.properties.FloatLevelProperty;
import mchorse.game.domain.properties.LevelProperty;

import java.util.List;

public class FloatUpgrade extends PropertyUpgrade
{
    public float value;
    public boolean multiply;
    public boolean percent;

    public FloatUpgrade(String id, UpgradeRarity rarity, float value)
    {
        this(id, rarity, value, false);
    }

    public FloatUpgrade(String id, UpgradeRarity rarity, float value, boolean multiply)
    {
        super(id, rarity);

        this.value = value;
        this.multiply = multiply;
    }

    public FloatUpgrade percent()
    {
        this.percent = true;

        return this;
    }

    @Override
    public void apply(GameState state)
    {
        LevelProperty property = state.getStats().properties.get(this.id);

        if (property instanceof FloatLevelProperty)
        {
            FloatLevelProperty floatProperty = (FloatLevelProperty) property;

            if (this.multiply)
            {
                floatProperty.set(floatProperty.get() * this.value);
            }
            else
            {
                floatProperty.set(floatProperty.get() + this.value);
            }
        }
    }

    @Override
    public String toString()
    {
        return (this.multiply ? "x" : "+") + (this.percent ? Math.round(this.value * 100F) + "%" : this.value);
    }
}