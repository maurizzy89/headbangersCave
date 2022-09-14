package mauriNetwork.headbangersCave.controladores;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import mauriNetwork.headbangersCave.entidades.Imagen;
import mauriNetwork.headbangersCave.entidades.Publicacion;
import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.servicios.CloudinaryService;
import mauriNetwork.headbangersCave.servicios.ImagenService;
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
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ImagenService imagenService;

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
    public String perfilUsuario(@PathVariable Long id, ModelMap modelo) {
        modelo.put("usuario", usuarioServicio.getReferenceById(id));
        List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
        modelo.addAttribute("publicaciones", publicaciones);
        return "perfil.html";
    }

    @GetMapping("/borrarImagenDePerfil/{id}")
    public String borrarImagenDePerfil(HttpSession session, @PathVariable("id") Long id, ModelMap modelo) throws IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Usuario usuario = usuarioServicio.getReferenceById(id);

        if (!imagenService.exists(usuario.getImagen().getId())) {
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
        Imagen imagen = imagenService.getOne(usuario.getImagen().getId()).get();
        Map result = cloudinaryService.delete(imagen.getImagenId());
        usuarioServicio.eliminarImagen(id);
        imagenService.delete(imagen.getId());

        modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
        List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(logueado.getId());
        modelo.addAttribute("publicaciones", publicaciones);
        return "perfil.html";
    }

    @PostMapping("/modificarImagenDePerfil/{id}")
    public String modificarImagenDePerfil(@PathVariable Long id, @RequestParam(name = "multipartFile") MultipartFile multipartFile, ModelMap modelo) throws IOException {
        try {
            BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
            if (bi == null) {
                modelo.put("error", "No cargaste ninguna imagen ");
                modelo.put("usuario", usuarioServicio.getReferenceById(id));
                List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
                modelo.addAttribute("publicaciones", publicaciones);
                return "perfil.html";
            }
            Map result = cloudinaryService.upload(multipartFile);
            Imagen imagen
                    = new Imagen(result.get("original_filename").toString(),
                            result.get("url").toString(),
                            result.get("public_id").toString());
            imagenService.save(imagen);

            usuarioServicio.modificarImagenDePerfil(id, imagen);
            modelo.put("exito", "La imagen de perfil fue cambiada");
            modelo.put("usuario", usuarioServicio.getReferenceById(id));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        } catch (IOException e) {
            modelo.put("usuario", usuarioServicio.getReferenceById(id));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
    }

    @PostMapping("/modificarNombreDeUsuario/{id}")
    public String modificarNombreDeUsuario(@PathVariable Long id, HttpSession session, @RequestParam(name = "nombreu") String nombreu, ModelMap modelo) throws IOException {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        try {
            usuarioServicio.modificarNombreDeUsuario(id, nombreu);
            logueado.setNombreu(nombreu);
            modelo.put("exito", "El nombre de usuario fue cambiado");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        } catch (Exception e) {
            modelo.put("error", "El nombre de usuario que pusiste ya esta en uso");
            modelo.put("usuario", usuarioServicio.getReferenceById(logueado.getId()));
            List<Publicacion> publicaciones = usuarioServicio.listarPublicacionesPorCreador(id);
            modelo.addAttribute("publicaciones", publicaciones);
            return "perfil.html";
        }
    }

}
