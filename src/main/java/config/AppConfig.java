package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration de base (etapes 8 et 14).
 *
 * <p>{@code @Configuration} declare cette classe comme source de definitions de
 * beans ; {@code @ComponentScan} demande a Spring de parcourir les paquets
 * {@code dao} et {@code metier} pour y trouver les {@code @Component} et
 * {@code @Service}. {@code @PropertySource} charge le fichier de proprietes.</p>
 */
@Configuration
@ComponentScan(basePackages = {"dao", "metier"})
@PropertySource("classpath:app.properties")
public class AppConfig {

    // Pas de @Bean ici : tout passe par la detection automatique des composants.
}
