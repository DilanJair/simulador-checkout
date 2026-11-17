package checkout;

import java.math.BigDecimal;
import java.util.List;

public final class CheckoutTest {
    private static final BigDecimal SUBTOTAL = new BigDecimal("600.00");

    public static void main(String[] args) {
        verificarCarrinho();
        verificarPix();
        verificarCartao();
        System.out.println("Verificações de carrinho, Pix e cartão passaram.");
    }

    private static void verificarCarrinho() {
        Produto fone = new Produto(1, "Fone", "Tecnologia", new BigDecimal("400.00"), 2);
        Produto luminaria = new Produto(2, "Luminária", "Casa", new BigDecimal("100.00"), 3);
        Carrinho carrinho = new Carrinho();
        conferir(new BigDecimal("0.00"), carrinho.subtotal());
        carrinho.adicionar(fone, 1);
        carrinho.adicionar(luminaria, 1);
        carrinho.adicionar(luminaria, 1);
        rejeitar(() -> carrinho.adicionar(luminaria, 2));
        rejeitar(() -> carrinho.adicionar(fone, 0));
        conferir(2, carrinho.itens().get(luminaria));
        conferir(SUBTOTAL, carrinho.subtotal());
    }

    private static void verificarPix() {
        conferir(new BigDecimal("570.00"), new PagamentoPix().simular(SUBTOTAL).total());
    }

    private static void verificarCartao() {
        conferir(new BigDecimal("600.00"), new PagamentoCartao(1).simular(SUBTOTAL).total());
        conferir(new BigDecimal("600.00"), new PagamentoCartao(6).simular(SUBTOTAL).total());
        conferir(new BigDecimal("636.00"), new PagamentoCartao(8).simular(SUBTOTAL).total());
        conferir(new BigDecimal("648.00"), new PagamentoCartao(9).simular(SUBTOTAL).total());
        conferir(new BigDecimal("660.00"), new PagamentoCartao(10).simular(SUBTOTAL).total());
        rejeitar(() -> new PagamentoCartao(0));
        rejeitar(() -> new PagamentoCartao(11));

        SimulacaoPagamento seteVezes = new PagamentoCartao(7).simular(SUBTOTAL);
        BigDecimal comCentavoExtra = new BigDecimal("89.15");
        BigDecimal valorBase = new BigDecimal("89.14");
        conferir(new BigDecimal("624.00"), seteVezes.total());
        conferir(List.of(comCentavoExtra, comCentavoExtra, valorBase, valorBase, valorBase, valorBase, valorBase),
            seteVezes.parcelas());
        conferir(seteVezes.total(), seteVezes.parcelas().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private static void conferir(Object esperado, Object obtido) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError("Esperado: " + esperado + ". Obtido: " + obtido);
        }
    }

    private static void rejeitar(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException esperado) {
            return;
        }
        throw new AssertionError("A operação deveria ter sido rejeitada.");
    }
}
