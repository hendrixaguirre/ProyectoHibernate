package ni.edu.uam.proyectohibernate.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.proyectohibernate.dao.LibroDAO;
import ni.edu.uam.proyectohibernate.model.Libro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibroController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtAnio;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, Long> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, Integer> colAnio;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;

    private final LibroDAO libroDAO = new LibroDAO();

    // ID del registro seleccionado en el TableView.
    private Long idSeleccionado = null;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tblLibros.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                seleccionarLibro(newValue);
                actualizarBotones();
            }else{
                idSeleccionado = null;
                actualizarBotones();
            }
        });
        actualizarBotones();
        cargarLibros();
    }

    private void seleccionarLibro(Libro libro){
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        actualizarBotones();
    }

    private void actualizarBotones(){
        boolean sinSeleccion = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Libro obtenerLibroFormulario(){
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        int anioPublicacion = Integer.valueOf(txtAnio.getText());
        BigDecimal precio = new BigDecimal(txtPrecio.getText());
        return new Libro(titulo, autor, anioPublicacion, precio);
    }

    @FXML
    private void guardarLibro(){
        try{
            Libro libro = obtenerLibroFormulario();
            libroDAO.guardar(libro);
            limpiarFormulario();
            cargarLibros();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void modificarLibro(){
        try{
            Libro libro = obtenerLibroFormulario();
            libro.setId(idSeleccionado);
            boolean modificado = libroDAO.modificar(libro);

            if(!modificado){
                showAlert(Alert.AlertType.ERROR,
                        "Error al modificar el libro",
                        "Error",
                        "El libro no existe"
                );
            }
            limpiarFormulario();
            cargarLibros();
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "La modificación fue exitosa",
                    "Libro Modificado",
                    "El libro fue modificado correctamente"
            );
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void eliminarLibro(){
        try{
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación de eliminación de datos");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText("¿Está seguro de que desea eliminar registro seleccionado?");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if(resultado.isEmpty() || resultado.get() != ButtonType.OK){
                return;
            }

            boolean eliminado = libroDAO.eliminar(idSeleccionado);
            if(!eliminado){
                showAlert(
                        Alert.AlertType.ERROR,
                        "Error al eliminar el libro",
                        "Error",
                        "La eliminación del libro con ID: " + idSeleccionado + " no fue completada"
                );
            }
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Eliminación exitosa",
                    "Libro Eliminado",
                    "El libro con ID: " + idSeleccionado + " fue eliminado correctamente"
            );
            limpiarFormulario();
            cargarLibros();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void cargarLibros(){
        try{
            List<Libro> libros = libroDAO.listar();
            limpiarFormulario();
            tblLibros.setItems(FXCollections.observableArrayList(libros));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormulario(){
        tblLibros.getSelectionModel().clearSelection();
        idSeleccionado = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtAnio.clear();
        txtPrecio.clear();
        actualizarBotones();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
