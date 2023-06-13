package mchorse.game.domain.upgrades;

import mchorse.game.domain.GameState;
import mchorse.game.domain.properties.IntLevelProperty;
import mchorse.game.domain.properties.LevelProperty;

import java.util.List;

public class IntUpgrade extends PropertyUpgrade
{
    public int value;

    public IntUpgrade(String id, UpgradeRarity rarity, int value)
    {
        super(id, rarity);

        this.value = value;
    }

    @Override
    public void apply(GameState state)
    {
        LevelProperty property = state.getStats().properties.get(this.id);

        if (property instanceof IntLevelProperty)
        {
            IntLevelProperty intProperty = (IntLevelProperty) property;

            intProperty.set(intProperty.get() + this.value);
        }
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.value);
    }
}