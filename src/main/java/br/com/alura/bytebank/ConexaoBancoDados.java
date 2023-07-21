package br.com.alura.bytebank;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {
    public static void main(String[] args) {
        try {
            DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/byte_bank?user=root&password=root");
        } catch (SQLException e) {
            System.err.println("Erro: falha ao conectar com banco de dados!");
        }
    }
}
