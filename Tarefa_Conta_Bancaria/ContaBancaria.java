/* =========================================================================
 * Tarefa - Conta Bancária (Programação Orientada a Objetos)
 *
 * Integrante 1: ______________________________
 * Integrante 2: ______________________________
 *
 * Classe ContaBancaria
 * Etapas 2 e 3: classe, objeto, construtor e encapsulamento.
 * ========================================================================= */

import java.util.Locale;

public class ContaBancaria {

    /* ---------------------------------------------------------------------
     * ETAPA 3 - ENCAPSULAMENTO
     * Todos os atributos são PRIVADOS. Nada de fora da classe altera o
     * estado da conta diretamente: só através dos métodos abaixo, que
     * aplicam as regras definidas na Etapa 1.
     * ------------------------------------------------------------------- */

    private final String numeroConta;      // tem getter (identifica a conta, nunca muda)
    private final String titular;          // tem getter (nome do dono)
    private final String cpfTitular;       // NÃO tem getter: dado sensível (só versão mascarada)
    private double saldo;                  // tem getter, mas NÃO tem setter
    private double totalTaxasPagas;        // tem getter (útil para o extrato)

    /* ---------------------------------------------------------------------
     * REGRAS DE NEGÓCIO DEFINIDAS PELA DUPLA (constantes = não mudam)
     * ------------------------------------------------------------------- */

    // Limite máximo permitido em UMA transferência
    private static final double LIMITE_POR_TRANSFERENCIA = 1500.00;

    // Regra extra: transferências ACIMA deste valor pagam taxa
    private static final double VALOR_MINIMO_PARA_TAXA = 1000.00;

    // Taxa de 1% sobre o valor transferido...
    private static final double PERCENTUAL_TAXA = 0.01;

    // ... respeitando uma taxa mínima de R$ 2,00
    private static final double TAXA_MINIMA = 2.00;

    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    /* ---------------------------------------------------------------------
     * ETAPA 2 - CONSTRUTOR
     * Todo objeto já nasce com um estado válido e coerente.
     * ------------------------------------------------------------------- */
    public ContaBancaria(String numeroConta, String titular, String cpfTitular, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.cpfTitular = cpfTitular;

        // Regra 1 (saldo nunca negativo) já vale na criação da conta
        if (saldoInicial < 0) {
            System.out.println("[AVISO] Saldo inicial negativo não é permitido. A conta "
                    + numeroConta + " foi aberta com saldo R$ 0,00.");
            this.saldo = 0.0;
        } else {
            this.saldo = arredondar(saldoInicial);
        }

        this.totalTaxasPagas = 0.0;
    }

    /* =====================================================================
     * GETTERS
     * Decisão da dupla: expomos o que é apenas informativo e mantemos
     * escondido o que é sensível ou o que deve mudar somente por regra.
     * Não existe setSaldo(): o saldo só muda por depositar/sacar/transferir.
     * =================================================================== */

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getTotalTaxasPagas() {
        return totalTaxasPagas;
    }

    /**
     * O CPF completo NÃO é exposto. Só devolvemos a versão mascarada,
     * suficiente para conferência: 123.456.789-00 -> ***.456.789-**
     */
    public String getCpfMascarado() {
        if (cpfTitular == null || cpfTitular.length() < 14) {
            return "***";
        }
        return "***" + cpfTitular.substring(3, 11) + "**";
    }

    /* =====================================================================
     * ETAPA 1 - REGRAS TRANSFORMADAS EM MÉTODOS
     * =================================================================== */

