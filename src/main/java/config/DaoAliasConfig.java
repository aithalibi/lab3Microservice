package config;

import dao.DaoApi;
import dao.IDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Strategie B : alias via {@code @Bean} (etape 12).
 *
 * <p>Un bean « pointeur » est declare par configuration. Pour changer de source de
 * donnees, il suffit de remplacer {@code new DaoApi()} par {@code new DaoFile()}
 * dans cette classe : ni les DAO, ni {@code MetierImpl} ne bougent.</p>
 *
 * <p>Le paquet {@code dao} n'est volontairement <b>pas</b> scanne ici : sinon les
 * quatre {@code @Component} seraient aussi candidats, et {@code DaoImpl} etant
 * {@code @Primary} on aurait <b>deux</b> beans primaires, ce que Spring refuse.</p>
 */
@Configuration
@ComponentScan(basePackages = "metier")
public class DaoAliasConfig {

    /**
     * Bean alias : c'est lui qui sera injecte dans {@code MetierImpl}.
     *
     * @return l'implementation ciblee, ici l'API REST
     */
    @Bean
    @Primary
    public IDao daoAlias() {
        // Pointe vers l'implementation API ; remplacer par new DaoFile() pour changer de cible.
        return new DaoApi();
    }
}
