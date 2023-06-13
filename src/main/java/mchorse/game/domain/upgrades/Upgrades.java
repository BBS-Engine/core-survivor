package mchorse.game.domain.upgrades;

import mchorse.game.domain.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Upgrades
{
    public Map<UpgradeRarity, List<IUpgrade>> allUpgrades = new HashMap<UpgradeRarity, List<IUpgrade>>();

    public List<IUpgrade> getRandom(GameState state, int count)
    {
        List<IUpgrade> upgrades = new ArrayList<IUpgrade>();

        while (upgrades.size() < count)
        {
            List<IUpgrade> rarityUpgrades = this.getUpgrades();
            IUpgrade random = rarityUpgrades.get((int) (Math.random() * rarityUpgrades.size()));

            if (random.canApply(upgrades, state))
            {
                upgrades.add(random);
            }
        }

        return upgrades;
    }

    private List<IUpgrade> getUpgrades()
    {
        return this.allUpgrades.get(this.genRarity());
    }

    private UpgradeRarity genRarity()
    {
        double random = Math.random();

        if (random < 0.05) return UpgradeRarity.LEGENDARY;
        else if (random < 0.15) return UpgradeRarity.EPIC;
        else if (random < 0.3) return UpgradeRarity.RARE;
        else if (random < 0.5) return UpgradeRarity.UNCOMMON;

        return UpgradeRarity.COMMON;
    }

    public void register(IUpgrade upgrade)
    {
        this.allUpgrades.computeIfAbsent(upgrade.getRarity(), (key) -> new ArrayList<IUpgrade>()).add(upgrade);
    }
}