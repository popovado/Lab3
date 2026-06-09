module org.example.iterator {
    requires javafx.controls;
    requires javafx.fxml;

    // Открываем пакет с контроллером для FXMLLoader
    opens ru.itmo.coursework.iterator to javafx.fxml;

    // Экспортируем основной пакет приложения (где лежит HelloApplication)
    exports org.example.iterator;
}