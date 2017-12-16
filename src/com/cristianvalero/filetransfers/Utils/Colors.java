package com.cristianvalero.filetransfers.Utils;

public enum Colors
{
    BLACK("\u001B[30m"),
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    CYAN("\u001B[36m");

    private String color;

    Colors(String c)
    {
        this.color = c;
    }

    @Override
    public String toString()
    {
        return this.color;
    }

    public String getColor() {
        return this.color;
    }
}
