package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ContaDAO {
    //Pegando conexão com o banco de dados
    private Connection conexao;
    ContaDAO(Connection conexao) {
        this.conexao = conexao; //entregando a conexão
    }
    public void salvar(DadosAberturaConta dadosDaConta) {
        //criando cliente e conta do mesmo
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente);

        //claúsula SQL para atribuir valores na conta
        String sql = "INSERT INTO conta(numero, saldo, cliente_nome, cliente_cpf, cliente_email)"+
                "VALUES (?, ?, ?, ?, ?)";

        try {
            //para criarmos cláusulas SQL
            PreparedStatement parametros = conexao.prepareStatement(sql);

            //settando valores no banco de dados
            parametros.setInt(1, conta.getNumero());
            parametros.setBigDecimal(2, BigDecimal.ZERO);
            parametros.setString(3, conta.getTitular().getNome());
            parametros.setString(4, conta.getTitular().getCpf());
            parametros.setString(5, conta.getTitular().getEmail());
            parametros.execute();
            //fechando conexão com o banco
            parametros.close();
            conexao.close();
        } catch (SQLException e) { //tratando exceção
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {
        PreparedStatement clausulasSQL;
        ResultSet resultSet;
        Set<Conta> contas = new HashSet<Conta>();
        String sql = "SELECT * FROM conta";

        try {
            clausulasSQL = this.conexao.prepareStatement(sql);
            resultSet = clausulasSQL.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);

                contas.add(new Conta(numero, saldo, cliente));
            }
            clausulasSQL.close();
            resultSet.close();
            conexao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta listarPorNumero(Integer numero) {
        String sql = "SELECT * FROM conta WHERE numero = ?";

        PreparedStatement ps;
        ResultSet resultSet;
        Conta conta = null;
        try {
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, numero);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente);
            }
            resultSet.close();
            ps.close();
            conexao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            this.conexao.setAutoCommit(false);

            ps = this.conexao.prepareStatement(sql);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.execute();
            this.conexao.commit();
            ps.close();
            this.conexao.close();
        } catch (SQLException e) {
            try {
                this.conexao.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public void deletar(Integer numero) {
        String sql = "DELETE FROM conta WHERE numero = ?";

        try {
            PreparedStatement clausulasSql = this.conexao.prepareStatement(sql);
            clausulasSql.execute();
            clausulasSql.close();
            this.conexao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
