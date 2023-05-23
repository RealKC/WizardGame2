package WizardGame2;

import WizardGame2.GameWindow.GameWindow;
import WizardGame2.Scenes.MainMenuScene;
import WizardGame2.Scenes.SceneManager;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Main class of the project
 * This class implements an event loop that will progress the game one tick forwards every 16.7ms (aka it attempts to
 * make the game run at 60fps)
 */
public class Game implements Runnable {
    private static Game instance;

    private final GameWindow wnd;
    private boolean runState;
    private Thread gameThread;

    SceneManager sceneManager;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game("WizardGame 2", 800, 600);
        }

        return instance;
    }

    /**
     * Game constructor
     * @param title Window title
     * @param width Window width
     * @param height Window height
     */
    private Game(String title, int width, int height) {
        wnd = new GameWindow(title, width, height);
        runState = false;
        DatabaseManager.getInstance();
    }

    /**
     * Performs game initialization (such as constructing the player instance, setting up the camera, etc)
     */
    private void initGame() {
        wnd.buildGameWindow();

        sceneManager = new SceneManager(new MainMenuScene());
    }

    /**
     * This function implements the game event loop
     */
    @Override
    public void run() {
        initGame();

        long lastFrameTimeNs = System.nanoTime();
        long currentFrameTimeNs;


        final int framesPerSecond = 60;
        final long timeFrame = 1000000000 / framesPerSecond;

        while (runState) {
            currentFrameTimeNs = System.nanoTime();
            if ((currentFrameTimeNs - lastFrameTimeNs) > timeFrame) {
                update(currentFrameTimeNs);
                draw();
                lastFrameTimeNs = currentFrameTimeNs;
            }
        }

    }

    /**
     * Exits the game, performing any clean-up needed
     */
    public void exit() {
        DatabaseManager.getInstance().commit();
        System.exit(0);
    }

    /**
     * Starts the game on its own thread
     */
    public synchronized void startGame() {
        if (!runState) {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * Updates the game objects
     * @param currentTime Current time in nanoseconds
     */
    private void update(long currentTime) {
        sceneManager.update(currentTime);
    }

    public int getWindowWidth() {
        return wnd.getWindowWidth();
    }

    public int getWindowHeight() {
        return wnd.getWindowHeight();
    }

    public Canvas getCanvas() {
        return wnd.getCanvas();
    }

    /**
     * Renders all game objects and the map
     */
    private void draw() {
        BufferStrategy bs = wnd.getCanvas().getBufferStrategy();

        if (bs == null) {
            try {
                /// We use triple buffering to avoid flickering

                ///                          |------------------------------------------------>|
                ///                          |                                                 |
                ///                  ****************          *****************        ***************
                ///                  *              *   Show   *               *        *             *
                /// [ Screen ] <---- * Front Buffer *  <------ * Middle Buffer * <----- * Back Buffer * <---- Draw()
                ///                  *              *          *               *        *             *
                ///                  ****************          *****************        ***************

                wnd.getCanvas().createBufferStrategy(3);
                return;
            } catch (Exception e) {
                Utils.logException(getClass(), e, "got an exception trying to create the buffer strategy");
            }
        }
        assert bs != null;

        Graphics gfx = bs.getDrawGraphics();

        // Clear the screen
        gfx.clearRect(0, 0, wnd.getWindowWidth(), wnd.getWindowHeight());

        sceneManager.render(gfx);
        drawDebugRibbon(gfx);
        bs.show();

        gfx.dispose();
    }

    private void drawDebugRibbon(Graphics gfx) {
        if (!Main.IS_DEBUG_BUILD) {
            return;
        }

        var oldFont = gfx.getFont();
        var oldColor = gfx.getColor();

        gfx.setColor(Color.red);
        gfx.setFont(new Font(Font.MONOSPACED, Font.BOLD, 35));

        gfx.drawString("DEBUG BUILD", 550, 30);

        gfx.setFont(oldFont);
        gfx.setColor(oldColor);
    }
}

