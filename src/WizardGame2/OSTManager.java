package WizardGame2;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class OSTManager {
    private AudioInputStream battleMusicStream;
    private AudioInputStream menuMusicStream;
    private AudioInputStream alertSoundStream;

    boolean isPlayingBossMusic = false;

    private Clip battleMusic = null;
    private Clip menuMusic = null;
    private Clip alertSound = null;

    private static OSTManager instance;

    public static OSTManager getInstance() {
        if (instance == null) {
            instance = new OSTManager();
        }

        return instance;
    }

    private OSTManager() {
        try {
            battleMusicStream =  AudioSystem.getAudioInputStream(Objects.requireNonNull(OSTManager.class.getResource("/ost/battle-music.wav"), "battle music is missing from resouces"));
            battleMusic = AudioSystem.getClip();
            battleMusic.open(battleMusicStream);

            menuMusicStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(OSTManager.class.getResource("/ost/menu-music.wav"), "menu music is missing from resources"));
            menuMusic = AudioSystem.getClip();
            menuMusic.open(menuMusicStream);

            alertSoundStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(OSTManager.class.getResource("/ost/alert.wav"), "the alert sound is missing from resources"));
            alertSound = AudioSystem.getClip();
            alertSound.open(alertSoundStream);
        } catch (UnsupportedAudioFileException e) {
            Utils.logException(getClass(), e, "failed loading audio");
        } catch (IOException e) {
            Utils.logException(getClass(), e, "got an IO error");
        } catch (LineUnavailableException e) {
            Utils.logException(getClass(), e, "line unavailable (???)");
        } catch (NullPointerException e) {
            Utils.logException(getClass(), e, "a resource was unavailable");
        }
    }

    public void playMenuMusic() {
        if (menuMusic == null) {
            return;
        }

        if (battleMusic != null && battleMusic.isRunning()) {
            battleMusic.stop();
        }

        menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void playBossMusic() {
        if (battleMusic == null) {
            return;
        }

        if (menuMusic != null && menuMusic.isRunning()) {
            menuMusic.stop();
        }

        battleMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void playAlertSound() {
        if (alertSound == null) {
            return;
        }

        if (battleMusic != null && battleMusic.isRunning()) {
            return;
        }

        alertSound.start();
    }

    public void stopMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.setFramePosition(0);
        }

        if (battleMusic != null) {
            battleMusic.stop();
            battleMusic.setFramePosition(0);
        }

        if (alertSound != null) {
            alertSound.stop();
            alertSound.setFramePosition(0);
        }
    }
}
