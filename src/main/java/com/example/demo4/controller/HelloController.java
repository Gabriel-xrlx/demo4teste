package com.example.demo4.controller;

import com.example.demo4.dao.TaskDAO;
import com.example.demo4.models.Task; // Import correto da sua classe Task
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HelloController {
    @FXML
    private ListView<Task> tasksListView;
    @FXML private TableColumn<Task, Integer> colId;
    @FXML private TableColumn<Task, String> colTitle;
    @FXML private TableColumn<Task, String> colDescription;
    @FXML private TableColumn<Task, LocalDate> colDueDate;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private Label lblDateTime;

    @FXML
    private ListView<Task> taskListView;
    private TaskDAO taskDAO = new TaskDAO();

    @FXML
    public void initialize() {
        setupListView(); // Você também não está chamando isso
        loadTasks();
        startClock();    // Adicione esta linha
    }

    private void setupListView() {
        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setText(null);
                } else {
                    setText(task.toString());
                }
            }
        });
    }

    private void setupTable() {

        // Configura as colunas com PropertyValueFactory usando os nomes EXATOS dos getters
        colId.setCellValueFactory(new PropertyValueFactory<>("id")); // Corresponde a getId()
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title")); // getTitle()
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description")); // getDescription()
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate")); // getDueDate()
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status")); // getStatus()

        // Configuração especial para a coluna de data
        colDueDate.setCellFactory(column -> new TableCell<Task, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? "" : formatter.format(date));
            }
        });

        // Adiciona listener para debug
        tasksListView.itemsProperty().addListener((obs, old, newVal) -> {
            System.out.println("Itens na tabela: " + newVal.size());
        });
    }

    private void loadTasks() {
        // Certifique-se de que taskListView foi inicializado corretamente
        if (taskListView != null) {
            // Supondo que TaskDAO seja uma classe que recupera as tarefas
            List<Task> tasks = taskDAO.findAll();
            taskListView.getItems().setAll(tasks);
        }
    }

    private void startClock() {
        Thread thread = new Thread(() -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final String time = dtf.format(LocalDateTime.now());
                    Platform.runLater(() -> {
                        if (lblDateTime != null) { // Verifica se o label ainda existe
                            lblDateTime.setText(time);
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restaura o estado de interrupção
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleAddTask() {
        try {
            // Carrega o arquivo FXML usando o class loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo4/task_form.fxml"));
            Parent root = loader.load();

            TaskFormController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Adicionar Nova Tarefa");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadTasks();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar o formulário: " + e.getMessage());

            // Debug adicional - verifique o caminho do recurso
            URL url = getClass().getResource("/com/example/demo4/task_form.fxml");
            System.out.println("URL do recurso: " + url);
        }
    }

    // No HelloController, verifique:
    @FXML
    private void handleEditTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert("Aviso", "Nenhuma tarefa selecionada");
            return;
        }

        // Cria o contêiner principal
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Campo Título
        Label lblTitle = new Label("Título:");
        TextField txtTitle = new TextField(selectedTask.getTitle());
        txtTitle.setPrefWidth(300);

        // Campo Descrição
        Label lblDesc = new Label("Descrição:");
        TextArea txtDesc = new TextArea(selectedTask.getDescription());
        txtDesc.setPrefRowCount(4);
        txtDesc.setWrapText(true);

        // Campo Data
        Label lblDate = new Label("Data de Entrega:");
        DatePicker dpDate = new DatePicker();
        if (selectedTask.getDueDate() != null) {
            dpDate.setValue(selectedTask.getDueDate());
        }

        // Campo Status
        Label lblStatus = new Label("Status:");
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("A Fazer", "Em Andamento", "Concluído");
        cbStatus.setValue(selectedTask.getStatus() != null ? selectedTask.getStatus() : "A Fazer");

        // Botões
        HBox buttonBox = new HBox(10);
        Button btnSave = new Button("Salvar");
        Button btnCancel = new Button("Cancelar");
        buttonBox.getChildren().addAll(btnSave, btnCancel);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Adiciona todos os componentes ao layout
        root.getChildren().addAll(
                lblTitle, txtTitle,
                lblDesc, txtDesc,
                lblDate, dpDate,
                lblStatus, cbStatus,
                new Separator(),
                buttonBox
        );

        // Cria a cena
        Scene scene = new Scene(root, 400, 400);

        // Configura a janela
        Stage stage = new Stage();
        stage.setTitle("Editar Tarefa: " + selectedTask.getTitle());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        // Ações dos botões
        btnSave.setOnAction(e -> {
            // Atualiza a tarefa com os novos valores
            selectedTask.setTitle(txtTitle.getText());
            selectedTask.setDescription(txtDesc.getText());
            selectedTask.setDueDate(dpDate.getValue());
            selectedTask.setStatus(cbStatus.getValue());

            // Salva no banco de dados
            if (taskDAO.update(selectedTask)) {
                loadTasks(); // Atualiza a lista
                stage.close();
            } else {
                showAlert("Erro", "Não foi possível salvar as alterações");
            }
        });

        btnCancel.setOnAction(e -> stage.close());

        // Mostra a janela
        stage.showAndWait();
    }

    @FXML
    private void handleDeleteTask() {
        // Use taskListView que é o nome correto do seu componente
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText("Excluir Tarefa: " + selectedTask.getTitle());
            alert.setContentText("Tem certeza que deseja excluir esta tarefa?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (taskDAO.delete(selectedTask.getId())) {
                    // Atualização otimizada da lista
                    taskListView.getItems().remove(selectedTask);
                    System.out.println("Tarefa removida com sucesso!"); // Debug
                } else {
                    showAlert("Erro", "Falha ao excluir a tarefa do banco de dados.");
                }
            }
        } else {
            showAlert("Nenhuma tarefa selecionada", "Por favor, selecione uma tarefa para excluir.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
    }
    private void showTaskForm(Task task) {

        try {

            // Use o mesmo caminho que funciona no handleAddTask()
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo4/task_form.fxml"));
            Parent root = loader.load();

            TaskFormController controller = loader.getController();
            controller.setTask(task);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle(task == null ? "Adicionar Tarefa" : "Editar Tarefa - " + task.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Atualiza a lista após edição
            loadTasks();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar o formulário: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}