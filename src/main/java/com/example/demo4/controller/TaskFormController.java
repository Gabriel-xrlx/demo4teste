package com.example.demo4.controller;

import com.example.demo4.dao.TaskDAO;
import com.example.demo4.models.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class TaskFormController {
    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpDueDate;
    @FXML private ComboBox<String> cbStatus;

    private HelloController mainController;
    private TaskDAO taskDAO = new TaskDAO();

    @FXML
    public void initialize() {
        // Configura os valores possíveis para o status
        cbStatus.getItems().addAll("A Fazer", "Em Andamento", "Concluído");
        cbStatus.getSelectionModel().selectFirst();

        // Define a data atual como padrão
        dpDueDate.setValue(LocalDate.now());
    }

    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        if (validateFields()) {
            // Cria uma nova tarefa com os dados do formulário
            Task newTask = new Task(
                    txtTitle.getText(),
                    txtDescription.getText(),
                    dpDueDate.getValue(),
                    cbStatus.getValue()
            );

            // Salva no banco de dados
            taskDAO.save(newTask);

            // Fecha a janela
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateFields() {
        if (txtTitle.getText().isEmpty()) {
            showAlert("Erro", "O título da tarefa é obrigatório.");
            return false;
        }

        if (dpDueDate.getValue() == null) {
            showAlert("Erro", "A data de entrega é obrigatória.");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        txtTitle.getScene().getWindow().hide();
    }

    public void setTask(Task task) {
    }
}