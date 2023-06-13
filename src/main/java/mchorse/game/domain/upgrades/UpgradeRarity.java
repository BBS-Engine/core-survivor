package mchorse.game.domain.upgrades;

import mchorse.bbs.utils.colors.Colors;

public enum UpgradeRarity
{
    COMMON(0x888888, 0.8F), UNCOMMON(Colors.WHITE, 1F), RARE(Colors.GREEN, 2F), EPIC(Colors.BLUE, 4F), LEGENDARY(Colors.RED, 10F);

    public final int color;
    public final float costMultiplier;

    private UpgradeRarity(int color, float costMultiplier)
    {
        this.color = Colors.A100 | color;
        this.costMultiplier = costMultiplier;
    }
}