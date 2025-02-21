module grant.coburn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires jbcrypt;
    
    opens grant.coburn to javafx.fxml;
    exports grant.coburn;
    exports grant.coburn.view;
    exports grant.coburn.model;
}