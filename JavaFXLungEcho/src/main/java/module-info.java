module org.openjfx.JavaFXLungEcho {
    requires javafx.controls;
    requires javafx.fxml;
	requires aspose.imaging;
	requires java.desktop;

    opens org.openjfx.JavaFXLungEcho to javafx.fxml;
    exports org.openjfx.JavaFXLungEcho;
}
