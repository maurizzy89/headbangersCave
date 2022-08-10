package mauriNetwork.mauriPrueba.controladores;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import mauriNetwork.mauriPrueba.entidades.Periodista;
import mauriNetwork.mauriPrueba.entidades.Usuario;
import mauriNetwork.mauriPrueba.enumeraciones.Rol;
import mauriNetwork.mauriPrueba.excepciones.MyException;
import mauriNetwork.mauriPrueba.servicios.NoticiaServicio;
import mauriNetwork.mauriPrueba.servicios.PeriodistaServicio;
import mauriNetwork.mauriPrueba.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin")
public class AdminControlador {

    @Autowired
    private NoticiaServicio noticiaServicio;

    @Autowired
    private PeriodistaServicio periodistaServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard")
    public String panelAdministrativo(ModelMap modelo, HttpSession session) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.put("nombreLogueado", logueado.getNombreu());

        List<Noticia> noticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", noticias);
        return "noticias.html";
    }

    @GetMapping("/registrar_periodista")
    public String registrarPeriodista(Periodista periodista) {
        return "registro_periodista.html";
    }

    @PostMapping("/registro_periodista")
    public String registroPeriodista(HttpSession session, Periodista periodista, @RequestParam(name = "password2") String password2, ModelMap modelo) throws MyException {
        try {
            periodistaServicio.guardarPeriodista(periodista.getNombreu(), periodista.getPassword(), password2, periodista.getSueldoMensual());
            modelo.put("exito", "Periodista registrado correctamente");
            //vuelve a cargar la lista de noticias
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            modelo.put("nombreLogueado", logueado.getNombreu());
            List<Noticia> ultimasNoticias = noticiaServicio.listarNoticias();
            modelo.put("noticias", ultimasNoticias);
            return "noticias.html";
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombreu", periodista.getNombreu());
            modelo.put("sueldoMensual", periodista.getSueldoMensual());
            return "registro_periodista.html";
        }
    }

    @GetMapping("/lista_periodistas")
    public String listarPeriodistas(ModelMap modelo) {
        List<Usuario> periodistas = usuarioServicio.listarPeriodistas(Rol.PERIODISTA);
        modelo.addAttribute("periodistas", periodistas);
        return "lista_periodistas.html";
    }

    @GetMapping("/periodista/{nombreu}")
    public String darDeBaja(HttpSession session, @PathVariable String nombreu, ModelMap modelo) {
        periodistaServicio.darDeBajaPeriodista(nombreu);

        //vuelve a cargar la lista
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.put("nombreu", logueado.getNombreu());
        List<Noticia> ultimasNoticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", ultimasNoticias);
        return "noticias.html";
    }

}
