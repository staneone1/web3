package com.filemanager;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.Date;


import org.apache.commons.io.FileUtils;

public class FileWrapper {

    private Folder folder;
    int index;

    FileWrapper(Folder folder, int index) {
        this.folder = folder;
        this.index = index;
    }

    public File getFile() {
        return folder.children[index];
    }

    public String getUrl() {
        File file = getFile();
        return folder.url + file.getName();
    }

    public String getId() {
        String s = null;
        File file = getFile();
        try {
            s = URLEncoder.encode(file.getName() + "." + Long.toString(file.lastModified()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return s;
    }

    public String getName() {
        return getFile().getName();
    }

    public String getPath() {
        File file = getFile();
        return folder.path + file.getName();
    }

    public String toString() {
        return getFile().toString();
    }

    public String getSize() {
        long l;

        File file = getFile();

        l = file.length();

        if (file.isDirectory() && folder.isCalcRecursiveFolderSize()) {
            l = FileUtils.sizeOfDirectory(file);
        }

        String s = humanReadableByteCount(l, true);

        return s;
    }

    public boolean getIsDirectory() {
        File file = getFile();
        return file.isDirectory();
    }

    public boolean getIsZip() {
        File file = getFile();
        if (file.isDirectory()) {
            return false;
        }
        String s = file.getName().toLowerCase();

        return s.endsWith(".zip");
    }

    public String getType() throws IOException {
        File file = getFile();
        if (file.isDirectory()) return "dir";
        else if (file.getName().endsWith(".pdf")) return "pdf";
        else if (file.getName().endsWith(".pptx")) return "pptx";
        else if (file.getName().endsWith(".zip")) return "zip";
        else if (file.getName().endsWith(".txt")) return "txt";
        else return "file";
    }

    public String getLastModified() {
        File file = getFile();
        long l = file.lastModified();
        String s = folder.dateFormat.format(new Date(l));
        return s;
    }


    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}