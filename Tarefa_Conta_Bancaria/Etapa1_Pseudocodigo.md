# Tarefa – Conta Bancária
## Etapa 1 – Planejamento (Pseudocódigo)

**Integrante 1:** ______________________________
**Integrante 2:** ______________________________

---

### Decisões da dupla

| Item | Valor definido | Onde está no código |
|---|---|---|
| Limite por transferência | R$ 1.500,00 | `LIMITE_POR_TRANSFERENCIA` |
| Regra extra escolhida | Taxa em transferências acima de R$ 1.000,00 | `calcularTaxa()` |
| Percentual da taxa | 1% do valor, com mínimo de R$ 2,00 | `PERCENTUAL_TAXA` / `TAXA_MINIMA` |
| Valores aceitos | Somente valores maiores que zero | `valorEhValido()` |

---

## Regra 1 — Impedir saldo negativo

O saldo de uma conta nunca pode ficar abaixo de zero. Antes de qualquer
retirada de dinheiro (saque ou transferência), o sistema confere se o valor
a ser debitado cabe no saldo atual. **Na transferência, o que precisa caber
é o valor + a taxa**, não apenas o valor.

```
ALGORITMO impedirSaldoNegativo(conta, valorDebitado)

INÍCIO
    SE valorDebitado > conta.saldo ENTÃO
        ESCREVA "Operação recusada: saldo insuficiente"
        RETORNE FALSO                      // saldo não é alterado
    SENÃO
        conta.saldo <- conta.saldo - valorDebitado
        RETORNE VERDADEIRO
    FIM SE
FIM
```

Observação: a regra também vale na **abertura da conta**. Se alguém tentar
criar uma conta com saldo inicial negativo, o construtor avisa e abre a conta
com saldo R$ 0,00.

---

## Regra 2 (extra, escolha da dupla) — Taxa em transferências de valor alto

Transferências **acima de R$ 1.000,00** pagam uma taxa de **1% do valor
transferido**, respeitando uma **taxa mínima de R$ 2,00**. A taxa é debitada
de quem envia (a conta de origem) e **não** é creditada no destino: quem
recebe recebe o valor cheio.

```
ALGORITMO calcularTaxa(valor)

CONSTANTE VALOR_MINIMO_PARA_TAXA <- 1000.00
CONSTANTE PERCENTUAL_TAXA        <- 0.01      // 1%
CONSTANTE TAXA_MINIMA            <- 2.00

INÍCIO
    SE valor <= VALOR_MINIMO_PARA_TAXA ENTÃO
        RETORNE 0.00                       // transferência isenta
    SENÃO
        taxa <- valor * PERCENTUAL_TAXA
        SE taxa < TAXA_MINIMA ENTÃO
            taxa <- TAXA_MINIMA
        FIM SE
        RETORNE arredondar(taxa, 2 casas)
    FIM SE
FIM
```

**Exemplos:**

| Valor transferido | Taxa | Total debitado da origem |
|---|---|---|
| R$ 500,00 | R$ 0,00 (isento) | R$ 500,00 |
| R$ 1.000,00 | R$ 0,00 (isento, não passou do limite) | R$ 1.000,00 |
| R$ 1.200,00 | R$ 12,00 (1%) | R$ 1.212,00 |
| R$ 1.050,00 | R$ 10,50 (1%) | R$ 1.060,50 |

---

## Regra 3 — Validação de valores inválidos

Nenhuma operação aceita valor **zero, negativo** ou que não seja um número
válido (NaN / infinito). A checagem é feita **antes** de qualquer outra regra,
em depósito, saque e transferência.

```
ALGORITMO valorEhValido(valor, operacao)

INÍCIO
    SE valor não é um número válido ENTÃO
        ESCREVA operacao + ": o valor informado não é um número válido"
        RETORNE FALSO
    FIM SE

    SE valor <= 0 ENTÃO
        ESCREVA operacao + ": valor inválido. O valor deve ser maior que zero"
        RETORNE FALSO
    FIM SE

    RETORNE VERDADEIRO
FIM
```

---

## Algoritmo da transferência (junta as três regras)

```
ALGORITMO transferirPara(origem, destino, valor)

CONSTANTE LIMITE_POR_TRANSFERENCIA <- 1500.00

INÍCIO
    // 1) a conta de destino precisa existir e ser diferente da origem
    SE destino é NULO ENTÃO
        ESCREVA "Recusada: conta de destino não existe"
        RETORNE FALSO
    FIM SE
    SE destino = origem ENTÃO
        ESCREVA "Recusada: origem e destino são a mesma conta"
        RETORNE FALSO
    FIM SE

    // 2) REGRA 3 - valor inválido
    SE NÃO valorEhValido(valor, "Transferência") ENTÃO
        RETORNE FALSO
    FIM SE

    // 3) limite por transferência definido pela dupla
    SE valor > LIMITE_POR_TRANSFERENCIA ENTÃO
        ESCREVA "Recusada: valor acima do limite por transferência"
        RETORNE FALSO
    FIM SE

    // 4) REGRA 2 (extra) - taxa das transferências altas
    taxa          <- calcularTaxa(valor)
    totalDebitado <- valor + taxa

    // 5) REGRA 1 - saldo não pode ficar negativo
    SE totalDebitado > origem.saldo ENTÃO
        ESCREVA "Recusada: saldo insuficiente (contando a taxa)"
        RETORNE FALSO
    FIM SE

    // 6) só agora o dinheiro se move
    origem.saldo           <- origem.saldo - totalDebitado
    origem.totalTaxasPagas <- origem.totalTaxasPagas + taxa
    destino.saldo          <- destino.saldo + valor

    ESCREVA "Transferência concluída"
    RETORNE VERDADEIRO
FIM
```

**Por que essa ordem?** Primeiro as checagens que não custam nada e nunca
mudam estado (destino, valor, limite); a taxa só é calculada depois; e o saldo
só é alterado no último passo, quando já se sabe que a operação é válida.
Assim nunca acontece de o dinheiro sair de uma conta e não entrar na outra.

---

## Etapa 3 — Decisão sobre os getters

| Atributo | Getter? | Motivo |
|---|---|---|
| `numeroConta` | Sim | Identifica a conta; é informação pública do extrato. Sem setter: o número não muda. |
| `titular` | Sim | Precisa aparecer nas mensagens e no extrato. Sem setter. |
| `cpfTitular` | **Não** | Dado sensível. Só existe `getCpfMascarado()`, que devolve `***.456.789**`. |
| `saldo` | Sim (leitura) | Consultar saldo é normal. **Não existe `setSaldo()`** — o saldo só muda por `depositar()`, `sacar()` e `transferirPara()`, que aplicam as regras. |
| `totalTaxasPagas` | Sim | Útil para o extrato e para conferir o total de taxas cobradas. |

O método que credita o dinheiro no destino (`receberTransferencia`) é
**privado** de propósito: se fosse público, seria possível creditar valores em
uma conta sem passar por nenhuma regra.
