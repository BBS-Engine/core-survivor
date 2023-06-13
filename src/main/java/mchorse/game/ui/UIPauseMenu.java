package mchorse.game.ui;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.game.GameEngine2D;
import mchorse.game.domain.PlayerStats;
import mchorse.game.domain.properties.LevelProperty;

public class UIPauseMenu extends UIBaseMenu
{
    public UIElement column;
    public UIButton resume;
    public UIButton cycleUIScale;
    public UIButton exit;

    public static void renderStats(UIBaseMenu menu, UIRenderingContext context)
    {
        GameEngine2D engine2D = (GameEngine2D) menu.bridge.getEngine();

        int x = 10;
        int y = menu.height / 2 - engine2D.state.getStats().properties.size() * 7;

        for (LevelProperty property : engine2D.state.getStats().properties.values())
        {
            context.getFont().renderWithShadow(context, PlayerStats.HUMAN_NAMES.get(property.getId()), x, y);
            context.getFont().renderAnchored(context, "\u00A7a" + property.stringifyValue(property.get()), x + 140, y, Colors.WHITE, true, 1F, 0F);

            y += 14;
        }
    }

    public UIPauseMenu(IBridge bridge)
    {
        super(bridge);

        this.resume = new UIButton(IKey.str("Resume"), (b) -> this.closeMenu());
        this.exit = new UIButton(IKey.str("Exit"), (b) -> this.exit());

        this.cycleUIScale = new UIButton(IKey.str("UI scale: " + BBSSettings.userIntefaceScale.get()), (b) ->
        {
            BBSSettings.userIntefaceScale.set(MathUtils.cycler(BBSSettings.userIntefaceScale.get() + 1, 1, 4));

            this.cycleUIScale.label = IKey.str("UI scale: " + BBSSettings.userIntefaceScale.get());
        });

        this.column = UI.column(10, this.resume, this.cycleUIScale, this.exit);
        this.column.relative(this.main).xy(0.5F, 0.5F).w(140).anchor(0.5F);

        this.main.add(this.column);
    }

    private void exit()
    {
        GameEngine2D engine = (GameEngine2D) this.bridge.getEngine();

        engine.stopGame();
    }

    @Override
    public Link getMenuId()
    {
        return Link.create("core_survivor:pause");
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();

        renderStats(this, context);
    }
}