package eus.ehu.tta.ejemplo.model.beans;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private int id;
    private String wording;
    private final List<Choice> choices = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWording() {
        return wording;
    }

    public void setWording(String wording) {
        this.wording = wording;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
