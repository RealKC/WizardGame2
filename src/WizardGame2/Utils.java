package WizardGame2;

public class Utils {
    /**
     * Clamps x to the interval [min, max]
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @param x a value which may or may not be in the interval
     * @return x if x is in [min, max], min if x is less than min, or max otherwise
     */
    public static int clamp(int min, int max, int x) {
        if (x < min) {
            return min;
        }

        // I find the flow of the function easier to read this way
        //noinspection ManualMinMaxCalculation
        if (max < x) {
            return max;
        }

        return x;
    }
}
