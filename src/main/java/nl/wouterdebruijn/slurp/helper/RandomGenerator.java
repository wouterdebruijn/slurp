package nl.wouterdebruijn.slurp.helper;

import java.util.Random;

public class RandomGenerator {
    private static final int upperRange = 1000;

    /**
     * Generate a number with the standard upper bound of 1000
     *
     * @return Returns a random number between 0 and 1000
     */
    public static int defaultGenerator() {
        Random r = new Random();
        return r.nextInt(upperRange);
    }

    /**
     * Generate a number between 0 and the given upper bound.
     *
     * @param upper Upper bound of range of set of numbers used to generate.
     * @return Returns random number between 0 and upper bound.
     */
    public static int generate(int upper) {
        Random r = new Random();
        return r.nextInt(upper);
    }

    /**
     * Check if a player has a chance of 'winning the lottery' or not.
     *
     * @param upperBound Upper bound of the range of set of numbers used to generate.
     * @return Returns true if player has 'won the lottery', else returns false.
     */
    public static boolean hasChance(int upperBound) {
        int marker = generate(upperBound);
        int result = generate(upperBound);
        return marker == result;
    }
}
