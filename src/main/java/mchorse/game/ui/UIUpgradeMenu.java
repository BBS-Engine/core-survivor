package mchorse.game.ui;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.game.GameEngine2D;
import mchorse.game.domain.upgrades.IUpgrade;

import java.util.List;

public class UIUpgradeMenu extends UIBaseMenu
{
    public UIElement column;
    public UIButton reroll;

    private int rolls;

    public UIUpgradeMenu(IBridge bridge)
    {
        super(bridge);

        this.column = UI.column();
        this.column.relative(this.main).xy(0.5F, 0.5F).w(0.75F).h(0.5F).maxW(320).anchor(0.5F);

        this.reroll = new UIButton(IKey.str("Reroll"), (b) ->
        {
            GameEngine2D engine2D = (GameEngine2D) this.bridge.getEngine();
            int rollCost = this.getRollCost();

            if (engine2D.state.getEnergy().canConsume(rollCost))
            {
                engine2D.state.getEnergy().consume(rollCost);
                this.roll();
            }

            this.updatePrices();
        });
        this.reroll.relative(this.column).x(0.5F).y(1F, 10).w(80).anchorX(0.5F);

        this.main.add(this.column, this.reroll);

        this.roll();
    }

    private void updatePrices()
    {
        GameEngine2D engine2D = (GameEngine2D) this.bridge.getEngine();

        this.reroll.setEnabled(engine2D.state.getEnergy().canConsume(this.getRollCost()));

        for (UIUpgrade upgrade : this.column.getChildren(UIUpgrade.class))
        {
            upgrade.updatePrice();
        }
    }

    private double getBaseCost()
    {
        GameEngine2D engine2D = (GameEngine2D) this.bridge.getEngine();

        return 25D * Math.pow(1.25, engine2D.state.getWave() - 1);
    }

    private int getRollCost()
    {
        return (int) (this.getBaseCost() * 0.5D * Math.pow(1.25, this.rolls - 1));
    }

    public void roll()
    {
        GameEngine2D engine2D = (GameEngine2D) this.bridge.getEngine();
        List<IUpgrade> upgrades = engine2D.state.upgrades.getRandom(engine2D.state, 5);

        this.column.removeAll();

        for (IUpgrade upgrade : upgrades)
        {
            int cost = (int) (this.getBaseCost() * upgrade.getRarity().costMultiplier);
            UIUpgrade up = new UIUpgrade(this, upgrade, cost);

            up.h(30);
            this.column.add(up);
        }

        this.getRoot().resize();

        this.rolls += 1;

        String code = engine2D.state.getEnergy().canConsume(this.getRollCost()) ? "\u00A7c" : "\u00A75";

        this.reroll.tooltip(IKey.str(code + this.getRollCost() + "\u00A7r energy to re-roll"));
    }

    @Override
    public Link getMenuId()
    {
        return Link.create("core_survivor:upgrades");
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();

        context.getFont().renderCentered(this.context.render, "Upgrades", this.column.area.mx(), this.column.area.y - 20);

        UIPauseMenu.renderStats(this, context);
    }

    public static class UIUpgrade extends UIElement
    {
        private UIUpgradeMenu menu;
        private IUpgrade upgrade;
        private int cost;

        private IKey key;

        public UIUpgrade(UIUpgradeMenu menu, IUpgrade upgrade, int cost)
        {
            this.menu = menu;
            this.upgrade = upgrade;
            this.cost = cost;

            this.updatePrice();
        }

        public void updatePrice()
        {
            GameEngine2D engine2D = (GameEngine2D) this.menu.bridge.getEngine();
            String code = engine2D.state.getEnergy().canConsume(this.cost) ? "\u00A7c" : "\u00A75";

            this.key = IKey.str(this.upgrade.stringify() + " - " + code + this.cost + "\u00A7r energy");
        }

        @Override
        protected boolean subMouseClicked(UIContext context)
        {
            if (this.area.isInside(context) && context.mouseButton == 0)
            {
                GameEngine2D engine2D = (GameEngine2D) this.menu.bridge.getEngine();

                if (!engine2D.state.getEnergy().canConsume(this.cost))
                {
                    return false;
                }

                engine2D.state.applyUpgrade(this.upgrade);
                engine2D.state.getEnergy().consume(this.cost);

                this.removeFromParent();
                this.menu.updatePrices();

                return true;
            }

            return super.subMouseClicked(context);
        }

        @Override
        public void render(UIContext context)
        {
            boolean hover = this.area.isInside(context);
            int color = Colors.mulRGB(Colors.WHITE, hover ? 0.8F : 1F);

            context.draw.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.A75);
            context.draw.outline(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.setA(upgrade.getRarity().color, 0.75F));

            int x = this.area.x + 5;
            int y = this.area.y + 5;

            this.upgrade.getIcon().render(context.draw, x + 2, y + 2, color);

            x += 20;
            y += 10 - context.font.getHeight() / 2;

            context.font.renderWithShadow(context.render, this.key.get(), x, y, color);

            super.render(context);
        }
    }
}