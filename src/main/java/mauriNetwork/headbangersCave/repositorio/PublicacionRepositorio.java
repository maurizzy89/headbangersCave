package mauriNetwork.headbangersCave.repositorio;

import mauriNetwork.headbangersCave.entidades.Publicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepositorio extends JpaRepository<Publicacion, Long> {

    @Query("SELECT p FROM Publicacion p ORDER BY p.id DESC")
    public List<Publicacion> listarPublicaciones();

    @Query("SELECT p FROM Publicacion p WHERE p.creador.id = :id")
    public List<Publicacion> listarPublicacionesPorCreador(@Param("id") Long id);
}
