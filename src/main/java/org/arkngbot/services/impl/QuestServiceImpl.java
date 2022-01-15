package org.arkngbot.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.arkngbot.datastructures.TreasureHuntingQuest;
import org.arkngbot.services.CryptoService;
import org.arkngbot.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class QuestServiceImpl implements QuestService {

    private static final String QUESTS_FILE_NAME = "quests.dat";
    private static final String COULD_NOT_FIND_A_QUEST_FOR_THIS_NUMBER = "Could not find a quest for this number.";
    private static final String WRONG_ANSWER_PLEASE_TRY_AGAIN = "Wrong answer! Please try again.";
    private static final String DELIMITER = "\\Z";
    private static final String CORRECT_RESPONSE_NEXT_QUEST_EXISTS = "Congratulations, you have answered correctly!\nHere is the question number %s:\n\n%s";

    private CryptoService cryptoService;

    @Autowired
    public QuestServiceImpl(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @NonNull
    @Override
    public String processRequest(long questionNumber, @NonNull String answer) throws Exception {
        File questsFile = new File(ClassLoader.getSystemClassLoader().getResource(QUESTS_FILE_NAME).getFile());
        Scanner scanner = new Scanner(questsFile).useDelimiter(DELIMITER);
        try {
            String questsFileContent = scanner.next();
            scanner.close();
            String questsJson = cryptoService.decrypt(questsFileContent);
            List<TreasureHuntingQuest> quests = marshalQuestsFile(questsJson);

            return processQuests(quests, questionNumber, answer);
        } catch (Exception e) {
            scanner.close();
            throw e;
        }
    }

    private List<TreasureHuntingQuest> marshalQuestsFile(String questsJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.stream(mapper.readValue(questsJson, TreasureHuntingQuest[].class))
                .collect(Collectors.toList());
    }

    private String processQuests(List<TreasureHuntingQuest> quests, long questionNumber, String answer) throws Exception {

        TreasureHuntingQuest quest = quests.stream()
                .filter(q -> questionNumber == q.getNumber())
                .findFirst()
                .orElse(null);

        if (quest == null) {
            return COULD_NOT_FIND_A_QUEST_FOR_THIS_NUMBER;
        }

        if (Arrays.stream(quest.getAnswers())
                .anyMatch(a -> StringUtils.equalsIgnoreCase(answer, a))) {
            return replyToCorrectAnswer(quests, quest);
        } else {
            return WRONG_ANSWER_PLEASE_TRY_AGAIN;
        }
    }

    private String replyToCorrectAnswer(List<TreasureHuntingQuest> quests, TreasureHuntingQuest currentQuest) {
        if (quests.stream().anyMatch(q -> currentQuest.getNumber() + 1 == q.getNumber())) {
            return String.format(CORRECT_RESPONSE_NEXT_QUEST_EXISTS,
                    currentQuest.getNumber() + 1,
                    currentQuest.getNextQuest());
        }
        else {
            return currentQuest.getNextQuest();
        }
    }

}
