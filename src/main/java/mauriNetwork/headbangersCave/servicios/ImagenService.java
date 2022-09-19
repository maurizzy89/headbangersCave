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

    public String modificarURL(String urlImagen) {
        String[] urlDividida = urlImagen.split("/");
        String urlNueva = "";

        for (int i = 0; i < urlDividida.length; i++) {
            if (i == 5) {
                urlDividida[i] = urlDividida[i] + "/c_fill,f_auto,h_500,q_100:444,w_500";
            }
            if (i != urlDividida.length - 1) {
                urlNueva = urlNueva + urlDividida[i] + "/";
            } else {
                urlNueva = urlNueva + urlDividida[i];
            }
        }
        return urlNueva;
    }
}
