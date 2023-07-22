package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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

    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<Conta>();
        String sql = "SELECT * FROM conta";
        try {
            PreparedStatement clausulasSQL = this.conexao.prepareStatement(sql);
            ResultSet resultSet = clausulasSQL.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);

                contas.add(new Conta(numero, cliente));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }
}
