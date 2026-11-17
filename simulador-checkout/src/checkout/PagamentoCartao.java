package checkout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

final class PagamentoCartao implements EstrategiaPagamento {
    private static final BigDecimal CENTAVO = new BigDecimal("0.01");
    private final int quantidadeParcelas;
    private final BigDecimal taxaAcrescimo;

    PagamentoCartao(int quantidadeParcelas) {
        if (quantidadeParcelas < 1 || quantidadeParcelas > 10) {
            throw new IllegalArgumentException("Escolha de 1 a 10 parcelas.");
        }

        this.quantidadeParcelas = quantidadeParcelas;
        int percentual = switch (quantidadeParcelas) {
            case 7 -> 4;
            case 8 -> 6;
            case 9 -> 8;
            case 10 -> 10;
            default -> 0;
        };
        taxaAcrescimo = BigDecimal.valueOf(percentual, 2);
    }

    @Override
    public SimulacaoPagamento simular(BigDecimal subtotal) {
        BigDecimal acrescimo = subtotal.multiply(taxaAcrescimo).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(acrescimo);
        BigDecimal divisor = BigDecimal.valueOf(quantidadeParcelas);
        BigDecimal valorBase = total.divide(divisor, 2, RoundingMode.DOWN);
        int centavosRestantes = total.subtract(valorBase.multiply(divisor)).movePointRight(2).intValueExact();

        List<BigDecimal> parcelas = new ArrayList<>();
        for (int indice = 0; indice < quantidadeParcelas; indice++) {
            BigDecimal valor = indice < centavosRestantes ? valorBase.add(CENTAVO) : valorBase;
            parcelas.add(valor);
        }

        return new SimulacaoPagamento(total, parcelas);
    }
}
