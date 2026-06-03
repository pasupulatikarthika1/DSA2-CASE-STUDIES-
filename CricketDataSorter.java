package cricket_analytics;

import java.util.*;

public class CricketDataSorter {

    // Representation of a ball-by-ball delivery record
    public static class Delivery {
        int over;
        int ball;
        int batsmanId, bowlerId, runs;

        public Delivery(int over, int ball) {
            this.over = over;
            this.ball = ball;
        }

        @Override
        public String toString() {
            return "(" + over + "," + ball + ")";
        }
    }

    /**
     * Stable counting sort execution focused on the 'over' key field.
     * @param in Input array of Delivery objects
     * @return Sorted array sorted stably by over field boundaries
     */
    public static Delivery[] countingSortByOver(Delivery[] in) {
        // Maximum key threshold specified by case study (0 to 49)
        final int K = 50; 
        int[] count = new int[K + 1];

        // 1. Count occurrences of each over index value
        for (Delivery d : in) {
            count[d.over]++;
        }

        // 2. Convert counts to prefix sums to determine tracking indices
        for (int i = 1; i <= K; i++) {
            count[i] += count[i - 1];
        }

        // 3. Build the output destination array
        Delivery[] out = new Delivery[in.length];

        // 4. Backward iteration loop: mandatory to preserve stability properties
        for (int i = in.length - 1; i >= 0; i--) {
            Delivery d = in[i];
            out[--count[d.over]] = d;
        }

        return out;
    }

    /**
     * Stable counting sort execution focused on the 'ball' key field.
     */
    public static Delivery[] countingSortByBall(Delivery[] in) {
        final int K = 12; // Adjusted to handle potential extra deliveries up to 12
        int[] count = new int[K + 1];

        for (Delivery d : in) {
            count[d.ball]++;
        }

        for (int i = 1; i <= K; i++) {
            count[i] += count[i - 1];
        }

        Delivery[] out = new Delivery[in.length];

        for (int i = in.length - 1; i >= 0; i--) {
            Delivery d = in[i];
            out[--count[d.ball]] = d;
        }

        return out;
    }

    public static void main(String[] args) {
        // Mocking the 10 mini-delivery sample sets from the prompt
        int[][] rawData = {
            {2, 4}, {1, 1}, {3, 6}, {1, 5}, {2, 2}, 
            {3, 1}, {1, 3}, {2, 4}, {3, 4}, {1, 2}
        };

        Delivery[] dataset = new Delivery[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            dataset[i] = new Delivery(rawData[i][0], rawData[i][1]);
        }

        System.out.println("=== CRICKET SCORING ANK SORT ENGINE ===");
        System.out.println("Original Dataset Input: " + Arrays.toString(dataset));

        // Execute LSD Radix Sort: Pass 1 (Least Significant Key = Ball)
        Delivery[] pass1 = countingSortByBall(dataset);
        System.out.println("Pass 1 (Sorted by Ball): " + Arrays.toString(pass1));

        // Execute LSD Radix Sort: Pass 2 (Most Significant Key = Over)
        Delivery[] pass2 = countingSortByOver(pass1);
        System.out.println("Pass 2 (Sorted Stably by Over): " + Arrays.toString(pass2));
    }
}
