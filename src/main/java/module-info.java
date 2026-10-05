module org.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens org.library to javafx.fxml, javafx.graphics;
    opens org.library.controller to javafx.fxml;
    opens org.library.model to javafx.base;

    exports org.library;
    exports org.library.controller;
    exports org.library.model;
    exports org.library.service;
    exports org.library.util;
}
