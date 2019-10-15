package com.filemanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextEditor {
    private static TextEditor instance = new TextEditor();

    private TextEditor(){}

    public static TextEditor getInstance(){
        return instance;
    }

    public void editText(String text, String path){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
            writer.write(text);
            writer.flush();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

}
