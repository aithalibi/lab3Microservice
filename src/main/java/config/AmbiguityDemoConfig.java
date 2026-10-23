package config;

import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Reproduction de l'ambiguite de l'etape 10.
 *
 * <p>Quatre implementations de {@link IDao} sont declarees <b>sans aucun
 * {@code @Primary}</b>. Spring ne peut donc pas choisir : il leve une
 * {@code NoUniqueBeanDefinitionException} au moment d'injecter le DAO dans
 * {@code MetierImpl}.</p>
 *
 * <p>Point important : les noms de beans sont volontairement differents du nom
 * du parametre de constructeur de {@code MetierImpl} ({@code dao}). Sinon
 * Spring retombe sur la resolution <i>par nom</i> et choisit silencieusement le
 * bean homonyme, ce qui masque completement l'ambiguite.</p>
 */
@Configuration
@ComponentScan(basePackages = "metier")
public class AmbiguityDemoConfig {

    @Bean
    public IDao sqlDao() {
        return new DaoImpl();
    }

    @Bean
    public IDao devDao() {
        return new DaoImpl2();
    }

    @Bean
    public IDao fichierDao() {
        return new DaoFile();
    }

    @Bean
    public IDao apiDao() {
        return new DaoApi();
    }
}
