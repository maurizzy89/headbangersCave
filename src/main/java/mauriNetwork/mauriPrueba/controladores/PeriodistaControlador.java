package mauriNetwork.mauriPrueba.controladores;

import mauriNetwork.mauriPrueba.entidades.Noticia;
import mauriNetwork.mauriPrueba.servicios.NoticiaServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("periodista")
public class PeriodistaControlador {

    @Autowired
    private NoticiaServicio noticiaServicio;

    @GetMapping("/dashboard")
    public String panelPeriodistico(ModelMap modelo) {
        List<Noticia> noticias = noticiaServicio.listarNoticias();
        modelo.addAttribute("noticias", noticias);
        return "noticias.html";
    }
    
}
