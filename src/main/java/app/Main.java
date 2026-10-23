package app;

import config.AmbiguityDemoConfig;
import config.AppConfig;
import config.DaoAliasConfig;
import config.DynamicDaoConfig;
import config.ProdConfig;
import dao.IDao;
import metier.IMetier;
import metier.MetierMultiple;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;

/**
 * Point d'entree : execute l'une apres l'autre les cinq strategies de selection
 * presentees dans le TP 3, sans jamais modifier {@code MetierImpl}.
 */
public class Main {

    public static void main(String[] args) {

        scenarioAmbiguite();
        scenarioPrimary();
        scenarioAlias();
        scenarioProprietes();
        scenarioProfil();
        scenarioMultiDao();

        System.out.println();
        System.out.println("=== FIN === ");
    }

    /**
     * Etape 10 : sans mecanisme de selection, Spring ne peut pas choisir entre
     * quatre implementations de IDao.
     */
    private static void scenarioAmbiguite() {
        titre("SCENARIO 0 : AMBIGUITE (aucune strategie de selection)");
        AnnotationConfigApplicationContext context = null;
        try {
            context = new AnnotationConfigApplicationContext(AmbiguityDemoConfig.class);
            context.getBean(IMetier.class).calcul();
            System.out.println("!! Aucune exception : le scenario n'est pas celui attendu.");
        } catch (BeansException e) {
            NoUniqueBeanDefinitionException ambiguite = causeNoUnique(e);
            if (ambiguite != null) {
                System.out.println("EXCEPTION ATTENDUE : " + ambiguite.getClass().getSimpleName());
                System.out.println("MESSAGE            : " + ambiguite.getMessage());
            } else {
                System.out.println("EXCEPTION INATTENDUE : " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /** Spring enveloppe souvent l'erreur : on cherche la cause reelle dans la chaine. */
    private static NoUniqueBeanDefinitionException causeNoUnique(Throwable erreur) {
        for (Throwable cause = erreur; cause != null; cause = cause.getCause()) {
            if (cause instanceof NoUniqueBeanDefinitionException) {
                return (NoUniqueBeanDefinitionException) cause;
            }
            if (cause.getCause() == cause) {
                break;
            }
        }
        return null;
    }

    /**
     * Etape 11 : strategie A, {@code @Primary} sur {@code DaoImpl}.
     */
    private static void scenarioPrimary() {
        titre("SCENARIO A : @Primary -> DaoImpl par defaut");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            IMetier metier = context.getBean(IMetier.class);
            System.out.println("Bean IDao selectionne : " + context.getBean(IDao.class).getClass().getSimpleName());
            System.out.println("Resultat metier : " + metier.calcul());
        } finally {
            context.close();
        }
    }

    /**
     * Etape 12 : strategie B, bean alias declare dans {@link DaoAliasConfig}.
     */
    private static void scenarioAlias() {
        titre("SCENARIO B : alias via @Bean -> DaoApi");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(DaoAliasConfig.class);
        try {
            IMetier metier = context.getBean(IMetier.class);
            System.out.println("Bean IDao selectionne : " + context.getBean(IDao.class).getClass().getSimpleName());
            System.out.println("Resultat metier : " + metier.calcul());
        } finally {
            context.close();
        }
    }

    /**
     * Etapes 13 et 14 : strategie C, selection par propriete externe.
     * Deux executions : valeur du fichier, puis surcharge a l'execution.
     */
    private static void scenarioProprietes() {
        titre("SCENARIO C1 : propriete app.properties (dao.target=dao2)");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(DynamicDaoConfig.class);
        try {
            IMetier metier = context.getBean(IMetier.class);
            System.out.println("dao.target = " + context.getEnvironment().getProperty("dao.target"));
            System.out.println("Bean IDao selectionne : " + context.getBean(IDao.class).getClass().getSimpleName());
            System.out.println("Resultat metier : " + metier.calcul());
        } finally {
            context.close();
        }

        titre("SCENARIO C2 : surcharge externe (dao.target=daoApi) sans recompiler");
        AnnotationConfigApplicationContext overrideContext = new AnnotationConfigApplicationContext();
        MutablePropertySources sources = overrideContext.getEnvironment().getPropertySources();
        // Meme priorite qu'une variable d'environnement : la surcharge passe avant app.properties.
        sources.addFirst(new MapPropertySource(
                "surcharge-externe", Collections.singletonMap("dao.target", "daoApi")));
        overrideContext.register(DynamicDaoConfig.class);
        overrideContext.refresh();
        try {
            IMetier metier = overrideContext.getBean(IMetier.class);
            System.out.println("dao.target = " + overrideContext.getEnvironment().getProperty("dao.target"));
            System.out.println("Bean IDao selectionne : " + overrideContext.getBean(IDao.class).getClass().getSimpleName());
            System.out.println("Resultat metier : " + metier.calcul());
        } finally {
            overrideContext.close();
        }
    }

    /**
     * Etape 17 : selection par profil Spring.
     */
    private static void scenarioProfil() {
        titre("SCENARIO D : profil Spring 'prod' -> DaoImplProd");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("prod");
        context.register(ProdConfig.class);
        context.refresh();
        try {
            IMetier metier = context.getBean(IMetier.class);
            System.out.println("Profils actifs : " + String.join(",", context.getEnvironment().getActiveProfiles()));
            System.out.println("Bean IDao selectionne : " + context.getBean(IDao.class).getClass().getSimpleName());
            System.out.println("Resultat metier : " + metier.calcul());
        } finally {
            context.close();
        }
    }

    /**
     * Etape 20 : injection d'une liste et d'une map de DAO.
     */
    private static void scenarioMultiDao() {
        titre("SCENARIO E : injection de plusieurs DAO (List et Map)");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            MetierMultiple multiple = context.getBean("metierMultiple", MetierMultiple.class);
            System.out.println("Nombre de DAO injectes dans la List : " + multiple.getDaos().size());
            multiple.getDaoMap().forEach((nom, dao) ->
                    System.out.println("   Map[" + nom + "] -> " + dao.getClass().getSimpleName()
                            + " = " + dao.getValue()));
            System.out.println("Resultat metier (somme) : " + multiple.calcul());
        } finally {
            context.close();
        }
    }

    private static void titre(String texte) {
        System.out.println();
        System.out.println("=== " + texte + " ===");
    }
}
