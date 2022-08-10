package mauriNetwork.mauriPrueba.servicios;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import mauriNetwork.mauriPrueba.entidades.Periodista;
import mauriNetwork.mauriPrueba.enumeraciones.Rol;
import mauriNetwork.mauriPrueba.excepciones.MyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PeriodistaServicio extends UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioServicio usuarioServicio;

    public Periodista registrar(String nombreu, String password, String password2, Integer sueldoMensual) throws MyException {
        validar(nombreu, password, password2, sueldoMensual);

        List<Noticia> noticias = new ArrayList();
        boolean alta = true;
        Periodista periodista = new Periodista();
        periodista.setNombreu(nombreu);
        periodista.setPassword(new BCryptPasswordEncoder().encode(password));
        periodista.setSueldoMensual(sueldoMensual);
        periodista.setAlta(alta);
        periodista.setFechalta(new Date());
        periodista.setNoticiasPeriodista(noticias);

        return periodista;
    }

    @Transactional
    public void guardarPeriodista(String nombreu, String password, String password2, Integer sueldoMensual) throws MyException {
        Periodista periodista = registrar(nombreu, password, password2, sueldoMensual);
        periodista.setRol(Rol.PERIODISTA);

        usuarioRepositorio.save(periodista);
    }

    public void validar(String nombreu, String password, String password2, Integer sueldoMensual) throws MyException {
        if (nombreu.isEmpty()) {
            throw new MyException("Debe indicar un nombre de usuario");
        }
        if (password.isEmpty() || password.length() <= 5) {
            throw new MyException("Debe indicar la contraseña y debe tener mas de 5 digitos");
        }
        if (!password2.equalsIgnoreCase(password)) {
            throw new MyException("Debe repetir la contraseña y ambas deben coincidir");
        }
        if (sueldoMensual == null) {
            throw new MyException("Debe indicar un sueldo mensual");
        }

    }

    @Transactional
    public void darDeBajaPeriodista(String nombreu) {
        Periodista periodista = (Periodista) usuarioServicio.buscarPorUsuario(nombreu);
        System.out.println(periodista);
        
        periodista.setAlta(false);

        usuarioRepositorio.save(periodista);
    }

    
        @Transactional
    public void darDeAltaPeriodista(String nombreu) {
        Periodista periodista = (Periodista) usuarioServicio.buscarPorUsuario(nombreu);
        usuarioRepositorio.delete(periodista);
    }

}
