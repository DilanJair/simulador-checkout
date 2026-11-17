package checkout;

import java.math.BigDecimal;

record Produto(int codigo, String nome, String categoria, BigDecimal preco, int estoque) {}
