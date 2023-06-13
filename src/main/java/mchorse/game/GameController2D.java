package mchorse.game;

import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.game.domain.entities.CoreEntity2D;
import mchorse.game.domain.entities.Entity2D;
import mchorse.game.domain.entities.Mob2D;
import mchorse.game.domain.spells.ISpell;
import mchorse.game.domain.utils.Hitbox;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GameController2D implements IMouseHandler, IKeyHandler
{
    public GameEngine2D engine;

    public boolean cameraDragging;
    public float cameraX;
    public float cameraY;
    public int lastX;
    public int lastY;

    public boolean holdingLMB;

    private int timer;

    public GameController2D(GameEngine2D engine)
    {
        this.engine = engine;
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        if (key == GLFW.GLFW_KEY_H && action == GLFW.GLFW_PRESS)
        {
            Mob2D entity = new Mob2D();
            float x = (float) (Math.random() * 200 + 100);
            float y = (float) (Math.random() * 200 + 100);

            entity.setHealth(2);

            if (Math.random() < 0.5) x *= -1;
            if (Math.random() < 0.5) y *= -1;

            entity.teleport(x, y);

            this.engine.state.addEntity(entity);

            return true;
        }
        else if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9 && action == GLFW.GLFW_PRESS)
        {
            int i = key - GLFW.GLFW_KEY_1;
            List<ISpell> spells = this.engine.state.getSpells();
            ISpell spell = CollectionUtils.inRange(spells, i) ? spells.get(i) : null;

            if (spell != null && spell.getCooldown(0) <= 0)
            {
                Vector2i cursor = this.engine.renderer.getMouse();
                Vector2i resolution = this.engine.renderer.getScaledResolution();
                float x = cursor.x - resolution.x / 2 - this.cameraX;
                float y = cursor.y - resolution.y / 2 - this.cameraY;

                spell.cast(new Vector2f(x, y));
            }

            return true;
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {}

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        if (action == GLFW.GLFW_PRESS)
        {
            Vector2i cursor = this.engine.renderer.getMouse();

            if (button == GLFW.GLFW_MOUSE_BUTTON_3 || (Window.isCtrlPressed() && button == GLFW.GLFW_MOUSE_BUTTON_1))
            {
                this.cameraDragging = true;
                this.lastX = cursor.x;
                this.lastY = cursor.y;
            }
            else if (button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                this.holdingLMB = true;

                this.click();
            }
        }
        else if (action == GLFW.GLFW_RELEASE)
        {
            this.cameraDragging = false;
            this.holdingLMB = false;
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {}

    public void update()
    {
        this.timer += 1;

        int rate = CoreEntity2D.processRate(10, 2, this.engine.state.getStats().cursorClickRate.get());

        if (this.holdingLMB && this.timer % rate == 0)
        {
            this.click();
        }
    }

    private void click()
    {
        Vector2i cursor = this.engine.renderer.getMouse();
        Vector2i resolution = this.engine.renderer.getScaledResolution();
        float x = cursor.x - resolution.x / 2 - this.cameraX;
        float y = cursor.y - resolution.y / 2 - this.cameraY;
        Hitbox hitbox = new Hitbox(x - 10F, y - 10F, 20F, 20F);
        CoreEntity2D core = this.engine.state.getCore();

        if (core.hitbox.intersects(hitbox))
        {
            this.engine.state.addClickEnergy();
        }
        else
        {
            for (Entity2D entity : this.engine.state.getEntitites())
            {
                if (entity instanceof Mob2D && entity.hitbox.intersects(hitbox))
                {
                    ((Mob2D) entity).damage(this.engine.state.getStats().cursorDamage.get());
                }
            }
        }
    }

    public void render()
    {
        if (this.cameraDragging)
        {
            Vector2i cursor = this.engine.renderer.getMouse();

            this.cameraX += cursor.x - this.lastX;
            this.cameraY += cursor.y - this.lastY;
            this.lastX = cursor.x;
            this.lastY = cursor.y;

            this.cameraX = MathUtils.clamp(this.cameraX, -960, 960);
            this.cameraY = MathUtils.clamp(this.cameraY, -960, 960);
        }
    }

    public void reset()
    {
        this.cameraDragging = false;
        this.holdingLMB = false;
    }
}