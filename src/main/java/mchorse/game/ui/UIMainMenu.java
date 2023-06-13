package mchorse.game.ui;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.game.GameEngine2D;
import mchorse.game.domain.GameState;
import mchorse.game.domain.PlayerStats;
import mchorse.game.domain.entities.CoreEntity2D;
import mchorse.game.domain.properties.LevelProperty;
import org.joml.Vector2f;

public class UIMainMenu extends UIBaseMenu
{
    public UIElement column;
    public UIButton play;
    public UIButton openUpgrades;
    public UIButton cycleUIScale;
    public UIButton quit;

    private int ticks;

    public UIMainMenu(IBridge bridge)
    {
        super(bridge);

        this.play = new UIButton(IKey.str("Play"), (b) -> this.play());
        this.openUpgrades = new UIButton(IKey.str("Upgrades"), (b) -> this.openUpgrades());
        this.quit = new UIButton(IKey.str("Quit"), (b) -> Window.close());
        this.cycleUIScale = new UIButton(IKey.str("UI scale: " + BBSSettings.userIntefaceScale.get()), (b) ->
        {
            BBSSettings.userIntefaceScale.set(MathUtils.cycler(BBSSettings.userIntefaceScale.get() + 1, 1, 4));

            this.cycleUIScale.label = IKey.str("UI scale: " + BBSSettings.userIntefaceScale.get());
        });
        this.column = UI.column(10, this.play, this.openUpgrades, this.cycleUIScale, this.quit);

        this.column.relative(this.main).x(0.5F).y(1F, -20).w(120).anchor(0.5F, 1F);

        this.main.add(this.column);
    }

    private void openUpgrades()
    {
        GameEngine2D engine = (GameEngine2D) this.bridge.getEngine();
        UIUpgradesOverlayPanel panel = new UIUpgradesOverlayPanel(engine.state);

        UIOverlay.addOverlay(this.context, panel, 300, 0);

        panel.h(0.8F);
        panel.resize();
    }

    private void play()
    {
        GameEngine2D engine = (GameEngine2D) this.bridge.getEngine();

        engine.startGame();
    }

    @Override
    public Link getMenuId()
    {
        return Link.create("core_survivor:main");
    }

    @Override
    protected void closeMenu()
    {}

    @Override
    public void update()
    {
        super.update();

        this.ticks += 1;
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        GameEngine2D engine = (GameEngine2D) bridge.getEngine();

        int w = this.column.area.w - 20;
        int x = this.width / 2;
        int y = this.height / 2;

        /* Draw glow */
        context.draw.dropCircleShadow(
            x, y,
            (int) (w * 0.6F), 24,
            0xcc0088ff, 0x0088ff
        );

        /* Draw projectile(s) */
        for (int i = 0, c = 3; i < c; i++)
        {
            Vector2f orb = CoreEntity2D.getOrb(MathUtils.PI * 2 / c * i, this.ticks, w, w, context.getTransition());

            context.stack.push();
            context.stack.translate(x + orb.x, y + orb.y, 0);
            context.stack.scale(1.5F, 1.5F, 1.5F);

            context.getUBO().updateView(context.stack.getModelMatrix());
            Icons.SPHERE.render(context.draw, 0, 0, 0.5F, 0.5F);

            context.stack.pop();
        }

        /* Draw giant block */
        context.stack.push();
        context.stack.translate(x, y + (float) Math.sin((this.ticks + context.getTransition()) / 15F) * 5 - 2F, 0);
        context.stack.scale(6F, 6F, 6F);

        context.getUBO().updateView(context.stack.getModelMatrix());

        Icons.BLOCK.render(context.draw, 0, 0, 0.5F, 0.5F);
        context.stack.pop();

        context.getUBO().updateView(context.stack.getModelMatrix());
    }

    public static class UIUpgrade extends UIElement
    {
        public UIIcon upgrade;

        private LevelProperty property;
        private GameState state;

        public UIUpgrade(LevelProperty property, GameState state)
        {
            this.property = property;
            this.state = state;

            this.upgrade = new UIIcon(PlayerStats.ICONS.get(property.getId()), (b) -> this.upgrade());
            this.upgrade.relative(this).w(20).h(1F);

            this.add(this.upgrade);
        }

        private void upgrade()
        {
            int cost = this.property.getCost();

            if (this.state.getCrystals().canConsume(cost))
            {
                this.property.levelUp();
                this.state.getCrystals().consume(cost);
            }
        }

        @Override
        public void render(UIContext context)
        {
            Area area = this.upgrade.area;
            int cost = this.property.getCost();
            String label = String.valueOf(cost);
            int w = context.font.getWidth(label);
            boolean hasCrystals = this.state.getCrystals().canConsume(cost);

            // context.draw.outline(area.x, area.y, area.ex(), area.ey(), hasCrystals ? (area.isInside(context) ? Colors.A100 | 0x0088ff : Colors.WHITE) : Colors.A100 | Colors.RED);

            context.font.renderWithShadow(context.render, PlayerStats.HUMAN_NAMES.get(this.property.getId()) + " - \u00A7a" + this.property.stringifyValue(this.property.getLevel() + 1), this.upgrade.area.x + 25, this.upgrade.area.my(context.font.getHeight()));
            context.font.renderWithShadow(context.render, label, this.area.ex() - 16 - w, this.upgrade.area.my(context.font.getHeight()), hasCrystals ? Colors.WHITE : Colors.A100 | Colors.RED);

            Icons.SHARD.render(context.draw, this.area.ex(), this.area.my(), Colors.A100 | 0xcc00ff, 1F, 0.5F);

            super.render(context);
        }
    }

    public static class UIUpgradesOverlayPanel extends UIOverlayPanel
    {
        public UIScrollView upgrades;

        private GameState state;

        public UIUpgradesOverlayPanel(GameState state)
        {
            super(IKey.str("Upgrades"));

            this.state = state;

            this.upgrades = UI.scrollView(10, 13);
            this.upgrades.relative(this.content).full();

            this.content.add(this.upgrades);

            for (LevelProperty property : state.getStats().properties.values())
            {
                UIUpgrade upgrade = new UIUpgrade(property, state);

                upgrade.h(20);

                this.upgrades.add(upgrade);
            }
        }

        @Override
        protected void renderBackground(UIContext context)
        {
            super.renderBackground(context);

            /* Draw crystals */
            String label = String.valueOf(this.state.getCrystals().get());
            int w = context.font.getWidth(label);
            int x = this.close.area.x - 16 - w;
            int y = this.close.area.my();

            context.font.renderWithShadow(context.render, label, x, y - context.font.getHeight() / 2 - 1);
            Icons.SHARD.render(context.draw, x + w, y, Colors.A100 | 0xcc00ff, 0F, 0.5F);
        }
    }
}