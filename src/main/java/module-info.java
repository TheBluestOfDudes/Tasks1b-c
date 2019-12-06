module eksamenTestFX {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens eksamenTestFX to javafx.fxml;
    exports eksamenTestFX;
}