package mauriNetwork.mauriPrueba.controladores;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import mauriNetwork.mauriPrueba.entidades.Usuario;
import mauriNetwork.mauriPrueba.excepciones.MyException;
import mauriNetwork.mauriPrueba.servicios.NoticiaServicio;
import mauriNetwork.mauriPrueba.servicios.UsuarioServicio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private NoticiaServicio noticiaServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
    }

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "registro.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam(name = "nombreu") String nombreu, @RequestParam(name = "password") String password, @RequestParam(name = "password2") String password2, ModelMap modelo) throws MyException {
        try {
            usuarioServicio.guardarUsuario(nombreu, password, password2);
            modelo.put("exito", "Fuiste registrado correctamente, ya podes loguearte");
            return "index.html";
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombreu", nombreu);
            return "registro.html";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña invalidos");
        }
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN','ROLE_PERIODISTA')") // solo pueden entrar usuarios, admin o periodista
    @GetMapping("/noticias")
    public String listar(ModelMap modelo, HttpSession session) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().toString().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        modelo.put("nombreLogueado", logueado.getNombreu());
        List<Noticia> ultimasNoticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", ultimasNoticias);
        return "noticias.html";
    }

    @GetMapping("/noticia/{id}")
    public String noticia(@PathVariable Long id, ModelMap modelo) {
        modelo.put("noticia", noticiaServicio.getReferenceById(id));
        return "noticia.html";
    }

    @GetMapping("/crear")
    public String crear(Noticia noticia) {
        return "formulario.html";
    }

    @PostMapping("/guardar")
    public String guardar(Noticia noticia, HttpSession session, @RequestParam(name = "titulo") String titulo, @RequestParam(name = "cuerpo") String cuerpo, @RequestParam(name = "file") MultipartFile file, ModelMap modelo) throws MyException, IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (!file.isEmpty()) {
            String ruta = "C:\\Users\\Maurizzy\\Desktop\\Curso Egg\\Modulo 5 -Spring\\18 - Spring - MVC\\Ejercicio\\1\\imagenes";
            try {
                byte[] bytesFoto = file.getBytes();
                Path rutaAbsoluta = Paths.get(ruta + "//" + file.getOriginalFilename());
                Files.write(rutaAbsoluta, bytesFoto);
            } catch (IOException ex) {
                modelo.put("error", ex.getMessage());
                return "formulario.html";
            }
        }
        try {
            String foto = file.getOriginalFilename();
            noticiaServicio.crearNoticia(titulo, cuerpo, foto, logueado.getNombreu());
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            return "formulario.html";
        }
        modelo.put("exito", "La noticia fue cargada correctamente");
        //vuelve a cargar la lista
        modelo.put("nombreLogueado", logueado.getNombreu());
        List<Noticia> noticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", noticias);
        return "noticias.html";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, ModelMap modelo) {
        modelo.put("noticia", noticiaServicio.getReferenceById(id));
        return "formulario.html";
    }

    @GetMapping("/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {
        modelo.put("noticia", noticiaServicio.getReferenceById(id));
        noticiaServicio.eliminarNoticia(id);
        //vuelve a cargar la lista
        List<Noticia> ultimasNoticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", ultimasNoticias);
        return "noticias.html";
    }

    @GetMapping("/perfil/{nombreu}")
    public String perfil(@PathVariable String nombreu, ModelMap modelo) throws MyException {
        try {
            modelo.put("usuario", usuarioServicio.buscarPorUsuario(nombreu));
            return "perfil.html";
        } catch (Exception e) {
            throw new MyException("Periodista sin noticias");
        }
    }

}
