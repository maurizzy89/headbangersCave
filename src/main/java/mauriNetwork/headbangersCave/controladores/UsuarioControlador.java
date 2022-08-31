package mauriNetwork.headbangersCave.controladores;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpSession;
import mauriNetwork.headbangersCave.entidades.Publicacion;
import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.servicios.PublicacionServicio;
import mauriNetwork.headbangersCave.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("usuario")
public class UsuarioControlador {

    @Autowired
    private PublicacionServicio publicacionServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public String panelLogueado(ModelMap modelo, HttpSession session) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().toString().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        modelo.put("id", logueado.getId());
        List<Publicacion> publicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/perfil/{id}")
    public String perfilUsuario(HttpSession session, @PathVariable Long id, ModelMap modelo) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado != null) {
            modelo.put("logueadoNombreu", logueado.getNombreu());
            modelo.put("usuario", usuarioServicio.getReferenceById(id));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        } else {
            modelo.put("usuario", usuarioServicio.getReferenceById(id));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
    }

    @PostMapping("/modificarImagenDePerfil")
    public String modificarImagenDePerfil(HttpSession session, @RequestParam(name = "file") MultipartFile file, ModelMap modelo) throws IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            String ruta = "./src/main/resources/static/perfiles";
            byte[] bytesFoto = file.getBytes();
            Path rutaAbsoluta = Paths.get(ruta + "/" + file.getOriginalFilename());
            Files.write(rutaAbsoluta, bytesFoto);

            String fotoNombre = file.getOriginalFilename();
            usuarioServicio.modificarImagenDePerfil(logueado.getId(), fotoNombre);
            modelo.put("exitoImagen", "La imagen de perfil fue cambiada");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        } catch (Exception e) {
            modelo.put("errorImagen", "No cargaste ninguna imagen ");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
    }

    @PostMapping("/modificarNombreDeUsuario")
    public String modificarNombreDeUsuario(HttpSession session,
            @RequestParam(name = "nombreu") String nombreu, ModelMap modelo) throws IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        try {
            usuarioServicio.modificarNombreDeUsuario(logueado.getId(), nombreu);
            logueado.setNombreu(nombreu);
            modelo.put("exitoNombre", "El nombre de usuario fue cambiado");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        } catch (Exception e) {
            modelo.put("errorNombre", "El nombre de usuario que pusiste ya esta en uso");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
    }

}
