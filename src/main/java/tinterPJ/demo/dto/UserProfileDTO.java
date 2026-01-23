package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinterPJ.demo.model.Gender;
import tinterPJ.demo.model.UserProfile;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String bio;
    private LocalDate dataNascimento;
    private Integer idade;
    private Gender genero;
    private Gender interesseEm;
    private String cidade;
    private String estado;
    private String pais;
    private Double latitude;
    private Double longitude;
    private Integer raioBusca;
    private Integer idadeMinima;
    private Integer IdadeMaxima;
    private List<String> fotos;
    private List<String> interesses;
    private String profissao;
    private String empresa;
    private String Escola;
    private Boolean perfilAtivo;
    private Boolean verificado;

    public static UserProfileDTO fromEntity(UserProfile profile) {
        return UserProfileDTO.builder()
                .id(profile.getId())
                .bio(profile.getBio())
                .dataNascimento(profile.getDataNascimento())
                .idade(profile.getIdade())
                .genero(profile.getGenero())
                .interesseEm(profile.getInteresseEm())
                .cidade(profile.getCidade())
                .estado(profile.getEstado())
                .pais(profile.getPais())
                .latitude(profile.getLatitude())
                .longitude(profile.getLongitude())
                .raioBusca(profile.getRaioBusca())
                .idadeMinima(profile.getIdadeMinima())
                .IdadeMaxima(profile.getIdadeMaxima())
                .fotos(profile.getFoto())
                .interesses(profile.getInteresses())
                .profissao(profile.getProfissao())
                .empresa(profile.getEmpresa())
                .Escola(profile.getEscola())
                .perfilAtivo(profile.getPerfilAtivo())
                .verificado(profile.getVerificado())
                .build();
    }
}
