package metier;

/**
 * Contrat de la couche metier.
 */
public interface IMetier {

    /**
     * Applique la logique metier a la valeur fournie par le DAO.
     *
     * @return la valeur metier calculee
     */
    double calcul();
}
