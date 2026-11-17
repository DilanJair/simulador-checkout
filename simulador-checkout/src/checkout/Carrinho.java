package checkout;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class Carrinho {
    private final Map<Produto, Integer> itens = new LinkedHashMap<>();

    void adicionar(Produto produto, int quantidade) {
        if (quantidade < 1) {
            throw new IllegalArgumentException("Informe uma quantidade maior que zero.");
        }

        int quantidadeNoCarrinho = itens.getOrDefault(produto, 0);
        int disponivel = produto.estoque() - quantidadeNoCarrinho;
        if (quantidade > disponivel) {
            throw new IllegalArgumentException("Unidades disponíveis para adicionar: " + disponivel);
        }

        itens.put(produto, quantidadeNoCarrinho + quantidade);
    }

    Map<Produto, Integer> itens() {
        return Collections.unmodifiableMap(itens);
    }

    BigDecimal subtotal() {
        BigDecimal total = new BigDecimal("0.00");
        for (var item : itens.entrySet()) {
            BigDecimal valorDoItem = item.getKey().preco().multiply(BigDecimal.valueOf(item.getValue()));
            total = total.add(valorDoItem);
        }
        return total;
    }
}
