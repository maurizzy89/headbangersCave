package mauriNetwork.mauriPrueba.entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Periodista extends Usuario {

    @OneToMany
    private List<Noticia> noticiasPeriodista;
    
    private Integer sueldoMensual;
    private boolean alta;

}
