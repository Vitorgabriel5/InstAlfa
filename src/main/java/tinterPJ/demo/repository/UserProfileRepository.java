package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.User;
import tinterPJ.demo.model.UserProfile;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {

    Optional<UserProfile> findByUsuario(User usuario);
    Optional<UserProfile> findByUsuarioId(Long UsuarioId);

    @Query(value = """
        SELECT p.* FROM perfis_usuario p
        WHERE p.visivel_busca = true
        AND p.perfil_ativo = true 
        AND p.usuario_id != :usuarioId
        AND p.usuario_id NOT IN (
            SELECT usuario_destino_id FROM swipes WHERE usuario_origem_id = :usuarioId
            )
        AND (6371 * acos(cos (radians(:lat))*cos(radians(p.latitude))
            * cos(radians(p.longitude) - radians(:lon))
            + sin(raians(:lat)) * sin(raians(p.latitude)))) <= :raio
        ORDER BY RAND() 
        LIMIT :limite
        """,nativeQuery = true)
    List<UserProfile> findPerfilsProximos(
            @Param("usuarioId") Long usuarioId,
            @Param("lat") Double latitude,
            @Param("lon") Double Longitude,
            @Param("raio") Integer raioKm,
            @Param("limite") Integer limite);
}