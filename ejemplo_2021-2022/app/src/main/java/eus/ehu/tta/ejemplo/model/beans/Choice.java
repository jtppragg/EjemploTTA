package eus.ehu.tta.ejemplo.model.beans;

public class Choice {
    private int id;
    private String answer;
    private boolean correct;
    private String advise;
    private String adviseType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getAdvise() {
        return advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    public String getAdviseType() {
        return adviseType;
    }

    public void setAdviseType(String adviseTypeType) {
        this.adviseType = adviseTypeType;
    }
}
