module grant.coburn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    
    opens grant.coburn to javafx.fxml;
    exports grant.coburn;
}
