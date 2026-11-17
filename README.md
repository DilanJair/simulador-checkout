# Simulador de Checkout

Projeto em Java puro para o desafio de padrões de projeto da DIO. No terminal, você monta um carrinho com produtos de um catálogo em memória e compara Pix e cartão. Cada simulação mantém o carrinho disponível para uma nova escolha.

## Rodar no Windows

Use o JDK 21 e abra o PowerShell na pasta que contém este README. Compile e execute:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem .\src\checkout -Filter *.java).FullName
java -cp out checkout.Main
```

Em Linux ou macOS, use `javac -encoding UTF-8 -d out src/checkout/*.java` para compilar e o mesmo comando `java` para executar.

## Demonstração

1. Use a opção **Adicionar produto** para colocar um fone Bluetooth (código 1) e duas luminárias de mesa (código 2) no carrinho. O subtotal deve ser R$ 600,00.
2. Use **Simular pagamento** e selecione **Pix**. O total deve ser R$ 570,00.
3. No mesmo menu, selecione **Cartão** em **6x**. O resultado deve ser seis parcelas de R$ 100,00.
4. Experimente **7x**. O total deve ser R$ 624,00, dividido em duas parcelas de R$ 89,15 e cinco de R$ 89,14.
5. Experimente **11x** para verificar a validação. Use **Voltar ao menu principal** para rever os mesmos itens no carrinho.

## Regras do projeto

- O carrinho soma as quantidades de um mesmo produto antes de verificar o estoque. A simulação consulta o carrinho atual, sem consumir estoque.
- O Pix aplica 5% de desconto no subtotal. O cartão aceita de 1 a 10 parcelas. Até 6x o total é igual ao subtotal.
- A tabela de acréscimos do simulador é fictícia. O percentual incide uma vez sobre o subtotal.

| Parcelas | Acréscimo |
| --- | ---: |
| 7x | 4% |
| 8x | 6% |
| 9x | 8% |
| 10x | 10% |

Os cálculos monetários usam `BigDecimal`. O desconto e o acréscimo são arredondados para centavos com `HALF_UP`. Quando a divisão em parcelas deixa centavos sobrando, as primeiras parcelas recebem um centavo a mais até a soma fechar.

## Onde entra o Strategy

[`EstrategiaPagamento`](src/checkout/EstrategiaPagamento.java) define o contrato de cálculo. [`PagamentoPix`](src/checkout/PagamentoPix.java) aplica o desconto. [`PagamentoCartao`](src/checkout/PagamentoCartao.java) calcula o total e as parcelas. Em [`Main.java`](src/checkout/Main.java), `mostrarSimulacao` recebe qualquer uma das duas implementações e passa a ela o subtotal do carrinho.

A pasta `src/checkout/` guarda o código. A pasta `test/checkout/` guarda os casos de verificação. O nome `checkout` corresponde ao pacote declarado nos arquivos Java. O `.gitignore` mantém a pasta de compilação `out/` fora do Git.

### Verificações automatizadas no PowerShell

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem .\src\checkout, .\test\checkout -Filter *.java).FullName
java -cp out checkout.CheckoutTest
```

Em Linux ou macOS, a compilação equivalente é `javac -encoding UTF-8 -d out src/checkout/*.java test/checkout/*.java`.

## Publicar no GitHub

No GitHub, crie um repositório chamado `simulador-checkout`. Deixe o repositório vazio, com as opções de gerar README, licença e gitignore desmarcadas. O projeto já tem README e gitignore. Use a visibilidade pública se quiser que a DIO e visitantes possam abrir o link diretamente. [Guia do GitHub](https://docs.github.com/pt/migrations/importing-source-code/using-the-command-line-to-import-source-code/adding-locally-hosted-code-to-github)

Abra o PowerShell na pasta deste README. Git registra as versões no seu computador e GitHub recebe essas versões quando você faz `push`. Troque `SEU-USUARIO` pelo seu nome de usuário no GitHub e execute:

```powershell
git init -b main
git add .
git status
git commit -m "Cria simulador de checkout em Java"
git remote add origin https://github.com/SEU-USUARIO/simulador-checkout.git
git push -u origin main
```

Se o Git pedir um nome e e-mail para o commit, configure sua identidade com `git config user.name "Seu nome"` e `git config user.email "Seu e-mail"` nessa pasta e repita a partir do `git commit`. Para manter seu e-mail privado em um repositório público, use o endereço `noreply` disponível nas configurações de e-mail do GitHub.

Confira se o repositório publicado mostra `README.md`, `src/` e `test/`. Depois, cole o endereço do repositório no formulário **Entregar Projeto** da DIO e descreva o fluxo de checkout e a aplicação do Strategy.
