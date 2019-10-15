package com.filemanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextReader {
    private static TextReader instance = new TextReader();

    private TextReader(){}

    public static TextReader getInstance(){
        return instance;
    }

    public String getText(String path){
        String text = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String s;
            while((s = reader.readLine())!=null){
                text += s + "\n";
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return text;
    }
}
