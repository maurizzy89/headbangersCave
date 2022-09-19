package mauriNetwork.headbangersCave.servicios;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import mauriNetwork.headbangersCave.entidades.Publicacion;
import mauriNetwork.headbangersCave.excepciones.MyException;
import mauriNetwork.headbangersCave.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import mauriNetwork.headbangersCave.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mauriNetwork.headbangersCave.repositorio.PublicacionRepositorio;

@Service
public class PublicacionServicio {

    @Autowired
    private PublicacionRepositorio publicacionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional    //se pone cuando el metodo hace alguna transaccion en la BD si el metodo se ejecuta bien, se realiza un commit a la BD y se aplican los cambios, si da algun error se hace un rollback (se vuelve atras) y no se aplica nada
    public Publicacion crearPublicacion(String titulo, String comentario, String link, String nombreLogueado) throws MyException, URISyntaxException, MalformedURLException {
        validar(titulo, comentario, link);

        Publicacion publicacion = new Publicacion();

        publicacion.setTitulo(titulo);
        publicacion.setComentario(comentario);
        publicacion.setLink(link);
        publicacion.setCreador((Usuario) usuarioRepositorio.buscarPorUsuario(nombreLogueado));
        publicacion.setFecha(new Date());

        publicacionRepositorio.save(publicacion);
        guardarPublicacionEnMiLista(publicacion, nombreLogueado);

        return publicacion;
    }

    @Transactional
    public void guardarPublicacionEnMiLista(Publicacion publicacion, String nombreLogueado) {
        Usuario logueado = usuarioRepositorio.buscarPorUsuario(nombreLogueado);
        List<Publicacion> publicaciones = new ArrayList();
        if (logueado.getPublicacionesUsuario().isEmpty()) {
            publicaciones.add(publicacion);
            logueado.setPublicacionesUsuario(publicaciones);
        } else {
            publicaciones.addAll(logueado.getPublicacionesUsuario());
            publicaciones.add(publicacion);
            logueado.setPublicacionesUsuario(publicaciones);
        }
    }

    public void borrarPublicacionDeMiLista(Publicacion publicacion) {
        Usuario logueado = usuarioRepositorio.buscarPorUsuario(publicacion.getCreador().getNombreu());
        List<Publicacion> publicaciones = new ArrayList();
        if (!logueado.getPublicacionesUsuario().isEmpty()) {
            publicaciones.addAll(logueado.getPublicacionesUsuario());
            publicaciones.remove(publicacion);
            logueado.setPublicacionesUsuario(publicaciones);
        }
    }

    public void editarPublicacion(Long id, String titulo, String comentario, String link) throws MyException, MalformedURLException, URISyntaxException {
        validar(titulo, comentario, link);

        Optional<Publicacion> respuestaPublicacion = publicacionRepositorio.findById(id);

        if (respuestaPublicacion.isPresent()) {
            Publicacion publicacion = respuestaPublicacion.get();

            publicacion.setTitulo(titulo);
            publicacion.setComentario(comentario);
            publicacion.setLink(link);

            publicacionRepositorio.save(publicacion);
        }
    }

    @Transactional
    public void eliminarPublicacion(Long id) {
        Optional<Publicacion> respuestaPublicacion = publicacionRepositorio.findById(id);

        if (respuestaPublicacion.isPresent()) {
            Publicacion noticia = respuestaPublicacion.get();
            borrarPublicacionDeMiLista(noticia);
            publicacionRepositorio.delete(noticia);
        }
    }

    public List<Publicacion> listarPublicaciones() {
        List<Publicacion> ultimasPublicaciones = new ArrayList();

        ultimasPublicaciones = publicacionRepositorio.listarPublicaciones();

        return ultimasPublicaciones;
    }

    public void validar(String titulo, String comentario, String link) throws MyException, MalformedURLException, URISyntaxException {
        if (titulo == null || titulo.isEmpty()) {
            throw new MyException("El titulo no puede estar vacio");
        }

        if (link == null || link.isEmpty()) {
            throw new MyException("Tenes que poner un link");
        } else {
            try {
                new URL(link).toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                throw new MyException("Link no vàlido");
            }
        }

    }

    public Publicacion getReferenceById(Long id) {
        return publicacionRepositorio.getReferenceById(id);
    }

    public List<Publicacion> listarPublicacionesPorCreador(Long id) {
        List<Publicacion> publicacionesCreador = new ArrayList();
        publicacionesCreador = publicacionRepositorio.listarPublicacionesPorCreador(id);

        return publicacionesCreador;
    }
}
