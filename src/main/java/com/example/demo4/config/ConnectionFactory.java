package com.example.demo4.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://localhost:5432/Gerenciamento de Tarefas";
    private static final String USER = "postgres"; // Nome de usuário do banco
    private static final String PASSWORD = "2005"; // Senha do banco

    public static Connection getConnection() {
        try {
            // Tenta estabelecer a conexão com o banco de dados
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão estabelecida com sucesso!");
            return conn;
        } catch (SQLException e) {
            // Caso ocorra um erro na conexão, exibe uma mensagem e retorna null
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }
}
