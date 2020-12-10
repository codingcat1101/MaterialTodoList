package com.edmingle.materialtodo.Pojo;

public class TaskItem {
    private int color;
    private String pseudo;
    private String text;
    private String important;
    public TaskItem(int color, String pseudo, String text, String important) {
        this.color = color;
        this.pseudo = pseudo;
        this.text = text;
        this.important = important;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public String getText() {
        return this.text;
    }

    public int getColor() {
        return this.color;
    }

    public String getImportant() { return this.important; }
}
