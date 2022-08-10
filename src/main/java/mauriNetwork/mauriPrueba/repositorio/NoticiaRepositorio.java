package mauriNetwork.mauriPrueba.repositorio;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticiaRepositorio extends JpaRepository<Noticia, Long> {
 
        @Query ("SELECT n FROM Noticia n ORDER BY n.id DESC")
    public List<Noticia> listarNoticias();
}
