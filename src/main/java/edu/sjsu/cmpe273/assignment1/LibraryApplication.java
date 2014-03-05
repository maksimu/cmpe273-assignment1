package edu.sjsu.cmpe273.assignment1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: maksim
 * Date: 2/22/14 - 2:21 PM
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class LibraryApplication {

    private static Log logger = LogFactory.getLog(LibraryApplication.class);


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(LibraryApplication.class);
        app.setShowBanner(false);
        app.run(args);
    }

    // Put Servlets and Filters in their own nested class so they don't force early
    // instantiation of ManagementServerProperties.
    @Configuration
    protected static class ApplicationContextFilterConfiguration {

        /**
         * ETags header (aka Conditional GET)
         * @return ShallowEtagHeaderFilter
         */
        @Bean
        public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
            logger.info("Adding ETag support (aka Conditional GET)");
            return new ShallowEtagHeaderFilter();
        }

        /**
         * Servlet initialization/destroy listeners
         * @return ServletContextListener
         */
        @Bean
        protected ServletContextListener listener() {
            return new ServletContextListener() {
                @Override
                public void contextInitialized(ServletContextEvent sce) {
                    logger.info("ServletContext initialized");
                }

                @Override
                public void contextDestroyed(ServletContextEvent sce) {
                    logger.info("ServletContext destroyed");
                }
            };
        }

        /**
         * Enable UTF-8 Character encoding
         * @return CharacterEncodingFilter
         */
        @Bean
        public CharacterEncodingFilter characterEncodingFilter() {
            logger.info("Enabling UTF-8 Character encoding");
            final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
            characterEncodingFilter.setEncoding("UTF-8");
            characterEncodingFilter.setForceEncoding(true);
            return characterEncodingFilter;
        }
    }
}
