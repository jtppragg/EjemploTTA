package eus.ehu.bi.tta.ejemplo.bl.beans;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    private int id;
    @SerializedName("user")
    private String name;
    @SerializedName("lessonNumber")
    private int currentLesson;
    private String lessonTitle;
    @SerializedName("nextTest")
    private int currentTest;
    @SerializedName("nextExercise")
    private int currentExercise;
    private String pictureUrl;

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

    public int getCurrentLesson() {
        return currentLesson;
    }

    public void setCurrentLesson(int currentLesson) {
        this.currentLesson = currentLesson;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
