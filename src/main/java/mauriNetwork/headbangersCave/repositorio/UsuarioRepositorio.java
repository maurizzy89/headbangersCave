package mauriNetwork.headbangersCave.repositorio;

import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.enumeraciones.Rol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.nombreu = :nombreu")
    public Usuario buscarPorUsuario(@Param("nombreu") String nombreu);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    public List<Usuario> listarPeriodistas(@Param("rol") Rol rol);

    @Query("SELECT u FROM Usuario u ORDER BY u.id DESC")
    public List<Usuario> listarUsuarios();
    
}
