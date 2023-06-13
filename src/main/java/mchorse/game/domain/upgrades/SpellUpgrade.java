package mchorse.game.domain.upgrades;

import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.game.domain.GameState;
import mchorse.game.domain.spells.ISpell;

import java.util.List;

public class SpellUpgrade implements IUpgrade
{
    public final String name;
    public final ISpell spell;

    public SpellUpgrade(String name, ISpell spell)
    {
        this.name = name;
        this.spell = spell;
    }

    @Override
    public String stringify()
    {
        return this.name;
    }

    @Override
    public Icon getIcon()
    {
        return this.spell.getIcon();
    }

    @Override
    public UpgradeRarity getRarity()
    {
        return UpgradeRarity.EPIC;
    }

    @Override
    public boolean canApply(List<IUpgrade> upgrades, GameState state)
    {
        for (ISpell spell : state.getSpells())
        {
            if (spell == this.spell)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void apply(GameState state)
    {
        state.getSpells().add(this.spell);
    }
}