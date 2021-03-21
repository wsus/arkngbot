package org.arkngbot.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.services.CoreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ArkngBot {

    private static final String BASE_PACKAGE = "org.arkngbot";
    private static final String NO_TOKEN_MESSAGE = "No token passed in. Exiting.";
    private static final Logger LOGGER = LogManager.getLogger(ArkngBot.class);

    public static void main(String[] args) {

        if (args.length == 0) {
            LOGGER.error(NO_TOKEN_MESSAGE);
            System.exit(-1);
        }

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BASE_PACKAGE);
        CoreService coreService = applicationContext.getBean(CoreService.class);

        coreService.initBot(args[0]);
    }
}
