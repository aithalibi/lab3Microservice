package dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Variante active uniquement sous le profil Spring {@code prod}.
 *
 * <p>Alternative a la selection par proprietes : le bean n'existe que si le profil
 * est actif. Moins souple qu'une propriete externe (il faut relancer avec un autre
 * profil), mais tres lisible pour separer les environnements.</p>
 */
@Component("daoProd")
@Profile("prod")
public class DaoImplProd implements IDao {

    @Override
    public double getValue() {
        System.out.println("[DAO] Lecture depuis la base de donnees de PRODUCTION (profil prod)");
        return 100.0;
    }
}
