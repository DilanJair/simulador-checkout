package checkout;

import java.math.BigDecimal;

interface EstrategiaPagamento {
    SimulacaoPagamento simular(BigDecimal subtotal);
}
