package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {
    public static void main(String[] args) {
        try {
            Connection conexao = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/byte_bank?user=root&password=root");

            System.out.println("Conectado com o Banco de Dados!");

            conexao.close();
        } catch (SQLException e) {
            System.err.println("Erro: falha ao conectar com banco de dados!");
        }
    }
}
