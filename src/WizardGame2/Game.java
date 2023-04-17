package WizardGame2;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Obstacle;
import WizardGame2.GameObjects.Player;
import WizardGame2.GameWindow.GameWindow;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

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

    Player player;
    Enemy enemy;
    Map map;

    private final ArrayList<Bullet> bullets = new ArrayList<>();

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
    }

    /**
     * Performs game initialization (such as constructing the player instance, setting up the camera, etc)
     */
    private void initGame() {
        wnd.buildGameWindow();

        var assets = Assets.getInstance();

        player = new Player(assets.getCharacters(), 800, 600);
        player.getCamera().setCameraWidth(wnd.getWindowWidth());
        player.getCamera().setCameraHeight(wnd.getWindowHeight());

        enemy = new Enemy(assets.getCharacters().crop(1, 0), 50, 700, 32, 32);
        player.addPositionObserver(enemy);

        map = new Map(new Obstacle[4], assets.getMapDatas().get(0));

        player.getCamera().setMapWidth(map.getWidth());
        player.getCamera().setMapHeight(map.getHeight());

        map.obstacles[0] = new Obstacle(null, 50, 50, 200, 100);
        map.obstacles[1] = new Obstacle(null, 150, 200, 200, 100);
        map.obstacles[2] = new Obstacle(null, 150, 500, 200, 100);
        map.obstacles[3] = new Obstacle(null, 50, 650, 200, 100);
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
     * Stops the game thread
     */
    public synchronized void stopGame() {
        if (runState) {
            /// Actualizare stare thread
            runState = false;
            /// Metoda join() arunca exceptii motiv pentru care trebuie incadrata intr-un block try - catch.
            try {
                /// Metoda join() pune un thread in asteptare panca cand un altul isi termina executie.
                /// Totusi, in situatia de fata efectul apelului este de oprire a threadului.
                gameThread.join();
            } catch (InterruptedException ex) {
                /// In situatia in care apare o exceptie pe ecran vor fi afisate informatii utile pentru depanare.
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets all the bullets in the game
     */
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    /**
     * Updates the game objects
     * @param currentTime Current time in nanoseconds
     */
    private void update(long currentTime) {
        player.update(map, currentTime);
        enemy.update(map, currentTime);

        for (var bullet : bullets) {
            bullet.update(map, currentTime);
        }
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
                e.printStackTrace();
            }
        }
        assert bs != null;

        Graphics gfx = bs.getDrawGraphics();

        // Clear the screen
        gfx.clearRect(0, 0, wnd.getWindowWidth(), wnd.getWindowHeight());

        map.render(gfx, player.getCamera());

        player.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        enemy.render(gfx, player.getCamera().getX(), player.getCamera().getY());

        for (var bullet : bullets) {
            bullet.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        bs.show();

        gfx.dispose();
    }
}

