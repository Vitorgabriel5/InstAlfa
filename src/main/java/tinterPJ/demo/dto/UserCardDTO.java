package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardDTO {

    private Long usuarioId;
    private String nome;
    private Integer idade;
    private String bio;
    private String cidade;
    private List<String> fotos;
    private List<String> interesses;
    private String profissao;
    private Double distanciaKm;
    private Boolean verificado;
}
