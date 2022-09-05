package mauriNetwork.headbangersCave.servicios;

import java.util.Optional;
import javax.transaction.Transactional;
import mauriNetwork.headbangersCave.entidades.Imagen;
import mauriNetwork.headbangersCave.repositorio.ImagenRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ImagenService {

    @Autowired
    ImagenRepositorio imagenRepositorio;

    public void save(Imagen imagen) {
        imagenRepositorio.save(imagen);
    }

    public void delete(int id) {
        imagenRepositorio.deleteById(id);
    }

    public Optional<Imagen> getOne(int id) {
        return imagenRepositorio.findById(id);
    }

    public boolean exists(int id) {
        return imagenRepositorio.existsById(id);
    }

}
