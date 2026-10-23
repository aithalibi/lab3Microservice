package metier;

import dao.IDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Etape 20 : injection de <b>plusieurs</b> implementations de la meme interface.
 *
 * <p>Spring injecte automatiquement :</p>
 * <ul>
 *   <li>une {@code List<IDao>} contenant tous les beans de type {@code IDao} ;</li>
 *   <li>une {@code Map<String, IDao>} indexee par <b>nom de bean</b>.</li>
 * </ul>
 */
@Service("metierMultiple")
public class MetierMultiple implements IMetier {

    private final List<IDao> daos;
    private final Map<String, IDao> daoMap;

    public MetierMultiple(List<IDao> daos, Map<String, IDao> daoMap) {
        this.daos = daos;
        this.daoMap = daoMap;
    }

    /**
     * Somme des valeurs de toutes les implementations injectees.
     */
    @Override
    public double calcul() {
        return daos.stream().mapToDouble(IDao::getValue).sum();
    }

    public List<IDao> getDaos() {
        return daos;
    }

    public Map<String, IDao> getDaoMap() {
        return daoMap;
    }
}
