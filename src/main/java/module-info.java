module com.svalero.psaa2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires static lombok;
    requires retrofit2;
    requires com.google.gson;
    requires retrofit2.converter.gson;
    requires retrofit2.adapter.rxjava3;
    requires io.reactivex.rxjava3;

    //MUY IMPORTANTE poner esto o GSON no tendra acceso a las clases de domain y fallara
    opens com.svalero.psaa2.domain to com.google.gson;
    opens com.svalero.psaa2 to javafx.fxml;
    exports com.svalero.psaa2;
}