package test;

import config.AmbiguityDemoConfig;
import config.AppConfig;
import config.DaoAliasConfig;
import config.DynamicDaoConfig;
import config.ProdConfig;
import dao.DaoImplProd;
import metier.IMetier;
import metier.MetierMultiple;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests de non-regression (etape 16) : {@code MetierImpl} n'est jamais modifiee,
 * seul le resultat change selon la configuration active.
 *
 * <p>Valeurs de reference : DaoImpl 100, DaoImpl2 150, DaoFile 180, DaoApi 220.
 * Le metier double la valeur lue.</p>
 */
public class OcpSelectionTest {

    private static final double DELTA = 0.001;

    @Test
    public void testDaoImplParDefaut() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            IMetier metier = context.getBean(IMetier.class);
            // @Primary designe DaoImpl : 100.0 * 2
            assertEquals(200.0, metier.calcul(), DELTA);
        }
    }

    @Test
    public void testAliasPointeVersDaoApi() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(DaoAliasConfig.class)) {
            IMetier metier = context.getBean(IMetier.class);
            // L'alias pointe sur DaoApi : 220.0 * 2
            assertEquals(440.0, metier.calcul(), DELTA);
        }
    }

    @Test
    public void testProprieteSelectionneDao2() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(DynamicDaoConfig.class)) {
            IMetier metier = context.getBean(IMetier.class);
            // app.properties : dao.target=dao2 -> 150.0 * 2
            assertEquals(300.0, metier.calcul(), DELTA);
        }
    }

    @Test
    public void testSurchargeParProprieteExterne() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        MutablePropertySources sources = context.getEnvironment().getPropertySources();
        sources.addFirst(new MapPropertySource(
                "surcharge", Collections.singletonMap("dao.target", "daoApi")));
        context.register(DynamicDaoConfig.class);
        context.refresh();
        try {
            // Surcharge : dao.target=daoApi -> 220.0 * 2
            assertEquals(440.0, context.getBean(IMetier.class).calcul(), DELTA);
        } finally {
            context.close();
        }
    }

    @Test
    public void testProfilProd() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("prod");
        context.register(ProdConfig.class);
        context.refresh();
        try {
            Object dao = context.getBean(dao.IDao.class);
            assertTrue("Le profil prod doit injecter DaoImplProd", dao instanceof DaoImplProd);
            // DaoImplProd : 100.0 * 2
            assertEquals(200.0, context.getBean(IMetier.class).calcul(), DELTA);
        } finally {
            context.close();
        }
    }

    @Test
    public void testAmbiguiteSansSelection() {
        NoUniqueBeanDefinitionException ambiguite = null;
        AnnotationConfigApplicationContext context = null;
        try {
            // Quatre candidats, aucun @Primary -> ambiguite
            context = new AnnotationConfigApplicationContext(AmbiguityDemoConfig.class);
            context.getBean(IMetier.class).calcul();
        } catch (BeansException e) {
            ambiguite = causeNoUnique(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
        assertNotNull("Une NoUniqueBeanDefinitionException était attendue", ambiguite);
        assertTrue("Le message doit signaler les 4 candidats : " + ambiguite.getMessage(),
                ambiguite.getMessage().contains("4"));
    }

    /** Spring enveloppe l'erreur dans une UnsatisfiedDependencyException : on cherche la cause reelle. */
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

    @Test
    public void testInjectionDePlusieursDao() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            MetierMultiple multiple = context.getBean("metierMultiple", MetierMultiple.class);
            assertEquals(4, multiple.getDaos().size());
            assertEquals(4, multiple.getDaoMap().size());
            assertNotNull(multiple.getDaoMap().get("daoApi"));
            // 100 + 150 + 180 + 220
            assertEquals(650.0, multiple.calcul(), DELTA);
        }
    }
}
