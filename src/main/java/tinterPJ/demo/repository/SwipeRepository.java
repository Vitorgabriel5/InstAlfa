package tinterPJ.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.Swipe;
import tinterPJ.demo.model.SwipeType;
import tinterPJ.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe,Long> {

    Optional<Swipe> findByUsuarioOrigemAndUsuarioDestino(User origem, User destino);
    List<Swipe> findByUsuarioOrigemAndTipo (User origem, SwipeType tipo);
    List<Swipe> findByUsuarioDestinoAndTipo (User destino, SwipeType tipo);

    boolean existsByUsuarioOrigemAndUsuarioDestino (User origem, User destino);
}
