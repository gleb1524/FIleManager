module ru.gb.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires io.netty.codec;
    requires java.desktop;
    requires ru.gb.dto;
    exports ru.gb.client;
    exports ru.gb.client.net;
}