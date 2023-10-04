module org.openjfx.JavaFXLungEcho {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx.JavaFXLungEcho to javafx.fxml;
    exports org.openjfx.JavaFXLungEcho;
}
