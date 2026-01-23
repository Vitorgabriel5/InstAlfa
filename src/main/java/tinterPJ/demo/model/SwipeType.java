package tinterPJ.demo.model;

public enum SwipeType {
    LIKE("Like"),
    DISLIKE("Dislike"),
    SUPER_LIKE("Super Like");

    private final String descricao;

    SwipeType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
