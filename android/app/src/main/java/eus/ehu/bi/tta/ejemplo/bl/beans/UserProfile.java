package eus.ehu.bi.tta.ejemplo.bl.beans;

public class UserProfile {
    private int id;
    private String name;
    private Lesson currentLesson;
    private int currentTest;
    private int currentExercise;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lesson getCurrentLesson() {
        return currentLesson;
    }

    public void setCurrentLesson(Lesson currentLesson) {
        this.currentLesson = currentLesson;
    }

    public int getCurrentTest() {
        return currentTest;
    }

    public void setCurrentTest(int currentTest) {
        this.currentTest = currentTest;
    }

    public int getCurrentExercise() {
        return currentExercise;
    }

    public void setCurrentExercise(int currentExercise) {
        this.currentExercise = currentExercise;
    }
}
