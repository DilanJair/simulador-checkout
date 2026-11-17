package checkout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

final class PagamentoPix implements EstrategiaPagamento {
    private static final BigDecimal TAXA_DESCONTO = new BigDecimal("0.05");

    @Override
    public SimulacaoPagamento simular(BigDecimal subtotal) {
        BigDecimal desconto = subtotal.multiply(TAXA_DESCONTO).setScale(2, RoundingMode.HALF_UP);
        return new SimulacaoPagamento(subtotal.subtract(desconto), List.of());
    }
}