    /**
     * REGRA 3 - Validação de valores inválidos (zero, negativo ou não numérico).
     */
    private boolean valorEhValido(double valor, String operacao) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            System.out.println("[ERRO] " + operacao + ": o valor informado não é um número válido.");
            return false;
        }
        if (valor <= 0) {
            System.out.printf(BR, "[ERRO] %s: valor inválido (R$ %,.2f). O valor deve ser maior que zero.%n",
                    operacao, valor);
            return false;
        }
        return true;
    }

    /**
     * REGRA EXTRA (escolha da dupla) - Taxa em transferências de valor alto.
     * Transferência acima de R$ 1.000,00 paga 1% do valor, com mínimo de R$ 2,00.
     * Até R$ 1.000,00 a taxa é zero.
     */
    private double calcularTaxa(double valor) {
        if (valor <= VALOR_MINIMO_PARA_TAXA) {
            return 0.0;
        }
        return arredondar(Math.max(valor * PERCENTUAL_TAXA, TAXA_MINIMA));
    }

    /* =====================================================================
     * OPERAÇÕES DA CONTA
     * Todos devolvem true (deu certo) ou false (regra impediu).
     * =================================================================== */

    /**
     * Depósito. Só valida o valor - depositar nunca deixa o saldo negativo.
     */
    public boolean depositar(double valor) {
        if (!valorEhValido(valor, "Depósito")) {
            return false;
        }
        saldo = arredondar(saldo + valor);
        System.out.printf(BR, "[OK] Depósito de R$ %,.2f na conta %s. Novo saldo: R$ %,.2f%n",
                valor, numeroConta, saldo);
        return true;
    }

    /**
     * Saque. Valida o valor (Regra 3) e impede saldo negativo (Regra 1).
     */
    public boolean sacar(double valor) {
        if (!valorEhValido(valor, "Saque")) {
            return false;
        }
        if (valor > saldo) {
            System.out.printf(BR, "[ERRO] Saque recusado: saldo insuficiente na conta %s "
                    + "(saldo R$ %,.2f, pedido R$ %,.2f).%n", numeroConta, saldo, valor);
            return false;
        }
        saldo = arredondar(saldo - valor);
        System.out.printf(BR, "[OK] Saque de R$ %,.2f na conta %s. Novo saldo: R$ %,.2f%n",
                valor, numeroConta, saldo);
        return true;
    }

    /**
     * ETAPA 3 - Transferência entre duas contas.
     * Ordem das verificações:
     *   1) conta de destino existe e é diferente da origem;
     *   2) valor é válido (Regra 3);
     *   3) valor respeita o limite por transferência definido pela dupla;
     *   4) regra extra: calcula a taxa quando o valor é alto;
     *   5) valor + taxa cabem no saldo (Regra 1 - nunca fica negativo);
     *   6) só então debita a origem e credita o destino.
     */
    public boolean transferirPara(ContaBancaria destino, double valor) {
        System.out.printf(BR, "-> Transferência de R$ %,.2f | %s (%s) para %s (%s)%n",
                valor,
                this.titular, this.numeroConta,
                (destino == null ? "conta inexistente" : destino.getTitular()),
                (destino == null ? "-" : destino.getNumeroConta()));

        // 1) Destino válido
        if (destino == null) {
            System.out.println("[ERRO] Transferência recusada: conta de destino não existe.");
            return false;
        }
        if (destino == this) {
            System.out.println("[ERRO] Transferência recusada: origem e destino são a mesma conta.");
            return false;
        }

        // 2) Regra 3 - valor inválido
        if (!valorEhValido(valor, "Transferência")) {
            return false;
        }

        // 3) Limite por transferência definido pela dupla
        if (valor > LIMITE_POR_TRANSFERENCIA) {
            System.out.printf(BR, "[ERRO] Transferência recusada: valor acima do limite por "
                    + "transferência (limite R$ %,.2f, pedido R$ %,.2f).%n",
                    LIMITE_POR_TRANSFERENCIA, valor);
            return false;
        }

        // 4) Regra extra - taxa em transferências acima de R$ 1.000,00
        double taxa = calcularTaxa(valor);
        double totalDebitado = arredondar(valor + taxa);
        if (taxa > 0) {
            System.out.printf(BR, "   Regra extra aplicada: taxa de R$ %,.2f (1%%, mínimo R$ %,.2f) "
                    + "por transferir acima de R$ %,.2f.%n", taxa, TAXA_MINIMA, VALOR_MINIMO_PARA_TAXA);
        }

        // 5) Regra 1 - saldo não pode ficar negativo (valor + taxa)
        if (totalDebitado > this.saldo) {
            System.out.printf(BR, "[ERRO] Transferência recusada: saldo insuficiente "
                    + "(saldo R$ %,.2f, necessário R$ %,.2f já com a taxa).%n", this.saldo, totalDebitado);
            return false;
        }

        // 6) Efetiva a operação
        this.saldo = arredondar(this.saldo - totalDebitado);
        this.totalTaxasPagas = arredondar(this.totalTaxasPagas + taxa);
        destino.receberTransferencia(valor);

        System.out.printf(BR, "[OK] Transferência concluída. Debitado R$ %,.2f (valor R$ %,.2f + taxa R$ %,.2f).%n",
                totalDebitado, valor, taxa);
        System.out.printf(BR, "     Saldo %s: R$ %,.2f | Saldo %s: R$ %,.2f%n",
                this.titular, this.saldo, destino.getTitular(), destino.getSaldo());
        return true;
    }

    /**
     * Crédito no destino. É PRIVADO DO PACOTE / da classe de propósito:
     * ninguém do lado de fora consegue "criar dinheiro" chamando este método
     * solto - ele só é usado por transferirPara(), depois de todas as regras.
     */
    private void receberTransferencia(double valor) {
        this.saldo = arredondar(this.saldo + valor);
    }

    /* =====================================================================
     * APOIO
     * =================================================================== */

    /** Evita as "sobras" do double: 100.1 + 0.2 -> 100.30 */
    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Extrato resumido da conta. */
    public void exibirDados() {
        System.out.printf(BR, "Conta %s | Titular: %s | CPF: %s | Saldo: R$ %,.2f | Taxas pagas: R$ %,.2f%n",
                numeroConta, titular, getCpfMascarado(), saldo, totalTaxasPagas);
    }

    @Override
    public String toString() {
        return String.format(BR, "ContaBancaria{numero='%s', titular='%s', saldo=R$ %,.2f}",
                numeroConta, titular, saldo);
    }
}
