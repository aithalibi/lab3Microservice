package dao;

import org.springframework.stereotype.Component;

/**
 * Implementation "base de donnees de developpement".
 *
 * <p>Le nom de bean ({@code dao2}) est important : c'est lui qui sert de valeur
 * a la propriete {@code dao.target} dans la strategie C.</p>
 */
@Component("dao2")
public class DaoImpl2 implements IDao {

    @Override
    public double getValue() {
        System.out.println("[DAO] Lecture depuis la base de donnees (dev)");
        return 150.0;
    }
}
