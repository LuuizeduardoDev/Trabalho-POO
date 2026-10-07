[README.md](https://github.com/user-attachments/files/33135470/README.md)
# Conta Bancária — Trabalho de POO

Trabalho em dupla da disciplina de **Programação Orientada a Objetos**: uma classe `ContaBancaria` em Java que aplica regras de negócio (saldo nunca negativo, limite por transferência, taxa em transferências altas e validação de valores) usando **classe, objeto, construtor e encapsulamento**.

## Estrutura

```
Tarefa_Conta_Bancaria/
├── ContaBancaria.java        # A classe com atributos privados e as regras
├── Main.java                 # Cria duas contas e executa os testes da Etapa 4
├── Etapa1_Pseudocodigo.md    # Planejamento das regras em pseudocódigo
├── Etapa1_Pseudocodigo.pdf   # Mesmo planejamento, em PDF para entrega
├── saida_do_console.txt      # Saída obtida ao executar o programa
└── LEIA-ME.txt
```

## Como executar

Requisito: JDK 8 ou mais recente.

```bash
cd Tarefa_Conta_Bancaria
javac -encoding UTF-8 *.java
java Main
```

> **No Windows**, se os acentos aparecerem trocados no terminal, rode `chcp 65001` antes de executar o programa.

A saída esperada está em [`saida_do_console.txt`](Tarefa_Conta_Bancaria/saida_do_console.txt).

## Etapas do trabalho

| Etapa | O que foi feito | Onde está |
|---|---|---|
| 1 — Planejamento | Regras de negócio descritas em pseudocódigo | [`Etapa1_Pseudocodigo.md`](Tarefa_Conta_Bancaria/Etapa1_Pseudocodigo.md) |
| 2 — Classe, objeto e construtor | Classe `ContaBancaria` e duas contas com estados independentes | `ContaBancaria.java`, `Main.java` |
| 3 — Encapsulamento | Atributos privados, getters escolhidos e transferência entre contas | `ContaBancaria.java` |
| 4 — Testes | Transferência válida, transferência recusada pela regra/limite e valores inválidos | `Main.java` |

## Regras de negócio

| Regra | Definição |
|---|---|
| Saldo nunca negativo | Saque e transferência só acontecem se o valor (mais a taxa, quando houver) couber no saldo. Saldo inicial negativo vira R$ 0,00. |
| Limite por transferência | No máximo **R$ 1.500,00** por transferência. |
| Regra extra: taxa | Transferências **acima de R$ 1.000,00** pagam **1%** do valor, com mínimo de **R$ 2,00**. A taxa sai da conta de origem; o destino recebe o valor cheio. |
| Valores inválidos | Zero, negativos, NaN e infinito são recusados em depósito, saque e transferência. |

## Decisões de encapsulamento

- **Não existe `setSaldo()`**: o saldo só muda por `depositar()`, `sacar()` e `transferirPara()`, que aplicam as regras.
- **O CPF completo não é exposto**: só existe `getCpfMascarado()` (`***.456.789**`).
- **`receberTransferencia()` é privado**: ninguém de fora consegue creditar dinheiro em uma conta sem passar pelas regras da transferência.
- `numeroConta`, `titular` e `cpfTitular` são `final`: definidos no construtor e nunca mais alterados.

## Testes (Etapa 4)

| Teste | Cenário | Esperado |
|---|---|---|
| 1 | Ana transfere R$ 500,00 para Bruno (sem taxa) | Aceita |
| 2 | Ana transfere R$ 1.200,00 (taxa de R$ 12,00) | Aceita |
| 3 | Bruno tenta transferir R$ 2.000,00 (acima do limite) | Recusada |
| 4 | Ana tenta transferir R$ 1.050,00 + R$ 10,50 de taxa sem saldo suficiente | Recusada |
| 5 | Depósito de R$ 0,00, saque de −R$ 50,00 e transferência de −R$ 100,00 | Recusadas |

Ao final, o programa confere que a soma dos saldos com as taxas cobradas continua igual ao dinheiro que entrou (R$ 2.800,00): nenhum valor foi criado nem perdido.

## Autores

- Luiz Eduardo — [@LuuizeduardoDev](https://github.com/LuuizeduardoDev)
