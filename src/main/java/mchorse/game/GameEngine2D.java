package mchorse.game;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.core.Engine;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.resources.packs.InternalAssetsSourcePack;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.OS;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.game.domain.GameState;
import mchorse.game.domain.PlayerStats;
import mchorse.game.domain.spells.FreezeSpell;
import mchorse.game.domain.spells.PushSpell;
import mchorse.game.domain.upgrades.FloatUpgrade;
import mchorse.game.domain.upgrades.IntUpgrade;
import mchorse.game.domain.upgrades.SpellUpgrade;
import mchorse.game.domain.upgrades.UpgradeRarity;
import mchorse.game.events.GameOverEvent;
import mchorse.game.events.SoundEvent;
import mchorse.game.events.WaveEndedEvent;
import mchorse.game.ui.UIMainMenu;
import mchorse.game.ui.UIPauseMenu;
import mchorse.game.ui.UIUpgradeMenu;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class GameEngine2D extends Engine implements IBridge, IBridgeMenu
{
    public GameState state = new GameState();

    public GameRenderer2D renderer;
    public GameController2D controller;
    public UIBaseMenu menu;

    private UIRenderingContext menuContext;

    private SoundPlayer music;

    public static File getStatsFile()
    {
        return BBS.getConfigPath("stats.json");
    }

    public GameEngine2D(Game2D game)
    {
        super();

        BBS.registerCore(this, game.gameDirectory);
        BBS.registerFactories();
        BBS.registerFoundation();

        this.renderer = new GameRenderer2D(this);
        this.controller = new GameController2D(this);

        this.registerKeybinds();
        this.registerUpdates();

        BBS.getProvider().register(new InternalAssetsSourcePack("game", GameEngine2D.class));
        BBSSettings.userIntefaceScale.callback((v) -> this.resize(Window.width, Window.height));

        this.state.eventBus.register(this);
    }

    private void registerKeybinds()
    {
        KeybindCategory category = new KeybindCategory("global");
        Keybind fullscreen = new Keybind("fullscreen").onPress(Window::toggleFullscreen);

        category.add(fullscreen.keys(GLFW.GLFW_KEY_F11));
        this.keys.keybinds.add(category);
    }

    private void registerUpdates()
    {
        PlayerStats stats = this.state.getStats();

        /* Energy */
        this.state.upgrades.register(new IntUpgrade(stats.energyPerClick.getId(), UpgradeRarity.COMMON, 1));
        this.state.upgrades.register(new IntUpgrade(stats.energyPerClick.getId(), UpgradeRarity.RARE, 2));
        this.state.upgrades.register(new IntUpgrade(stats.energyPerClick.getId(), UpgradeRarity.LEGENDARY, 4));

        this.state.upgrades.register(new IntUpgrade(stats.energyPerSecond.getId(), UpgradeRarity.COMMON, 5));
        this.state.upgrades.register(new IntUpgrade(stats.energyPerSecond.getId(), UpgradeRarity.RARE, 10));
        this.state.upgrades.register(new IntUpgrade(stats.energyPerSecond.getId(), UpgradeRarity.LEGENDARY, 25));

        this.state.upgrades.register(new FloatUpgrade(stats.energyPercentage.getId(), UpgradeRarity.COMMON, 0.1F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.energyPercentage.getId(), UpgradeRarity.UNCOMMON, 0.2F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.energyPercentage.getId(), UpgradeRarity.RARE, 1.05F, true));
        this.state.upgrades.register(new FloatUpgrade(stats.energyPercentage.getId(), UpgradeRarity.EPIC,1.1F, true));
        this.state.upgrades.register(new FloatUpgrade(stats.energyPercentage.getId(), UpgradeRarity.LEGENDARY, 1.25F, true));

        /* Crystals */
        this.state.upgrades.register(new FloatUpgrade(stats.crystalDropRate.getId(), UpgradeRarity.UNCOMMON, 0.01F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.crystalDropRate.getId(), UpgradeRarity.RARE, 0.02F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.crystalDropRate.getId(), UpgradeRarity.LEGENDARY, 0.04F).percent());

        /* Cursor */
        this.state.upgrades.register(new IntUpgrade(stats.cursorDamage.getId(), UpgradeRarity.COMMON, 1));
        this.state.upgrades.register(new IntUpgrade(stats.cursorDamage.getId(), UpgradeRarity.RARE, 2));
        this.state.upgrades.register(new IntUpgrade(stats.cursorDamage.getId(), UpgradeRarity.LEGENDARY, 3));

        this.state.upgrades.register(new FloatUpgrade(stats.cursorSpellsCooldown.getId(), UpgradeRarity.COMMON, 0.1F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.cursorSpellsCooldown.getId(), UpgradeRarity.UNCOMMON, 0.2F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.cursorSpellsCooldown.getId(), UpgradeRarity.LEGENDARY, 2F, true));

        this.state.upgrades.register(new FloatUpgrade(stats.cursorClickRate.getId(), UpgradeRarity.COMMON, 0.1F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.cursorClickRate.getId(), UpgradeRarity.UNCOMMON, 0.2F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.cursorClickRate.getId(), UpgradeRarity.EPIC, 0.4F).percent());

        /* Core */
        this.state.upgrades.register(new IntUpgrade(stats.coreDamage.getId(), UpgradeRarity.COMMON, 1));
        this.state.upgrades.register(new IntUpgrade(stats.coreDamage.getId(), UpgradeRarity.RARE, 2));
        this.state.upgrades.register(new IntUpgrade(stats.coreDamage.getId(), UpgradeRarity.EPIC, 3));

        this.state.upgrades.register(new IntUpgrade(stats.coreHealth.getId(), UpgradeRarity.COMMON, 1));
        this.state.upgrades.register(new IntUpgrade(stats.coreHealth.getId(), UpgradeRarity.UNCOMMON, 2));
        this.state.upgrades.register(new IntUpgrade(stats.coreHealth.getId(), UpgradeRarity.RARE, 3));

        this.state.upgrades.register(new IntUpgrade(stats.coreProjectiles.getId(), UpgradeRarity.LEGENDARY, 1));

        this.state.upgrades.register(new FloatUpgrade(stats.coreFireRate.getId(), UpgradeRarity.UNCOMMON, 0.05F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.coreFireRate.getId(), UpgradeRarity.RARE, 0.1F).percent());
        this.state.upgrades.register(new FloatUpgrade(stats.coreFireRate.getId(), UpgradeRarity.EPIC, 0.2F).percent());

        /* Spells */
        this.state.upgrades.register(new SpellUpgrade("Push spell", new PushSpell(this.state)));
        this.state.upgrades.register(new SpellUpgrade("Freeze spell", new FreezeSpell(this.state)));
    }

    private void updateWindowIcon()
    {
        if (OS.CURRENT == OS.MACOS)
        {
            return;
        }

        try
        {
            Pixels pixels48 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.create("game:textures/icons/icon_48.png")));
            Pixels pixels32 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.create("game:textures/icons/icon_32.png")));
            Pixels pixels16 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.create("game:textures/icons/icon_16.png")));

            Window.updateIcon(pixels48, pixels32, pixels16);

            pixels48.delete();
            pixels32.delete();
            pixels16.delete();
        }
        catch (Exception e)
        {
            System.err.println("Failed to register window icons!");
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception
    {
        super.init();

        this.stopGame();

        this.renderer.init();
        this.resize(Window.width, Window.height);

        this.state.read(getStatsFile());

        this.menuContext = new UIRenderingContext(this.renderer.getContext(), this.renderer.getShaders().ortho);
        this.menuContext.setUBO(this.renderer.getShaders().ubo);

        ShaderRepository mainShaders = this.menuContext.getMainShaders();

        mainShaders.register(this.renderer.getShaders().vertexRGBA2D);
        mainShaders.register(this.renderer.getShaders().vertexUVRGBA2D);

        BBS.getSounds().update();

        this.music = BBS.getSounds().play(Link.create("game:sounds/music_1.ogg"));

        if (this.music != null)
        {
            this.music.setRelative(true);
            this.music.setVolume(0.25F);
        }

        this.updateWindowIcon();
    }

    public void startGame()
    {
        this.showMenu(null);
        this.state.startGame();
    }

    public void stopGame()
    {
        this.controller.cameraX = 0;
        this.controller.cameraY = 0;

        this.state.stopGame();
        this.showMenu(new UIMainMenu(this));

        this.renderer.clear();

        if (this.music != null)
        {
            this.music.stop();
            this.music.play();
        }
    }

    @Subscribe
    public void onGameOver(GameOverEvent event)
    {
        this.stopGame();
    }

    @Subscribe
    public void onWaveEnd(WaveEndedEvent event)
    {
        this.showMenu(new UIUpgradeMenu(this));

        this.state.setupWave();
    }

    @Subscribe
    public void onSoundEvent(SoundEvent event)
    {
        SoundPlayer player = BBS.getSounds().play(event.sound);

        if (player != null)
        {
            player.setVolume(event.volume);
            player.setPitch((float) (Math.random() * 0.1F) + 0.95F);
        }
    }

    @Override
    public void delete()
    {
        this.state.write(getStatsFile());

        super.delete();

        BBS.terminate();
    }

    @Override
    public boolean handleGamepad(int button, int action)
    {
        return false;
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        if (this.keys.keybinds.handleKey(key, scancode, action, mods))
        {
            return true;
        }

        if (this.menu != null)
        {
            return this.menu.handleKey(key, scancode, action, mods);
        }
        else
        {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS)
            {
                this.showMenu(new UIPauseMenu(this));

                return true;
            }
        }

        return this.controller.handleKey(key, scancode, action, mods);
    }

    @Override
    public void handleTextInput(int key)
    {
        if (this.menu != null)
        {
            this.menu.handleTextInput(key);
        }
        else
        {
            this.controller.handleTextInput(key);
        }
    }

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        if (this.menu == null)
        {
            this.controller.handleMouse(button, action, mode);

            return;
        }

        MouseInput mouse = this.mouse;

        if (action == GLFW.GLFW_PRESS)
        {
            this.menu.mouseClicked(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), button);
        }
        else if (action == GLFW.GLFW_RELEASE)
        {
            this.menu.mouseReleased(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), button);
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {
        if (this.menu == null)
        {
            this.controller.handleScroll(x, y);

            return;
        }

        MouseInput mouse = this.mouse;
        int mouseWheel = (int) Math.round(y);

        if (mouseWheel != 0)
        {
            this.menu.mouseScrolled(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), mouseWheel);
        }
    }

    @Override
    public void update()
    {
        super.update();

        BBS.getSounds().update();

        if (this.menu == null)
        {
            this.controller.update();
            this.state.update();
            this.renderer.update();
        }
        else
        {
            this.menu.update();
        }

        if (this.music != null && this.music.isStopped())
        {
            this.music = BBS.getSounds().play(Link.create("game:sounds/music_1.ogg"));

            if (this.music != null)
            {
                this.music.setRelative(true);
                this.music.setVolume(0.25F);
            }
        }
    }

    @Override
    public void render(float transition)
    {
        super.render(transition);

        this.renderer.render(this.menu == null ? transition : 0F);
        this.controller.render();

        this.menuContext.setTransition(transition);
        this.menuContext.getUBO().update(this.renderer.getShaders().ortho, Matrices.EMPTY_4F);

        if (this.menu != null)
        {
            MouseInput mouse = this.mouse;

            this.menu.renderMenu(this.menuContext, BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        GLStates.resetViewport();

        this.renderer.resize(width, height);

        if (this.menu != null)
        {
            int scale = BBSSettings.getScale();

            this.menu.resize(width / scale, height / scale);
        }
    }

    /* IBridge implementation */

    @Override
    public Engine getEngine()
    {
        return this;
    }

    @Override
    public <T> T get(Class<T> apiInterface)
    {
        if (apiInterface == IBridgeMenu.class)
        {
            return (T) this;
        }

        return null;
    }

    /* IBridgeMenu implementation */

    @Override
    public UIBaseMenu getCurrentMenu()
    {
        return this.menu;
    }

    @Override
    public void showMenu(UIBaseMenu menu)
    {
        UIBaseMenu old = this.menu;

        this.controller.reset();

        if (this.menu != null)
        {
            this.menu.onClose(menu);
        }

        this.menu = menu;

        if (this.menu != null)
        {
            int scale = BBSSettings.getScale();

            this.menu.onOpen(old);
            this.menu.resize(Window.width / scale, Window.height / scale);
        }
    }
}