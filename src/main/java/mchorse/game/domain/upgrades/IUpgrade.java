package mchorse.game.domain.upgrades;

import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.game.domain.GameState;

import java.util.List;

public interface IUpgrade
{
    public String stringify();

    public Icon getIcon();

    public UpgradeRarity getRarity();

    public boolean canApply(List<IUpgrade> upgrades, GameState state);

    public void apply(GameState state);
}