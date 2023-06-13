package mchorse.game.domain.upgrades;

import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.game.domain.GameState;
import mchorse.game.domain.PlayerStats;

import java.util.List;

public abstract class PropertyUpgrade implements IUpgrade
{
    public final String id;
    public final UpgradeRarity rarity;

    public PropertyUpgrade(String id, UpgradeRarity rarity)
    {
        this.id = id;
        this.rarity = rarity;
    }

    @Override
    public String stringify()
    {
        return PlayerStats.HUMAN_NAMES.get(this.id) + " - \u00A7a" + this;
    }

    @Override
    public Icon getIcon()
    {
        return PlayerStats.ICONS.get(this.id);
    }

    @Override
    public UpgradeRarity getRarity()
    {
        return this.rarity;
    }

    @Override
    public boolean canApply(List<IUpgrade> upgrades, GameState state)
    {
        for (IUpgrade upgrade : upgrades)
        {
            if (upgrade instanceof PropertyUpgrade && ((PropertyUpgrade) upgrade).id.equals(this.id))
            {
                return false;
            }
        }

        return true;
    }
}