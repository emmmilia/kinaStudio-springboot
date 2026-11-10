package com.kinastudio.webapp.dominio;

import java.math.BigDecimal;
import java.util.List;

public class Caja {
    private BigDecimal total;
    private List<Pago> pagos;

    public Caja(BigDecimal total, List<Pago> pagos){
        this.total = total;
        this.pagos = pagos;
    }
}
