package mauriNetwork.headbangersCave.repositorio;

import java.util.List;
import mauriNetwork.headbangersCave.entidades.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenRepositorio extends JpaRepository<Imagen, Integer>{
   List<Imagen> findByOrderById();  
}
