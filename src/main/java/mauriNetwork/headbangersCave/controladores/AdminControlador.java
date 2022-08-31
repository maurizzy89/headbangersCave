package mauriNetwork.headbangersCave.controladores;

import mauriNetwork.headbangersCave.entidades.Publicacion;
import mauriNetwork.headbangersCave.entidades.Usuario;
import mauriNetwork.headbangersCave.servicios.PublicacionServicio;
import mauriNetwork.headbangersCave.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminControlador {

    @Autowired
    private PublicacionServicio publicacionServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    ;

    @GetMapping("/dashboard")
    public String panelAdministrativo(ModelMap modelo, HttpSession session) {
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        List<Publicacion> publicaciones = publicacionServicio.listarPublicaciones();
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones.html";
    }

    @GetMapping("/eliminar_usuario/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {
        modelo.put("publicacion", publicacionServicio.getReferenceById(id));
        usuarioServicio.eliminarUsuario(id);
        //vuelve a cargar la lista
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);
        return "lista_usuarios.html";
    }

}
