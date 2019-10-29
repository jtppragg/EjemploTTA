package eus.ehu.bi.tta.ejemplo.bl.beans;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public String getWording() {
        return wording;
    }

    public void setWording(String wording) {
        this.wording = wording;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {
        private int id;
        @SerializedName("answer")
        private String wording;
        private boolean correct;
        private String advise;
        private ResourceType resourceType;

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

        public ResourceType getResourceType() {
            return resourceType;
        }

        public void setResourceType(ResourceType resourceType) {
            this.resourceType = resourceType;
        }

        public String getMime() {
            return getResourceType() == null ? null : getResourceType().getMime();
        }
    }

    public static class ResourceType {
        private int id;
        private String description;
        private String mime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }
    }

    private String wording;
    private final List<Choice> choices = new ArrayList<>();
}
