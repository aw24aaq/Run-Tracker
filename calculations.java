package core;

public class Calculations {

    public static final double KM_PER_MILE = 1.609344;
    public static final double MILE_PER_KM = 1.0 / KM_PER_MILE;

    // --- Pace class (equivalent to @dataclass) ---
    public static class Pace {
        private final double minutes;
        private final double seconds;
        private final String unit; // "km" or "mile"

        public Pace(double minutes, double seconds, String unit) {
            this.minutes = minutes;
            this.seconds = seconds;
            this.unit = unit;
        }

        public double getMinutes() {
            return minutes;
        }

        public double getSeconds() {
            return seconds;
        }

        public String getUnit() {
            return unit;
        }

        public double getTotalSeconds() {
            return (minutes * 60.0) + seconds;
        }
    }

    // --- get_time_seconds ---
    public static double getTimeSeconds(double distance, double paceSeconds) {
        return distance * paceSeconds;
    }

    // --- seconds_to_hms ---
    public static int[] secondsToHms(double totalSeconds) {
        int rounded = (int) Math.round(totalSeconds);
        int hours = rounded / 3600;
        int minutes = (rounded % 3600) / 60;
        int seconds = rounded % 60;
        return new int[]{hours, minutes, seconds};
    }

    // --- km_to_miles ---
    public static double kmToMiles(double km) {
        return km * MILE_PER_KM;
    }

    // --- miles_to_km ---
    public static double milesToKm(double miles) {
        return miles * KM_PER_MILE;
    }

    // --- average_speed ---
    // Returns (km/h, mph) just like Python returns a tuple
    public static double[] averageSpeed(double distanceKm, double totalSeconds) {
        double hours = totalSeconds / 3600.0;
        if (hours == 0) {
            return new double[]{0.0, 0.0};
        }
        double speedKmh = distanceKm / hours;
        double speedMph = kmToMiles(distanceKm) / hours;
        return new double[]{speedKmh, speedMph};
    }
}
