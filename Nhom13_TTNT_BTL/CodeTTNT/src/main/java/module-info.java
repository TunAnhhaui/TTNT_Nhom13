module com.example.nhom4ai.codettnt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.nhom4ai.codettnt to javafx.fxml;
    exports com.example.nhom4ai.codettnt;
}