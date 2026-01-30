package core;

public class Predictions {

    // --- Riegel Prediction ---
    // Riegel formula: T2 = T1 * (D2 / D1)^exponent
    public static double riegelPredict(double tSeconds, double d1, double d2, double exponent) {
        if (d1 <= 0) {
            return 0.0;
        }
        double ratio = d2 / d1;
        return tSeconds * Math.pow(ratio, exponent);
    }

    // Overload with default exponent = 1.06
    public static double riegelPredict(double tSeconds, double d1, double d2) {
        return riegelPredict(tSeconds, d1, d2, 1.06);
    }

    // --- Format time as H:M:S ---
    public static String formatTimeHms(double totalSeconds) {
        int[] hms = Calculations.secondsToHms(totalSeconds);
        int h = hms[0];
        int m = hms[1];
        int s = hms[2];
        return h + "h " + m + "m " + s + "s";
    }

    // --- Cooper VO2max Estimate ---
    // VO2max ≈ (distance_m - 504.9) / 44.73 (normalized to 12 minutes)
    public static double cooperVo2max(double distanceM, double timeSeconds) {
        if (timeSeconds <= 0) {
            return 0.0;
        }
        double normalizedDistance = distanceM * ((12 * 60.0) / timeSeconds);
        return (normalizedDistance - 504.9) / 44.73;
    }

    // --- Simple VDOT-like metric from pace ---
    public static double simpleVdotFromPace(double paceSecondsPerKm) {
        if (paceSecondsPerKm <= 0) {
            return 0.0;
        }
        double minutes = paceSecondsPerKm / 60.0;
        double estimate = 90.0 - (minutes * 10.0);
        return Math.max(20.0, estimate);
    }
}