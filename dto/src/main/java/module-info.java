module ru.gb.dto {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.dto to javafx.fxml;
    exports ru.gb.dto;
}