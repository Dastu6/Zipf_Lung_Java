module org.openjfx.JavaFXLungEcho {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.desktop;
	requires java.base;
	requires transitive dcm4che.core;
	requires commons.cli;
	requires dcm4che.imageio;
		

    opens org.openjfx.JavaFXLungEcho to javafx.fxml;
    exports org.openjfx.JavaFXLungEcho;
}
