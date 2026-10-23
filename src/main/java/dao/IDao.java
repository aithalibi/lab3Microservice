package dao;

/**
 * Contrat unique d'acces aux donnees.
 *
 * <p>C'est le point de couplage faible entre la couche metier et les sources de
 * donnees. Le metier ne connait QUE cette interface : il ne sait pas si la valeur
 * provient d'une base de donnees, d'un fichier ou d'une API REST.</p>
 */
public interface IDao {

    /**
     * Recupere une valeur numerique depuis la source de donnees.
     *
     * @return la valeur recuperee
     */
    double getValue();
}
