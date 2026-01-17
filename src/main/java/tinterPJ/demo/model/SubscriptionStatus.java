package tinterPJ.demo.model;

public enum SubscriptionStatus {
    TRIAL("Trial - Período de teste"),
    ATIVA("Ativa"),
    EXPIRADA("Expirada"),
    CANCELADA("Cancelada"),
    SUSPENSA("Suspensa"),
    PENDENTE_PAGAMENTO("Pendente de pagamento");

    private final String descricao;

    SubscriptionStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}