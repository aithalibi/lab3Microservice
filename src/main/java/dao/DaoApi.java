package dao;

import org.springframework.stereotype.Component;

/**
 * Implementation "appel a une API REST externe" (simulee).
 */
@Component("daoApi")
public class DaoApi implements IDao {

    @Override
    public double getValue() {
        System.out.println("[DAO] Appel a l'API REST externe");
        return 220.0;
    }
}
