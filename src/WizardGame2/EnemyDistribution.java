package WizardGame2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Random;

/**
 * This class represents the distribution of enemies within a wave and allows one to pick
 * the next enemy that should be spawned.
 */
public class EnemyDistribution {
    public static class Deserializer implements JsonDeserializer<EnemyDistribution> {

        @Override
        public EnemyDistribution deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String rawDistribution = jsonElement.getAsString();
            String[] probabilities = rawDistribution.split(";");

            var ranges = new double[probabilities.length + 1];
            ranges[0] = 0.0;

            int last = 0;
            for (var probability : probabilities) {
                ranges[last + 1] = Double.parseDouble(probability) + ranges[last];
                last++;
            }

            var enemyDistribution = new EnemyDistribution();
            enemyDistribution.ranges = ranges;

            return enemyDistribution;
        }
    }

    private double[] ranges;
    private final Random random = new Random();

    public EnemyDistribution() {
    }

    public String pickEnemy(String[] enemies) {
        assert ranges.length == enemies.length + 1;

        double pick = random.nextDouble();

        for (int i = 0; i < ranges.length - 1; ++i) {
            if (ranges[i] <= pick && pick < ranges[i + 1]) {
                return enemies[i];
            }
        }

        throw new RuntimeException("Failed to pick an enemy");
    }

    @Override
    public String toString() {
        return "EnemyDistribution{" +
                "ranges=" + Arrays.toString(ranges) +
                '}';
    }
}
