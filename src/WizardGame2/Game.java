package WizardGame2;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Player;
import WizardGame2.GameWindow.GameWindow;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * The number of seconds that have passed since the start of the level
     */
    private int secondsPassed = 0;

    Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
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

        Timer timeTicker = new Timer();
        timeTicker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Game.this.tickASecond();
            }
        }, 0, 1000);

        player = new Player(assets.getCharacters(), 800, 600);
        player.getCamera().setCameraWidth(wnd.getWindowWidth());
        player.getCamera().setCameraHeight(wnd.getWindowHeight());

        enemies.add(new Enemy(assets.getCharacters().crop(1, 0), 50, 700, 32, 32));
        player.addPositionObserver(enemies.get(0));

        map = Map.fromData(assets.getMapDatas().get(0));

        player.getCamera().setMapWidth(map.getWidth());
        player.getCamera().setMapHeight(map.getHeight());
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

        for (var enemy : enemies) {
            enemy.update(map, currentTime);
        }

        for (var bullet : bullets) {
            bullet.update(map, currentTime);
        }

        int i = 0;
        while (i < bullets.size()) {
            int j = 0;

            Bullet bullet = bullets.get(i);
            while (j < enemies.size()) {
                if (bullet.collidesWith(enemies.get(j))) {
                    enemies.remove(j);

                    if (bullet.shouldBeRemovedAfterThisHit()) {
                        bullets.remove(i);
                        break;
                    }
                }
                j++;
            }

            i++;
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

        for (var bullet : bullets) {
            bullet.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        player.render(gfx, player.getCamera().getX(), player.getCamera().getY());

        for (var enemy : enemies) {
            enemy.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        gfx.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        var currentTime = String.format("%02d:%02d", secondsPassed / 60, secondsPassed % 60);
        var width = (int) gfx.getFontMetrics().getStringBounds(currentTime, gfx).getWidth();
        drawTextWithOutline(gfx, currentTime, wnd.getWindowWidth() / 2 - width / 2, 50);

        bs.show();

        gfx.dispose();
    }

    private static void drawTextWithOutline(Graphics gfx, String text, int x, int y) {
        // Based on <https://stackoverflow.com/a/35222059>
        if (gfx instanceof Graphics2D gfx2d) {
            Color oldColor = gfx2d.getColor();
            RenderingHints oldHints = gfx2d.getRenderingHints();
            Stroke oldStroke = gfx2d.getStroke();

            gfx2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gfx2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            GlyphVector glyphVector = gfx2d.getFont().createGlyphVector(gfx2d.getFontRenderContext(), text);
            Shape textShape = glyphVector.getOutline(x, y);

            // Paint the outline
            gfx2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gfx2d.setColor(Color.BLACK);
            gfx2d.draw(textShape);

            // Pain the text itself
            gfx2d.setColor(Color.WHITE);
            gfx2d.fill(textShape);

            gfx2d.setColor(oldColor);
            gfx2d.setRenderingHints(oldHints);
            gfx2d.setStroke(oldStroke);
        }
    }

    private synchronized void tickASecond() {
        secondsPassed++;
    }
}

