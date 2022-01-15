package org.arkngbot.services;

import org.arkngbot.services.impl.CryptoServiceImpl;
import org.arkngbot.services.impl.QuestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class QuestServiceTest {
    private static final String COULD_NOT_FIND_A_QUEST_FOR_THIS_NUMBER = "Could not find a quest for this number.";
    private static final String WRONG_ANSWER_PLEASE_TRY_AGAIN = "Wrong answer! Please try again.";
    private static final String QUESTS_JSON = "[\n" +
            "  {\n" +
            "  \"number\": 1,\n" +
            "  \"answers\": [\"answer1\", \"answer2\"],\n" +
            "  \"nextQuest\": \"This is your next quest!\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"number\": 2,\n" +
            "    \"answers\": [\"answer3\", \"answer4\"],\n" +
            "    \"nextQuest\": \"This is your final quest!\"\n" +
            "  }\n" +
            "]";
    private static final String CORRECT_ANSWER_QUESTION_ONE = "Congratulations, you have answered correctly!\nHere is the question number 2:\n\nThis is your next quest!";
    private static final String CORRECT_ANSWER_QUESTION_TWO = "This is your final quest!";
    private static final String ANSWER_1 = "answer1";
    private static final String ANSWER_2 = "answer2";
    private static final String ANSWER_3 = "answer3";

    private QuestService questService;
    private CryptoService cryptoServiceMock;

    @BeforeEach
    public void setUp() throws Exception {
        cryptoServiceMock = mock(CryptoServiceImpl.class);
        questService = new QuestServiceImpl(cryptoServiceMock);
        when(cryptoServiceMock.decrypt(any())).thenReturn(QUESTS_JSON);
    }

    @Test
    public void shouldProcessCorrectAnswerQ1A1() throws Exception {
        String reply = questService.processRequest(1, ANSWER_1);
        assertThat(reply, is(CORRECT_ANSWER_QUESTION_ONE));
    }

    @Test
    public void shouldProcessCorrectAnswerQ1A2() throws Exception {
        String reply = questService.processRequest(1, ANSWER_2);
        assertThat(reply, is(CORRECT_ANSWER_QUESTION_ONE));
    }

    @Test
    public void shouldProcessCorrectAnswerQ2A3() throws Exception {
        String reply = questService.processRequest(2, ANSWER_3);
        assertThat(reply, is(CORRECT_ANSWER_QUESTION_TWO));
    }

    @Test
    public void shouldProcessInorrectAnswerQ1A3() throws Exception {
        String reply = questService.processRequest(1, ANSWER_3);
        assertThat(reply, is(WRONG_ANSWER_PLEASE_TRY_AGAIN));
    }

    @Test
    public void shouldFindNoQuestion() throws Exception {
        String reply = questService.processRequest(3, ANSWER_1);
        assertThat(reply, is(COULD_NOT_FIND_A_QUEST_FOR_THIS_NUMBER));
    }

}
