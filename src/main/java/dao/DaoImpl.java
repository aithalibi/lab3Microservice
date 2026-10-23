package dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Implementation "base de donnees de production".
 *
 * <p>Strategie A : {@code @Primary} designe ce bean comme le candidat par defaut
 * lorsque plusieurs implementations de {@link IDao} sont disponibles.</p>
 *
 * <p>Limite : le choix est <b>statique</b>. Changer d'implementation impose de
 * modifier le code et de recompiler.</p>
 */
@Primary
@Component("dao")
public class DaoImpl implements IDao {

    @Override
    public double getValue() {
        System.out.println("[DAO] Lecture depuis la base de donnees (prod) - PRIMARY");
        return 100.0;
    }
}
