package tinterPJ.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfis_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false ,unique = true)
    private User usuario;

    @Column(length = 500)
    private String bio;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Gender genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "interesse_em")
    private Gender interesseEm;

    @Column(length = 100)
    private String cidade;

    @Column(length = 100)
    private String estado;

    @Column(length = 50)
    private String pais;

    //Coordenadas para Geolocalizacao
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    //Raio de Busca em Km

    @Column(name = "raio_busca")
    private Integer raioBusca = 50; // Padrao 50km

    @Column(name = "idade_minima")
    private Integer idadeMinima = 18;

    @Column(name = "idade_maxima")
    private Integer idadeMaxima = 99;

    @ElementCollection
    @CollectionTable(name = "foto_perfil", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "url_foto")
    private List<String> foto = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "interesses", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "url_foto")
    private List<String> interesses = new ArrayList<>();

    @Column(length = 100)
    private String profissao;

    @Column(length = 100)
    private String empresa;

    @Column(length = 100)
    private String escola;

    @Column(name = "perfil_ativo")
    private Boolean perfilAtivo = true;

    @Column(name = "visivel_busca")
    private Boolean visivelNaBusca = true;

    @Column(name = "verificado")
    private Boolean verificado = true;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @Column(name = "ultima_localizacao")
    private LocalDateTime ultimaLocalizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        ultimaAtualizacao = LocalDateTime.now();
        ultimaLocalizacao = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        ultimaAtualizacao = LocalDateTime.now();
    }

    //Calcular idade
    public Integer getIdade(){
        if (dataNascimento == null) return null;

        return LocalDate.now().getYear() - dataNascimento.getYear();
    }
}
