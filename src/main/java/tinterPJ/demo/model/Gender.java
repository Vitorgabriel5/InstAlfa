package tinterPJ.demo.model;

public enum Gender {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    NAO_BINARIO("Nao-binario"),
    TODOS("Todos");

    private final String descricao;

    Gender(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
