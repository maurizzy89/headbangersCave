package mauriNetwork.headbangersCave.servicios;

import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.enumeraciones.Rol;
import mauriNetwork.headbangersCave.excepciones.MyException;
import mauriNetwork.headbangersCave.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import mauriNetwork.headbangersCave.entidades.Imagen;
import mauriNetwork.headbangersCave.entidades.Publicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    public UsuarioRepositorio usuarioRepositorio;
    @Autowired
    public PublicacionServicio publicacionServicio;

    public Usuario registrar(String nombreu, String password, String password2, Imagen imagen) throws MyException {
        validar(nombreu, password, password2);

        Usuario usuario = new Usuario();
        usuario.setNombreu(nombreu);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setFechalta(new Date());
        usuario.setImagen(imagen);

        return usuario;
    }

    @Transactional
    public void guardarUsuario(String nombreu, String password, String password2, Imagen imagen) throws MyException {
        Usuario usuario = registrar(nombreu, password, password2, imagen);
        usuario.setRol(Rol.USER);

        usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void modificarImagenDePerfil(Long id, Imagen imagen) {
        Optional<Usuario> respuestaUsuario = usuarioRepositorio.findById(id);

        if (respuestaUsuario.isPresent()) {
            Usuario usuario = respuestaUsuario.get();

            usuario.setImagen(imagen);

            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void modificarNombreDeUsuario(Long id, String nombreu) {
        Optional<Usuario> respuestaUsuario = usuarioRepositorio.findById(id);

        if (respuestaUsuario.isPresent()) {
            Usuario usuario = respuestaUsuario.get();

            usuario.setNombreu(nombreu);

            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void eliminarImagen(Long idUsuario) {
        Optional<Usuario> respuestaUsuario = usuarioRepositorio.findById(idUsuario);

        if (respuestaUsuario.isPresent()) {
            Usuario usuario = respuestaUsuario.get();

            usuario.setImagen(null);

            usuarioRepositorio.save(usuario);
        }
    }

    private void validar(String nombreu, String password, String password2) throws MyException {
        boolean nombreValidacion = Pattern.matches("(\\s+)", nombreu);

        if (nombreu.isEmpty()) {
            throw new MyException("El nombre no puede estar vacio");
        }
        if (nombreValidacion == true) {
            throw new MyException("El nombre no puede tener solo espacios");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MyException("La contraseña no puede estar vacia y debe tener al menos 6 digitos");
        }
        if (password.isEmpty() || !password2.equals(password)) {
            throw new MyException("Debe repetir la contraseña y ambas deben coincidir");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String nombreu) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorUsuario(nombreu);
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getNombreu(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
    }
    // este metodo sirve para que cuando un usuario inicie sesion, spring le otorgue los permisos a los que tiene acceso 

    public Usuario buscarPorUsuario(String nombreu) {
        return usuarioRepositorio.buscarPorUsuario(nombreu);
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuariosRegistrados = new ArrayList();

        usuariosRegistrados = usuarioRepositorio.listarUsuarios();

        return usuariosRegistrados;
    }

    public Usuario getReferenceById(Long id) {
        return usuarioRepositorio.getReferenceById(id);
    }

    public List<Publicacion> listarPublicacionesPorCreador(Long id) {
        List<Publicacion> publicacionesCreador = publicacionServicio.listarPublicacionesPorCreador(id);

        return publicacionesCreador;
    }

    public void eliminarUsuario(Long id) {
        usuarioRepositorio.deleteById(id);
    }
}
