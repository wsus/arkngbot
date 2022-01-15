package org.arkngbot.services;

import org.springframework.lang.NonNull;

/**
 * Processes the requests for the treasure hunting game.
 */
public interface QuestService {

    /**
     * Processes a request for a treasure hunting game. Returns next question if the answer was correct.
     * @param questionNumber the question whose answer is to be validated
     * @param answer the answer to be validated
     * @return next question if the answer was correct, info message if it wasn't
     */
    @NonNull
    String processRequest(long questionNumber, @NonNull String answer) throws Exception;
}
