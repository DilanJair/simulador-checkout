package checkout;

import java.math.BigDecimal;
import java.util.List;

record SimulacaoPagamento(BigDecimal total, List<BigDecimal> parcelas) {
    SimulacaoPagamento {
        parcelas = List.copyOf(parcelas);
    }
}
