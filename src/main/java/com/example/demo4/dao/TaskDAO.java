package com.example.demo4.dao;

import com.example.demo4.config.ConnectionFactory;
import com.example.demo4.models.Task; // Correção: Import da sua classe de modelo
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public void save(Task task) {
        String sql = "INSERT INTO task (titulo, descricao, data_entrega, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, Date.valueOf(task.getDueDate()));
            stmt.setString(4, task.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tarefa", e);
        }
    }

    public List<Task> findAll() {
        String sql = "SELECT * FROM task"; // Corrigido aqui
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getDate("data_entrega").toLocalDate(),
                        rs.getString("status")
                );
                task.setId(rs.getInt("id"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas", e);
        }

        return tasks;
    }

    // No TaskDAO, verifique o update:
    public boolean update(Task task) {
        if (task == null || task.getId() <= 0) {
            throw new IllegalArgumentException("Tarefa inválida para atualização");
        }

        String sql = "UPDATE task SET titulo = ?, descricao = ?, data_entrega = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, Date.valueOf(task.getDueDate()));
            stmt.setString(4, task.getStatus());
            stmt.setInt(5, task.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar tarefa ID " + task.getId(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();  // Captura o número de linhas afetadas
            return rowsAffected > 0;  // Retorna true se pelo menos 1 linha foi deletada

        } catch (SQLException e) {
            System.err.println("Erro ao deletar tarefa ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Task findById(int id) {
        String sql = "SELECT * FROM task WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task(
                            rs.getString("titulo"),
                            rs.getString("descricao"),
                            rs.getDate("data_entrega").toLocalDate(),
                            rs.getString("status")
                    );
                    task.setId(rs.getInt("id"));
                    return task;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa", e);
        }

        return null;
    }
    public class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}