package mauriNetwork.mauriPrueba.servicios;

import mauriNetwork.mauriPrueba.entidades.Periodista;
import mauriNetwork.mauriPrueba.entidades.Usuario;
import mauriNetwork.mauriPrueba.enumeraciones.Rol;
import mauriNetwork.mauriPrueba.excepciones.MyException;
import mauriNetwork.mauriPrueba.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
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

    public Usuario registrar(String nombreu, String password, String password2) throws MyException {
        validar(nombreu, password, password2);

        Usuario usuario = new Usuario();
        usuario.setNombreu(nombreu);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setFechalta(new Date());

        return usuario;
    }

    @Transactional
    public void guardarUsuario(String nombreu, String password, String password2) throws MyException {
        Usuario usuario = registrar(nombreu, password, password2);
        usuario.setRol(Rol.USER);

        usuarioRepositorio.save(usuario);
    }

    private void validar(String nombreu, String password, String password2) throws MyException {
        if (nombreu.isEmpty() || nombreu == null) {
            throw new MyException("El nombre no puede estar vacio");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MyException("La contraseña no puede estar vacia y debe tener mas de 5 digitos");
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

    public List<Usuario> listarPeriodistas(Rol rol) {

        List<Usuario> listaPeriodistas = new ArrayList();

        listaPeriodistas = usuarioRepositorio.listarPeriodistas(rol);

        return listaPeriodistas;
    }
}
