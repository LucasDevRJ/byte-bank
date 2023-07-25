package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private Set<Conta> contas = new HashSet<>();

    private ConnectionFactory conexao;

    public ContaService() {
        this.conexao = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = conexao.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = conexao.recuperarConexao();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        BigDecimal novoValor = conta.getSaldo().subtract(valor);
        Connection conn = conexao.recuperarConexao();
        new ContaDAO(conn).alterar(numeroDaConta, novoValor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        BigDecimal novoValor = conta.getSaldo().add(valor);
        alterar(conta, novoValor);
    }


    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conexao = this.conexao.recuperarConexao();

        new ContaDAO(conexao).deletar(numeroDaConta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = conexao.recuperarConexao();
        Conta conta = new ContaDAO(conn).listarPorNumero(numero);
        if(conta != null) {
            return conta;
        } else {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }
    }

    private void alterar(Conta conta, BigDecimal valor) {
        Connection conn = conexao.recuperarConexao();
        new ContaDAO(conn).alterar(conta.getNumero(), valor);
    }

    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroContaOrigem, valor);
        this.realizarDeposito(numeroContaDestino, valor);
    }
}