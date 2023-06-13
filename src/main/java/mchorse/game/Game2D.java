package mchorse.game;

import mchorse.bbs.utils.TimePrintStream;
import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.utils.CrashReport;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.cli.ArgumentParser;
import mchorse.bbs.utils.cli.ArgumentType;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class Game2D
{
    public static final String VERSION = "0.1";
    public static final String GIT_HASH = "@GIT_HASH@";
    public static final String FULL_VERSION = VERSION + (GIT_HASH.startsWith("@") ? " (dev)" : " (" + GIT_HASH + ")");

    /* Command line arguments */

    public File gameDirectory;
    public String defaultWorld = "flat";
    public int windowWidth = 1280;
    public int windowHeight = 720;
    public boolean development;

    public static void main(String[] args)
    {
        System.out.println(IOUtils.readText(BBS.class.getResourceAsStream("/assets/strings/title.txt")));
        System.out.println("\nBBS: " + FULL_VERSION + ", LWJGL: " + Version.getVersion() + ", GLFW: " + GLFW.glfwGetVersionString());

        System.setOut(new TimePrintStream(System.out));
        System.setErr(new TimePrintStream(System.err));

        ArgumentParser parser = new ArgumentParser();

        parser.register("gameDirectory", ArgumentType.PATH)
            .register("defaultWorld", "dw", ArgumentType.STRING)
            .register("width", "ww", ArgumentType.NUMBER)
            .register("height", "wh", ArgumentType.NUMBER)
            .register("development", "dev", ArgumentType.NUMBER);

        Game2D game = new Game2D();

        game.setup(parser.parse(args));
        game.launch();
    }

    private void setup(MapType data)
    {
        if (data.has("gameDirectory"))
        {
            this.gameDirectory = new File(data.getString("gameDirectory"));
        }

        this.defaultWorld = data.getString("defaultWorld", "flat");
        this.windowWidth = data.getInt("width", this.windowWidth);
        this.windowHeight = data.getInt("height", this.windowHeight);
        this.development = data.getBool("development", this.development);
    }

    public void launch()
    {
        if (this.gameDirectory == null || !this.gameDirectory.isDirectory())
        {
            throw new IllegalStateException("Given game directory '" + this.gameDirectory + "' doesn't exist or not a directory...");
        }

        GameEngine2D engine = new GameEngine2D(this);
        long id = -1;

        try
        {
            /* Start the game */
            Window.initialize("Core Survivor " + FULL_VERSION, this.windowWidth, this.windowHeight, false);
            Window.setupStates();

            id = Window.getWindow();

            engine.init();
            engine.start(id);
        }
        catch (Exception e)
        {
            File crashes = new File(this.gameDirectory, "crashes");
            Pair<File, String> crash = CrashReport.writeCrashReport(crashes, e, "BBS " + FULL_VERSION + " has crashed! Here is a crash stacktrace:");

            /* Here we should actually save a crash log with exception
             * and other relevant information */
            e.printStackTrace();

            CrashReport.showDialogue(crash, "BBS " + FULL_VERSION + " has crashed! The crash log " + crash.a.getName() + " was generated in \"crashes\" folder, which you should send to BBS' developer(s).\n\nIMPORTANT: don't screenshot this window!");
        }

        /* Terminate the game */
        engine.delete();

        Callbacks.glfwFreeCallbacks(id);
        GLFW.glfwDestroyWindow(id);

        GLFW.glfwSetErrorCallback(null).free();
        GLFW.glfwTerminate();
    }
}