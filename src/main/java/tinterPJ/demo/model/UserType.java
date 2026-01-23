package tinterPJ.demo.model;

public enum UserType {
    PESSOA_FISICA("Pessoa Fisica", "PF"),
    PESSOA_JURIDICA("Pessoa Juridica", "PJ");

    private final String descricao;
    private final String sigla;

    UserType(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getSigla() {
        return sigla;
    }
}
