package tinterPJ.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private User remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private User destinatario;

    @Column(length = 5000, nullable = false)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensagem")
    private MessageType tipoMensagem = MessageType.TEXT;

    @Column(name = "url_midia")
    private String urlMidia;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    @Column(name = "lida")
    private Boolean lida = false;

    @Column(name = "deletada")
    private Boolean deletada = false;

    @PrePersist
    public void onCreate() {
        dataEnvio = LocalDateTime.now();
    }
}
