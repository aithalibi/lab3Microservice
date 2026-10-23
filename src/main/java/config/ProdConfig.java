package config;

import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * Etape 17 : selection par <b>profil</b> Spring.
 *
 * <p>Active uniquement lorsque le profil {@code prod} est actif. Les quatre DAO
 * « classiques » sont explicitement exclus du scan : sinon {@code DaoImpl}
 * ({@code @Primary}) resterait prioritaire et {@code DaoImplProd} ne serait jamais
 * injecte.</p>
 */
@Configuration
@Profile("prod")
@ComponentScan(
        basePackages = {"dao", "metier"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {DaoImpl.class, DaoImpl2.class, DaoFile.class, DaoApi.class}))
public class ProdConfig {

    // Seul dao.DaoImplProd (@Profile("prod")) sera enregistre.
}
