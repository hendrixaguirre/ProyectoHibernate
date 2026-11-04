module ni.edu.uam.proyectohibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;


    opens ni.edu.uam.proyectohibernate to javafx.fxml;
    exports ni.edu.uam.proyectohibernate;
}