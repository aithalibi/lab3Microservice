package config;

import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * Strategie C : selection par propriete externe (etape 13).
 *
 * <p>La valeur lue dans {@code app.properties} (cle {@code dao.target}) determine
 * l'implementation a instancier. On peut donc changer de source de donnees
 * <b>sans recompiler</b>.</p>
 *
 * <p>Comme pour {@link DaoAliasConfig}, le paquet {@code dao} n'est pas scanne afin
 * d'eviter un conflit de beans primaires.</p>
 */
@Configuration
@ComponentScan(basePackages = "metier")
@PropertySource("classpath:app.properties")
public class DynamicDaoConfig {

    @Value("${dao.target}")
    private String daoTarget;

    @Bean
    @Primary
    public IDao dynamicDao() {
        switch (daoTarget) {
            case "dao":
                return new DaoImpl();
            case "dao2":
                return new DaoImpl2();
            case "daoFile":
                return new DaoFile();
            case "daoApi":
                return new DaoApi();
            default:
                throw new IllegalArgumentException("Cible DAO inconnue : " + daoTarget);
        }
    }
}
