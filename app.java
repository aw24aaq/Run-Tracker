package gui;

import core.Calculations.Pace;
import core.Calculations;
import core.Predictions;
import core.Zones;
import core.Utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RunningApp extends Application {

    private ComboBox<String> unitCombo;
    private TextField paceMinField;
    private TextField paceSecField;
    private ComboBox<String> distPresetCombo;
    private TextField customDistField;
    private TextArea outputArea;

    private static final Map<String, Double> PRESET_DISTANCES_KM = new LinkedHashMap<>();
    static {
        PRESET_DISTANCES_KM.put("5K", 5.0);
        PRESET_DISTANCES_KM.put("10K", 10.0);
        PRESET_DISTANCES_KM.put("Half Marathon", 21.097);
        PRESET_DISTANCES_KM.put("Marathon", 42.195);
        PRESET_DISTANCES_KM.put("Custom", null);
    }

    private String lastOutput = "";

    @Override
    public void start(Stage stage) {
        stage.setTitle("Running Pace & Race Performance Calculator");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        // Unit selection
        grid.add(new Label("Pace unit:"), 0, 0);
        unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll("km", "mile");
        unitCombo.setValue("km");
        grid.add(unitCombo, 1, 0);

        // Pace inputs
        grid.add(new Label("Pace minutes:"), 0, 1);
        paceMinField = new TextField();
        grid.add(paceMinField, 1, 1);

        grid.add(new Label("Pace seconds:"), 0, 2);
        paceSecField = new TextField();
        grid.add(paceSecField, 1, 2);

        // Distance preset
        grid.add(new Label("Distance preset:"), 0, 3);
        distPresetCombo = new ComboBox<>();
        distPresetCombo.getItems().addAll(PRESET_DISTANCES_KM.keySet());
        distPresetCombo.setValue("5K");
        grid.add(distPresetCombo, 1, 3);

        // Custom distance
        grid.add(new Label("Custom distance (km):"), 0, 4);
        customDistField = new TextField();
        grid.add(customDistField, 1, 4);

        // Buttons
        Button calcBtn = new Button("Calculate");
        calcBtn.setOnAction(e -> calculate());

        Button saveBtn = new Button("Save Results");
        saveBtn.setOnAction(e -> saveResults());

        HBox buttonRow = new HBox(10, calcBtn, saveBtn);

        // Output area
        outputArea = new TextArea();
        outputArea.setPrefRowCount(20);
        outputArea.setPrefColumnCount(80);

        root.getChildren().addAll(grid, buttonRow, outputArea);

        stage.setScene(new Scene(root, 700, 600));
        stage.show();
    }

    private Double parseFloat(String text, String name) {
        try {
            double v = Double.parseDouble(text);
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            showError("Invalid " + name + ".");
            return null;
        }
    }

    private Double getDistanceKm() {
        String preset = distPresetCombo.getValue();
        Double dist = PRESET_DISTANCES_KM.get(preset);
        if (dist == null) {
            return parseFloat(customDistField.getText(), "custom distance");
        }
        return dist;
    }

    private void calculate() {
        String unit = unitCombo.getValue();

        Double paceMin = parseFloat(paceMinField.getText(), "pace minutes");
        if (paceMin == null) return;

        Double paceSec = parseFloat(paceSecField.getText(), "pace seconds");
        if (paceSec == null) return;

        Double distKm = getDistanceKm();
        if (distKm == null) return;

        Pace pace = new Pace(paceMin, paceSec, unit);

        double effectiveDistance = unit.equals("km")
                ? distKm
                : Calculations.kmToMiles(distKm);

        double totalTimeSeconds = Calculations.getTimeSeconds(
                effectiveDistance, pace.getTotalSeconds()
        );

        int[] hms = Calculations.secondsToHms(totalTimeSeconds);
        int h = hms[0], m = hms[1], s = hms[2];

        double[] speeds = Calculations.averageSpeed(distKm, totalTimeSeconds);
        double speedKmh = speeds[0];
        double speedMph = speeds[1];

        double distanceM = distKm * 1000;
        double vo2 = Predictions.cooperVo2max(distanceM, totalTimeSeconds);
        double vdot = Predictions.simpleVdotFromPace(pace.getTotalSeconds());

        // Predictions
        String[][] targets = {
                {"5K", "5.0"},
                {"10K", "10.0"},
                {"Half Marathon", "21.097"},
                {"Marathon", "42.195"}
        };

        StringBuilder sb = new StringBuilder();
        sb.append("=== Results ===\n");
        sb.append(String.format("Pace: %d:%02d per %s\n", paceMin.intValue(), paceSec.intValue(), unit));
        sb.append(String.format("Distance: %.3f km (%.3f miles)\n", distKm, Calculations.kmToMiles(distKm)));
        sb.append(String.format("Predicted Time: %dh %dm %ds\n", h, m, s));
        sb.append(String.format("Average speed: %.2f km/h (%.2f mph)\n", speedKmh, speedMph));
        sb.append(String.format("Estimated VO2max: %.1f\n", vo2));
        sb.append(String.format("VDOT-like index: %.1f\n\n", vdot));

        sb.append("Race time predictions (Riegel):\n");
        for (String[] t : targets) {
            String name = t[0];
            double dKm = Double.parseDouble(t[1]);
            double t2 = Predictions.riegelPredict(totalTimeSeconds, distKm, dKm);
            sb.append(String.format("  %s (%.3f km): %s\n", name, dKm, Predictions.formatTimeHms(t2)));
        }

        sb.append("\nPace zones:\n");
        List<Zones.ZonePace> zones = Zones.zonePaces(pace.getTotalSeconds());
        for (Zones.ZonePace zp : zones) {
            int[] lower = Zones.secondsToMinSec(zp.lower);
            int[] upper = Zones.secondsToMinSec(zp.upper);
            sb.append(String.format(
                    "  %s [%s]: %d:%02d - %d:%02d per km (approx)\n",
                    zp.zone.getName(), zp.zone.getColor(),
                    lower[0], lower[1], upper[0], upper[1]
            ));
        }

        lastOutput = sb.toString();
        outputArea.setText(lastOutput);
    }

    private void saveResults() {
        if (lastOutput.isBlank()) {
            showWarning("Please calculate first.");
            return;
        }
        Utils.saveResultsToFile(lastOutput);
        showInfo("Results saved to results.txt");
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
