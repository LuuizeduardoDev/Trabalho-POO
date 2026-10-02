/* =========================================================================
 * Tarefa - Conta Bancária (Programação Orientada a Objetos)
 *
 * Integrante 1: ______________________________
 * Integrante 2: ______________________________
 *
 * Classe Main
 * Etapa 2: criação de dois objetos com estados independentes.
 * Etapa 4: testes das regras (transferência válida, transferência que
 *          falha pela regra/limite e operação com valor inválido).
 * ========================================================================= */

import java.util.Locale;

public class Main {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    public static void main(String[] args) {

        titulo("ETAPA 2 - CRIANDO OS OBJETOS (DUAS CONTAS DE TITULARES DIFERENTES)");

        ContaBancaria contaAna = new ContaBancaria("0001-5", "Ana Souza", "123.456.789-00", 2000.00);
        ContaBancaria contaBruno = new ContaBancaria("0002-3", "Bruno Lima", "987.654.321-00", 500.00);

        contaAna.exibirDados();
        contaBruno.exibirDados();

        titulo("PROVA DE QUE CADA OBJETO TEM SEU PRÓPRIO ESTADO");

        System.out.println("Depositando R$ 300,00 APENAS na conta da Ana:");
        contaAna.depositar(300.00);
        System.out.println();
        System.out.println("Note que o saldo do Bruno não mudou:");
        contaAna.exibirDados();
        contaBruno.exibirDados();

        // ETAPA 3 - ENCAPSULAMENTO:
        // As duas linhas abaixo NÃO compilam, porque os atributos são privados.
        // É exatamente essa a proteção que queremos:
        //
        // contaAna.saldo = 1000000.00;   // erro: saldo has private access
        // contaAna.titular = "Outro";    // erro: titular has private access
        //
        // O saldo só muda por depositar(), sacar() e transferirPara().

        titulo("ETAPA 4 - TESTE 1: TRANSFERÊNCIA VÁLIDA (SEM TAXA)");
        System.out.println("Ana transfere R$ 500,00 para Bruno. Valor dentro do limite e");
        System.out.println("abaixo de R$ 1.000,00, então não há taxa.\n");
        boolean teste1 = contaAna.transferirPara(contaBruno, 500.00);
        resultado(teste1, true);

        titulo("ETAPA 4 - TESTE 2: TRANSFERÊNCIA VÁLIDA COM A REGRA EXTRA (TAXA)");
        System.out.println("Ana transfere R$ 1.200,00 para Bruno. Passa de R$ 1.000,00,");
        System.out.println("então a regra extra cobra 1% de taxa (R$ 12,00) da Ana.\n");
        boolean teste2 = contaAna.transferirPara(contaBruno, 1200.00);
        resultado(teste2, true);

        titulo("ETAPA 4 - TESTE 3: TRANSFERÊNCIA QUE FALHA PELO LIMITE");
        System.out.println("Bruno tenta transferir R$ 2.000,00, acima do limite de R$ 1.500,00");
        System.out.println("por transferência definido pela dupla. Deve ser recusada.\n");
        boolean teste3 = contaBruno.transferirPara(contaAna, 2000.00);
        resultado(teste3, false);

        titulo("ETAPA 4 - TESTE 4: TRANSFERÊNCIA QUE DEIXARIA O SALDO NEGATIVO");
        System.out.println("Ana tenta transferir R$ 1.050,00, mas com a taxa de R$ 10,50 o total");
        System.out.println("passa do saldo dela. A regra do saldo negativo bloqueia.\n");
        boolean teste4 = contaAna.transferirPara(contaBruno, 1050.00);
        resultado(teste4, false);

        titulo("ETAPA 4 - TESTE 5: OPERAÇÕES COM VALORES INVÁLIDOS");
        System.out.println("Zero e negativo devem ser recusados em qualquer operação.\n");
        boolean deposito = contaAna.depositar(0.00);
        boolean saque = contaBruno.sacar(-50.00);
        boolean transfer = contaAna.transferirPara(contaBruno, -100.00);
        System.out.println();
        resultado(deposito || saque || transfer, false);

        titulo("SITUAÇÃO FINAL DAS CONTAS");
        contaAna.exibirDados();
        contaBruno.exibirDados();

        double totalEmContas = contaAna.getSaldo() + contaBruno.getSaldo()
                + contaAna.getTotalTaxasPagas() + contaBruno.getTotalTaxasPagas();
        System.out.println();
        System.out.printf(BR, "Conferência: R$ 2.000,00 + R$ 500,00 (aberturas) + R$ 300,00 (depósito) = R$ 2.800,00%n");
        System.out.printf(BR, "Somando saldos + taxas cobradas: R$ %,.2f%n", totalEmContas);
        System.out.println(totalEmContas == 2800.00
                ? "Nenhum dinheiro foi criado nem perdido nas operações."
                : "ATENÇÃO: os valores não fecham.");
    }

    /* ---------------------------------------------------------------------
     * Métodos de apoio, só para deixar a saída do console organizada.
     * ------------------------------------------------------------------- */

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==============================================================");
        System.out.println(texto);
        System.out.println("==============================================================");
    }

    private static void resultado(boolean obtido, boolean esperado) {
        System.out.println();
        System.out.println("Resultado esperado: " + (esperado ? "operação aceita" : "operação recusada")
                + " | Obtido: " + (obtido ? "operação aceita" : "operação recusada")
                + " -> " + (obtido == esperado ? "TESTE OK" : "TESTE FALHOU"));
    }
}
