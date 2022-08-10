package mauriNetwork.mauriPrueba;

import mauriNetwork.mauriPrueba.excepciones.MyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SitioNoticiasApplication {

    public static void main(String[] args) throws MyException {
        SpringApplication.run(SitioNoticiasApplication.class, args);
    }

}
