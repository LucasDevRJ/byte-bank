package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContaDAO {
    private Connection conexao;
    ContaDAO(Connection conexao) {
        this.conexao = conexao;
    }
    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);

        String sql = "INSERT INTO conta(numero, saldo, cliente_nome, cliente_cpf, cliente_email)"+
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement parametros = conexao.prepareStatement(sql);

            parametros.setInt(1, conta.getNumero());
            parametros.setBigDecimal(2, BigDecimal.ZERO);
            parametros.setString(3, conta.getTitular().getNome());
            parametros.setString(4, conta.getTitular().getCpf());
            parametros.setString(5, conta.getTitular().getEmail());
            parametros.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
