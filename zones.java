package core;

import java.util.ArrayList;
import java.util.List;

public class Zones {

    // --- Zone class (equivalent to @dataclass) ---
    public static class Zone {
        private final String name;
        private final double lowerFactor;
        private final double upperFactor;
        private final String color;

        public Zone(String name, double lowerFactor, double upperFactor, String color) {
            this.name = name;
            this.lowerFactor = lowerFactor;
            this.upperFactor = upperFactor;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public double getLowerFactor() {
            return lowerFactor;
        }

        public double getUpperFactor() {
            return upperFactor;
        }

        public String getColor() {
            return color;
        }
    }

    // --- Static list of zones (equivalent to ZONES = [...] ) ---
    public static final List<Zone> ZONES = List.of(
            new Zone("Easy", 1.15, 1.40, "green"),
            new Zone("Steady", 1.05, 1.15, "blue"),
            new Zone("Tempo", 0.95, 1.05, "yellow"),
            new Zone("Threshold", 0.90, 0.95, "orange"),
            new Zone("Interval", 0.80, 0.90, "red")
    );

    // --- zone_paces(base_pace_seconds) ---
    // Returns a list of (Zone, lower, upper) just like Python
    public static List<ZonePace> zonePaces(double basePaceSeconds) {
        List<ZonePace> results = new ArrayList<>();
        for (Zone z : ZONES) {
            double lower = basePaceSeconds * z.getLowerFactor();
            double upper = basePaceSeconds * z.getUpperFactor();
            results.add(new ZonePace(z, lower, upper));
        }
        return results;
    }

    // Helper record-like class for returning (zone, lower, upper)
    public static class ZonePace {
        public final Zone zone;
        public final double lower;
        public final double upper;

        public ZonePace(Zone zone, double lower, double upper) {
            this.zone = zone;
            this.lower = lower;
            this.upper = upper;
        }
    }

    // --- seconds_to_min_sec ---
    public static int[] secondsToMinSec(double sec) {
        int rounded = (int) Math.round(sec);
        int minutes = rounded / 60;
        int seconds = rounded % 60;
        return new int[]{minutes, seconds};
    }
}
