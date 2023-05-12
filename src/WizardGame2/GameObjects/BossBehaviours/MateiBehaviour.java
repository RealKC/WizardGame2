package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

@SuppressWarnings("unused")
public class MateiBehaviour implements Boss.Behaviour {
    Boss boss;

    @Override
    public void attachTo(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void update(Level level, long currentTime) {
        System.out.printf("Matei: Let's rock @ (%d, %d)\n", boss.getX(), boss.getY());
    }
}
