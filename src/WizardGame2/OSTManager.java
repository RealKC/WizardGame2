package WizardGame2;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class OSTManager {
    private AudioInputStream battleMusic;
    private AudioInputStream menuMusic;

    private Clip clip = null;

    private static OSTManager instance;

    public static OSTManager getInstance() {
        if (instance == null) {
            instance = new OSTManager();
        }

        return instance;
    }

    private OSTManager() {
        try {
            battleMusic =  AudioSystem.getAudioInputStream(Objects.requireNonNull(OSTManager.class.getResource("/ost/battle-music.wav")));
            menuMusic = AudioSystem.getAudioInputStream(Objects.requireNonNull(OSTManager.class.getResource("/ost/menu-music.wav")));
            clip = AudioSystem.getClip();
        } catch (UnsupportedAudioFileException e) {
            Utils.logException(getClass(), e, "failed loading audio");
        } catch (IOException e) {
            Utils.logException(getClass(), e, "got an IO error");
        } catch (LineUnavailableException e) {
            Utils.logException(getClass(), e, "line unavailable (???)");
        }
    }

    public void playMenuMusic() {
        if (menuMusic == null || clip == null) {
            return;
        }

        clip.stop();
        clip.close();

        try {
            clip.open(menuMusic);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            Utils.logException(getClass(), e, "failed to get clip for menu music");
        } catch (IOException e) {
            Utils.logException(getClass(), e, "got an IO error trying to play menu music");
        }
    }

    public void playBossMusic() {
        if (battleMusic == null || clip == null) {
            return;
        }

        clip.stop();
        clip.close();

        try {
            clip.open(battleMusic);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            Utils.logException(getClass(), e, "failed to get clip for battle music");
        } catch (IOException e) {
            Utils.logException(getClass(), e, "got an IO error trying to play battle music");
        }
    }

    public void stopMusic() {
        if (clip == null) {
            return;
        }

        clip.stop();
    }
}