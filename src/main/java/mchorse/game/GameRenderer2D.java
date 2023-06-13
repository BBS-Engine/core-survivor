package mchorse.game;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.game.domain.GameState;
import mchorse.game.domain.entities.Entity2D;
import mchorse.game.domain.spells.ISpell;
import mchorse.game.events.CrystalDropEvent;
import mchorse.game.events.DamageEntityEvent;
import mchorse.game.events.IconParticleEvent;
import mchorse.game.render.Batcher2D;
import mchorse.game.render.DamageElement;
import mchorse.game.render.IconElement;
import mchorse.game.render.RenderElement;
import mchorse.game.render.Shaders2D;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameRenderer2D
{
    public static final Link STARS = Link.create("game:textures/stars.png");

    public GameEngine2D engine;

    private RenderingContext context;
    private UIDraw draw;
    private Batcher2D batcher;
    private Shaders2D shaders;

    private List<RenderElement> renderElements = new ArrayList<RenderElement>();

    private int timer;

    public GameRenderer2D(GameEngine2D engine)
    {
        this.engine = engine;
        this.engine.state.eventBus.register(this);
    }

    public RenderingContext getContext()
    {
        return this.context;
    }

    public Shaders2D getShaders()
    {
        return this.shaders;
    }

    @Subscribe
    public void onDamageEntityEvent(DamageEntityEvent event)
    {
        this.renderElements.add(new DamageElement(String.valueOf((int) Math.ceil(event.damage)), event.entity.position));
    }

    @Subscribe
    public void onCrystalDropEvent(CrystalDropEvent event)
    {
        this.renderElements.add(new IconElement(event.entity.position));
    }

    @Subscribe
    public void onIconParticleEvent(IconParticleEvent event)
    {
        this.renderElements.add(new IconElement(event.icon, event.color, event.scale, event.position));
    }

    public void clear()
    {
        this.renderElements.clear();
    }

    public void init()
    {
        this.shaders = new Shaders2D();
        this.context = BBS.getRender();

        FontRenderer fontDefault = BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round.json"));
        FontRenderer fontMonoDefault = BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round_mono.json"));

        this.context.setup(fontDefault, fontMonoDefault, BBS.getVAOs(), BBS.getTextures());
        this.context.setUBO(this.shaders.ubo);

        this.context.getShaders().register(this.shaders.vertexRGBA2D);
        this.context.getShaders().register(this.shaders.vertexUVRGBA2D);

        this.draw = new UIDraw(this.context);
        this.batcher = new Batcher2D(this.context);

        this.context.getTextures().getTexture(STARS).setWrap(GL11.GL_REPEAT);
    }

    public void update()
    {
        this.timer += 1;

        Iterator<RenderElement> it = this.renderElements.iterator();

        while (it.hasNext())
        {
            RenderElement renderElement = it.next();

            renderElement.update();

            if (renderElement.canBeRemoved())
            {
                it.remove();
            }
        }
    }

    public void render(float transition)
    {
        this.preRender(transition);

        Vector2i scaledResolution = this.getScaledResolution();

        FontRenderer font = this.context.getFont();

        this.renderBackground(scaledResolution, transition);
        this.renderWorld(font, scaledResolution, transition);

        this.draw.gradientVBox(0, 0, scaledResolution.x, 40, Colors.A100, 0);
        this.draw.gradientVBox(0, scaledResolution.y - 40, scaledResolution.x, scaledResolution.y, 0, Colors.A100);

        if (this.engine.state.getCore() != null)
        {
            this.renderHealth(font, scaledResolution, transition);
            this.renderResources(font, scaledResolution, transition);
        }
    }

    private void renderBackground(Vector2i resolution, float transition)
    {
        this.context.getTextures().bind(STARS);

        float x1 = -this.engine.controller.cameraX / 1.2F + (this.timer + transition) / 10F;
        float y1 = -this.engine.controller.cameraY / 1.2F;
        float t = this.timer + transition;
        float a = (float) Math.pow(Math.sin(t / 14F), 3) * 0.2F + 0.8F;

        int w = resolution.x;
        int h = resolution.y;
        float x2 = x1 + w;
        float y2 = y1 + h;

        this.context.stack.push();
        this.context.stack.translate(w / 2, h / 2, 0);
        this.context.stack.rotateZ((float) Math.cos(t / 60F) * 0.01F);

        this.context.getUBO().updateView(this.context.stack.getModelMatrix());

        this.draw.customTexturedBox(Colors.setA(Colors.WHITE, a), -w / 2, -h / 2, x1, y1, w, h, 256, 256, x2, y2);

        this.context.stack.pop();

        this.context.getUBO().updateView(this.context.stack.getModelMatrix());
    }

    private void renderWorld(FontRenderer font, Vector2i resolution, float transition)
    {
        GameController2D controller = this.engine.controller;

        this.context.stack.push();
        this.context.stack.translate(controller.cameraX + resolution.x / 2, controller.cameraY + resolution.y / 2, 0);

        for (Entity2D entity : this.engine.state.getEntitites())
        {
            entity.render(this.context, this.batcher);
        }

        for (RenderElement element : this.renderElements)
        {
            element.render(this.context, this.batcher);
        }

        this.batcher.render();

        this.context.stack.pop();
    }

    private void renderHealth(FontRenderer font, Vector2i resolution, float transition)
    {
        int health = this.engine.state.getHealth();

        int x = 20;
        int y = 20;

        for (int i = 0; i < health; i++)
        {
            Icons.HEART.render(this.draw, x, y, Colors.A100 | Colors.RED, 0.5F, 0.5F);

            x += 20;

            if (x > resolution.x * 0.4F)
            {
                y += 20;
                x = 20;
            }
        }
    }

    private void renderResources(FontRenderer font, Vector2i resolution, float transition)
    {
        /* Render energy */
        GameState state = this.engine.state;
        String label = String.format("%,d", state.getEnergy().get());
        int lw = font.getWidth(label);
        int x = resolution.x / 2;
        int y = 20;
        float s = Interpolation.EXP_IN.interpolate(0, 1, state.getEnergy().getTimer(transition)) + 1F;

        this.context.stack.push();
        this.context.stack.translate(x, y, 0);
        this.context.stack.scale(s, s, s);

        this.context.getUBO().updateView(this.context.stack.getModelMatrix());

        font.renderWithShadow(this.context, label, -lw / 2, -font.getHeight() / 2);

        Icons.SPHERE.render(this.draw, -lw / 2, 0, Colors.A100 | 0x00ccff, 1F, 0.5F);

        /* Render crystals */
        label = String.format("%,d", state.getCrystals().get());
        lw = font.getWidth(label);
        x = resolution.x - 20 - lw;
        y = 20;
        s = Interpolation.EXP_IN.interpolate(0, 1, state.getCrystals().getTimer(transition)) + 1F;

        this.context.stack.pop();

        this.context.stack.push();
        this.context.stack.translate(x, y, 0);
        this.context.stack.scale(s, s, s);

        this.context.getUBO().updateView(this.context.stack.getModelMatrix());

        font.renderWithShadow(this.context, label, -lw / 2, -font.getHeight() / 2);

        Icons.SHARD.render(this.draw, -lw / 2, 0, Colors.A100 | 0xcc00ff, 1F, 0.5F);

        this.context.stack.pop();

        this.context.getUBO().updateView(this.context.stack.getModelMatrix());

        /* Render info */
        font.renderWithShadow(this.context, "Wave " + state.getWave() + " - " + state.getSeconds(), 20, resolution.y - 20 - font.getHeight());

        /* Render spells */
        List<ISpell> spells = state.getSpells();

        x = resolution.x - 40 - 25 * (spells.size() - 1);
        y = resolution.y - 40;

        for (ISpell spell : spells)
        {
            spell.getIcon().render(this.draw, x + 10, y + 10, 0.5F, 0.5F);

            float cooldown = spell.getCooldown(transition);

            if (cooldown > 0)
            {
                this.draw.box(x, y + (int) (20 * cooldown), x + 20, y + 20, 0x88ffffff);
            }

            x += 25;
        }
    }

    private void preRender(float transition)
    {
        this.context.setTransition(transition);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GLStates.setupDepthFunction2D();

        this.shaders.ubo.update(this.shaders.ortho, Matrices.EMPTY_4F);
    }

    public Vector2i getScaledResolution()
    {
        int scale = BBSSettings.getScale();

        return new Vector2i(
            Window.width / scale,
            Window.height / scale
        );
    }

    public Vector2i getMouse()
    {
        int scale = BBSSettings.getScale();

        return new Vector2i(
            this.engine.mouse.x / scale,
            this.engine.mouse.y / scale
        );
    }

    public void resize(int width, int height)
    {
        this.shaders.resize(width, height);
    }
}