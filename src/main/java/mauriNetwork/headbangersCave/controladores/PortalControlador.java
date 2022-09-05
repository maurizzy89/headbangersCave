package mauriNetwork.headbangersCave.controladores;

import java.awt.image.BufferedImage;
import mauriNetwork.headbangersCave.entidades.Publicacion;
import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.excepciones.MyException;
import mauriNetwork.headbangersCave.servicios.PublicacionServicio;
import mauriNetwork.headbangersCave.servicios.UsuarioServicio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import mauriNetwork.headbangersCave.entidades.Imagen;
import mauriNetwork.headbangersCave.servicios.CloudinaryService;
import mauriNetwork.headbangersCave.servicios.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private PublicacionServicio publicacionServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImagenService imagenService;

    @PostMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable("id") int id) throws IOException {
        if (!imagenService.exists(id)) {
            return new ResponseEntity("no existe", HttpStatus.NOT_FOUND);
        }
        Imagen imagen = imagenService.getOne(id).get();
        Map result = cloudinaryService.delete(imagen.getImagenId());
        imagenService.delete(id);
        return new ResponseEntity("imagen eliminada", HttpStatus.OK);
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
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
    public String registro(@RequestParam(name = "multipartFile") MultipartFile multipartFile, @RequestParam(name = "nombreu") String nombreu, @RequestParam(name = "password") String password, @RequestParam(name = "password2") String password2, ModelMap modelo) throws MyException, IOException {
        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        if (bi == null) {
            return "registro.html";
        }
        Map result = cloudinaryService.upload(multipartFile);
        Imagen imagen
                = new Imagen(result.get("original_filename").toString(),
                             result.get("url").toString(),
                             result.get("public_id").toString());
        imagenService.save(imagen);

        usuarioServicio.guardarUsuario(nombreu, password, password2, imagen);
        modelo.put("exito", "Fuiste registrado correctamente, ya podes loguearte");
        List<Publicacion> ultimasPublicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", ultimasPublicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña invalidos");
        }
        return "login.html";
    }

    @GetMapping("/publicaciones")
    public String listar(HttpSession session, ModelMap modelo) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("logueado", logueado);
        List<Publicacion> ultimasPublicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", ultimasPublicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/crear")
    public String crear(Publicacion publicacion) {
        return "crear_publicacion.html";
    }

    @PostMapping("/guardar_creada")
    public String guardarCreada(Publicacion publicacion, HttpSession session, @RequestParam(name = "titulo") String titulo, @RequestParam(name = "comentario") String comentario, @RequestParam(name = "link") String link, ModelMap modelo) throws MyException, IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            publicacionServicio.crearPublicacion(titulo, comentario, link, logueado.getNombreu());
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            return "crear_publicacion.html";
        }
        modelo.put("exito", "Tu publicación fue cargada correctamente");
        //vuelve a cargar la lista
        modelo.put("id", logueado.getId());
        List<Publicacion> publicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones.html";
    }

    @PostMapping("/guardar_editada")
    public String guardarEditada(Publicacion publicacion, HttpSession session, @RequestParam(name = "titulo") String titulo, @RequestParam(name = "comentario") String comentario, @RequestParam(name = "link") String link, ModelMap modelo) throws MyException, IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            publicacionServicio.editarPublicacion(publicacion.getId(), titulo, comentario, link);
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            return "editar_publicacion.html";
        }
        modelo.put("exito", "La publicación fue editada correctamente");
        //vuelve a cargar la lista
        modelo.put("id", logueado.getId());
        List<Publicacion> publicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, ModelMap modelo) {
        modelo.put("publicacion", publicacionServicio.getReferenceById(id));
        return "editar_publicacion.html";
    }

    @GetMapping("/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, ModelMap modelo) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("publicacion", publicacionServicio.getReferenceById(id));
        publicacionServicio.eliminarPublicacion(id);
        //vuelve a cargar la lista
        modelo.put("id", logueado.getId());
        List<Publicacion> ultimasPublicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", ultimasPublicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/lista_usuarios")
    public String listarUsuarios(ModelMap modelo) {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);
        return "lista_usuarios.html";
    }

    @GetMapping("/publicaciones/{id}")
    public String publicacionesCreador(@PathVariable Long id, ModelMap modelo) {
        List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones_creador.html";
    }
}
