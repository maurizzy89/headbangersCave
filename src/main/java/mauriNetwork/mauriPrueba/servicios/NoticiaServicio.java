package mauriNetwork.mauriPrueba.servicios;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import mauriNetwork.mauriPrueba.entidades.Periodista;
import mauriNetwork.mauriPrueba.excepciones.MyException;
import mauriNetwork.mauriPrueba.repositorio.NoticiaRepositorio;
import mauriNetwork.mauriPrueba.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticiaServicio {

    @Autowired
    private NoticiaRepositorio noticiaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional    //se pone cuando el metodo hace alguna transaccion en la BD si el metodo se ejecuta bien, se realiza un commit a la BD y se aplican los cambios, si da algun error se hace un rollback (se vuelve atras) y no se aplica nada
    public Noticia crearNoticia(String titulo, String cuerpo, String fotoNombre, String nombreLogueado) throws MyException {
        validar(titulo, cuerpo, fotoNombre);

        Noticia noticia = new Noticia();

        noticia.setTitulo(titulo);
        noticia.setCuerpo(cuerpo);
        noticia.setFoto(fotoNombre);
        if (nombreLogueado.equalsIgnoreCase("admin")) {
            noticia.setCreador((Periodista) usuarioRepositorio.buscarPorUsuario("adminEgg"));
            guardarNoticiaEnMiLista(noticia, "adminEgg");
        } else {
            noticia.setCreador((Periodista) usuarioRepositorio.buscarPorUsuario(nombreLogueado));
            guardarNoticiaEnMiLista(noticia, nombreLogueado);
        }

        noticiaRepositorio.save(noticia);
        return noticia;
    }

    @Transactional
    public void guardarNoticiaEnMiLista(Noticia noticia, String nombreLogueado) {
        Periodista logueado = (Periodista) usuarioRepositorio.buscarPorUsuario(nombreLogueado);
        List<Noticia> noticias = new ArrayList();
        if (logueado.getNoticiasPeriodista().isEmpty()) {
            noticias.add(noticia);
            logueado.setNoticiasPeriodista(noticias);
        } else {
            noticias.addAll(logueado.getNoticiasPeriodista());
            noticias.add(noticia);
            logueado.setNoticiasPeriodista(noticias);
        }
    }

    public void borrarNoticiaEnMiLista(Noticia noticia) {
        Periodista logueado = (Periodista) usuarioRepositorio.buscarPorUsuario(noticia.getCreador().getNombreu());
        List<Noticia> noticias = new ArrayList();
        if (!logueado.getNoticiasPeriodista().isEmpty()) {
            noticias.addAll(logueado.getNoticiasPeriodista());
            noticias.remove(noticia);
            logueado.setNoticiasPeriodista(noticias);
        }
    }

    public void modificarNoticia(Long id, String titulo, String cuerpo, String fotoNombre) throws MyException {
        validar(titulo, cuerpo, fotoNombre);

        Optional<Noticia> respuestaNoticia = noticiaRepositorio.findById(id);

        if (respuestaNoticia.isPresent()) {
            Noticia noticia = respuestaNoticia.get();

            noticia.setTitulo(titulo);
            noticia.setCuerpo(cuerpo);
            noticia.setFoto(fotoNombre);

            noticiaRepositorio.save(noticia);
        }
    }

    @Transactional
    public void eliminarNoticia(Long id) {
        Optional<Noticia> respuestaNoticia = noticiaRepositorio.findById(id);

        if (respuestaNoticia.isPresent()) {
            Noticia noticia = respuestaNoticia.get();
            borrarNoticiaEnMiLista(noticia);
            noticiaRepositorio.delete(noticia);
        }
    }

    public List<Noticia> listarNoticias() {
        List<Noticia> ultimasNoticias = new ArrayList();

        ultimasNoticias = noticiaRepositorio.listarNoticias();

        return ultimasNoticias;
    }

    public void validar(String titulo, String cuerpo, String fotoNombre) throws MyException {
        if (titulo == null || titulo.isEmpty()) {
            throw new MyException("El titulo no puede estar vacio");
        }
        if (cuerpo == null || cuerpo.isEmpty()) {
            throw new MyException("El cuerpo no puede estar vacio");
        }
        if (fotoNombre == null || fotoNombre.isEmpty()) {
            throw new MyException("La foto no puede estar vacia");
        }
    }

    public Noticia getReferenceById(Long id) {
        return noticiaRepositorio.getOne(id);
    }
}
