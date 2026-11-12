module com.courcework.pharmacyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; requires tools.jackson.databind;


    opens com.courcework.pharmacyclient.dto to tools.jackson.databind;
    opens com.courcework.pharmacyclient.controller to javafx.fxml;
    exports com.courcework.pharmacyclient;
}