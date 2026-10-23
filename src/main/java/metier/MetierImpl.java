package metier;

import dao.IDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Coeur de l'application : ne depend QUE de l'interface {@link IDao}.
 *
 * <p>Cette classe n'est <b>jamais</b> modifiee lorsqu'on change de source de
 * donnees : c'est exactement le principe Ouvert/Ferme (OCP). L'injection se fait
 * par constructeur : la dependance est obligatoire et le champ peut rester
 * {@code final}.</p>
 *
 * <p>{@code @Primary} est ajoute ici parce que le projet contient une seconde
 * implementation de {@link IMetier} ({@link MetierMultiple}) pour l'etape 20 :
 * sans cette annotation, {@code getBean(IMetier.class)} serait ambigu.</p>
 */
@Service
@Primary
public class MetierImpl implements IMetier {

    private final IDao dao;

    /**
     * Injection par constructeur : Spring fournit l'implementation selectionnee
     * par la configuration active.
     *
     * @param dao implementation injectee
     */
    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    /**
     * Trace de debogage : confirme quelle implementation a reellement ete injectee.
     */
    @PostConstruct
    public void init() {
        System.out.println("[TRACE] Implementation DAO injectee : " + dao.getClass().getSimpleName());
    }

    @Override
    public double calcul() {
        double valeur = dao.getValue();
        // Logique metier simple : on double la valeur lue.
        return valeur * 2.0;
    }

    public IDao getDao() {
        return dao;
    }
}
