package checkout;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    private static final List<Produto> CATALOGO = List.of(
        new Produto(1, "Fone Bluetooth", "Tecnologia", new BigDecimal("400.00"), 2),
        new Produto(2, "Luminária de mesa", "Casa", new BigDecimal("100.00"), 3),
        new Produto(3, "Café", "Alimentos", new BigDecimal("30.00"), 4),
        new Produto(4, "Mousepad", "Tecnologia", new BigDecimal("50.00"), 1)
    );

    private final Carrinho carrinho = new Carrinho();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final Scanner entrada;

    private Main(Scanner entrada) {
        this.entrada = entrada;
    }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            new Main(entrada).executar();
        } catch (NoSuchElementException erro) {
            System.out.println("\nEntrada encerrada.");
        }
    }

    private void executar() {
        mostrarCatalogo();
        while (true) {
            System.out.println("\n1. Ver catálogo");
            System.out.println("2. Adicionar produto");
            System.out.println("3. Ver carrinho");
            System.out.println("4. Simular pagamento");
            System.out.println("0. Sair");

            switch (lerInteiro("Opção")) {
                case 1 -> mostrarCatalogo();
                case 2 -> adicionarProduto();
                case 3 -> mostrarCarrinho();
                case 4 -> mostrarMenuPagamento();
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void mostrarCatalogo() {
        System.out.println("\nCatálogo");
        for (Produto produto : CATALOGO) {
            System.out.printf(
                "%d. [%s] %s | %s | estoque: %d%n",
                produto.codigo(), produto.categoria(), produto.nome(), moeda.format(produto.preco()), produto.estoque()
            );
        }
    }

    private void adicionarProduto() {
        int codigo = lerInteiro("Código do produto");
        try {
            Produto produto = CATALOGO.stream()
                .filter(item -> item.codigo() == codigo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            int quantidade = lerInteiro("Quantidade");
            carrinho.adicionar(produto, quantidade);
            System.out.println("Produto adicionado.");
        } catch (IllegalArgumentException erro) {
            System.out.println(erro.getMessage());
        }
    }

    private void mostrarCarrinho() {
        System.out.println("\nCarrinho");
        if (carrinho.itens().isEmpty()) {
            System.out.println("O carrinho está vazio.");
            return;
        }

        carrinho.itens().forEach((produto, quantidade) -> {
            BigDecimal valorDoItem = produto.preco().multiply(BigDecimal.valueOf(quantidade));
            System.out.printf("%d x %s | %s%n", quantidade, produto.nome(), moeda.format(valorDoItem));
        });
        System.out.println("Subtotal: " + moeda.format(carrinho.subtotal()));
    }

    private void mostrarMenuPagamento() {
        if (carrinho.itens().isEmpty()) {
            System.out.println("Adicione um produto antes de simular.");
            return;
        }

        while (true) {
            System.out.println("\nSubtotal: " + moeda.format(carrinho.subtotal()));
            System.out.println("1. Pix à vista (5% de desconto)");
            System.out.println("2. Cartão (1x a 6x sem juros, 7x a 10x com acréscimo)");
            System.out.println("0. Voltar ao menu principal");

            switch (lerInteiro("Opção")) {
                case 1 -> mostrarSimulacao("Pix", new PagamentoPix());
                case 2 -> simularCartao();
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void simularCartao() {
        int parcelas = lerInteiro("Número de parcelas");
        try {
            mostrarSimulacao("Cartão em " + parcelas + "x", new PagamentoCartao(parcelas));
        } catch (IllegalArgumentException erro) {
            System.out.println(erro.getMessage());
        }
    }

    private void mostrarSimulacao(String formaPagamento, EstrategiaPagamento estrategia) {
        BigDecimal subtotal = carrinho.subtotal();
        SimulacaoPagamento simulacao = estrategia.simular(subtotal);
        BigDecimal diferenca = simulacao.total().subtract(subtotal);

        System.out.println("\n" + formaPagamento);
        if (diferenca.signum() < 0) {
            System.out.println("Desconto: " + moeda.format(diferenca.abs()));
        } else if (diferenca.signum() > 0) {
            System.out.println("Acréscimo: " + moeda.format(diferenca));
        }

        List<BigDecimal> parcelas = simulacao.parcelas();
        for (int indice = 0; indice < parcelas.size(); indice++) {
            System.out.printf("Parcela %d: %s%n", indice + 1, moeda.format(parcelas.get(indice)));
        }
        System.out.println("Total a pagar: " + moeda.format(simulacao.total()));
    }

    private int lerInteiro(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            try {
                return Integer.parseInt(entrada.nextLine().strip());
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }
}
