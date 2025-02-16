module grant.coburn {
    requires javafx.controls;
    requires javafx.fxml;

    opens grant.coburn to javafx.fxml;
    exports grant.coburn;
}
