module grant.coburn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires jbcrypt;
    requires com.opencsv;
    requires io;
    requires kernel;
    requires layout;
    requires org.slf4j;
    
    opens grant.coburn to javafx.fxml;
    opens grant.coburn.view.admin to javafx.fxml;
    exports grant.coburn;
    exports grant.coburn.view;
    exports grant.coburn.model;
    exports grant.coburn.view.admin;
    exports grant.coburn.report;
}