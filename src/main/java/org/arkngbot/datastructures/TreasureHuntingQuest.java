package org.arkngbot.datastructures;

public class TreasureHuntingQuest {

    private Long number;

    private String[] answers;

    private String nextQuest;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public String getNextQuest() {
        return nextQuest;
    }

    public void setNextQuest(String nextQuest) {
        this.nextQuest = nextQuest;
    }
}
