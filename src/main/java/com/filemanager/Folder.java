package com.filemanager;

import com.filemanager.servlets.Controller;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Folder {
    String path;
    String url;
    private File myFile;
    File[] children;
    private FileWrapper[] wrappers;
    public static Map<String,File> nameToFile;
    public List<FileWrapper> wrappersList;

    private List parents;
    DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.US);
    private boolean calcRecursiveFolderSize = false;


    public boolean isCalcRecursiveFolderSize()
    {
        return calcRecursiveFolderSize;
    }

    public List getParents()
    {
        return parents;
    }

    private Folder() {// NOP
    }

    public Folder(File f, String path, String url) throws IOException {
        myFile = f;
        this.path = path;
        this.url = url;

        if (!myFile.exists()) {
            throw new IOException(f.getPath() + " does not exist.");
        }
    }

    public List<FileWrapper> getFiles()
    {
        return wrappersList;
    }

    public void load() {
        children = myFile.listFiles();
        if (children == null) {
            return;
        }

        wrappers = new FileWrapper[children.length];

        nameToFile = new HashMap<String,File>(children.length);

        for (int i = 0; i < children.length; i++) {
            String name = children[i].getName();
            wrappers[i] = new FileWrapper(this, i);
            nameToFile.put(name, children[i]);
        }
        wrappersList = Arrays.asList(wrappers);
        String[] pp = path.split("/");
        if ("/".equals(path)) {
            pp = new String[1];
        }
        pp[0] = "/";
        DirRef[] parentLinks = new DirRef[pp.length];
        String s;
        int p = 0;
        for (int i = 0; i < pp.length - 1; i++) {
            s = path.substring(0, 1 + path.indexOf("/", p));
            p = s.length();
            parentLinks[i] = new DirRef(pp[i], s);
        }
        parentLinks[pp.length - 1] = new DirRef(pp[pp.length - 1], null);
        parents = Arrays.asList(parentLinks);
    }

    private boolean checkFileName(String name) {
        if (name.indexOf("..") > -1) {
            return false;
        }
        return true;
    }

    private String rename(String[] selectedIDs, String target) throws Exception {
        if (selectedIDs.length > 1) {
            return "More than 1 file selected";
        }

        if (!checkFileName(target)) {
            return "Illegal target name";
        }

        File f = checkAndGet(selectedIDs[0]);
        if (f == null) {
            return "need to choose file for rename...";
        }
        File f1 = new File(f.getParent(), target);
        if (f1.exists()) {
            return target + " already exists";
        } if (!f.renameTo(f1)) {
            return "failed to rename " + f.getName();
        }
        return "";
    }

    private File getTargetFile(String target) throws IOException {
        File f = null;
        if (target.startsWith(File.separator)) {
            f = new File(target);
        } else {
            f = new File(myFile, target);
        }
        f = f.getCanonicalFile();
        return f;
    }

    public File checkAndGet(String id) {
        String s = null;
        try {
            s = URLDecoder.decode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // NOP
        }

        String s1 = s.substring(0, s.lastIndexOf('.'));
        String s2 = s.substring(s.lastIndexOf('.') + 1);

        File f = nameToFile.get(s1);

        if (f == null) {
            return null;
        }

        long l = f.lastModified();
        if (!(Long.toString(l).equals(s2))) {
            return null;
        }
        return f;
    }

    private String delete(String[] selectedIDs, String target)
            throws Exception {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < selectedIDs.length; i++) {
            File f = checkAndGet(selectedIDs[i]);

            if (null == f) {
                throw new Exception();
            }

            if (!f.delete()) {
                sb.append(f.getName());
            }
        }

        String s = sb.toString();

        if (!"".equals(s)) {
            return "failed to delete " + s;
        }
        return "";
    }
    private String deleteRecursive(String[] selectedIDs, String target) throws Exception {
        StringBuffer sb = new StringBuffer();

        if (!"+".equals(target)) {
            return "Please confirm with +";
        }

        for (int i = 0; i < selectedIDs.length; i++) {
            File f = checkAndGet(selectedIDs[i]);

            if (f == null) {
                throw new Exception();
            }

            try {
                FileUtils.deleteDirectory(f);
            }
            catch (IOException e)
            {
                sb.append(f.getName());
            }
        }

        String s = sb.toString();

        if (!"".equals(s)) {
            return "failed to delete " + s;
        }

        return "";
    }

    private String mkdir(String target) throws IOException {
        File f = getTargetFile(target);

        if (!f.mkdir()) {
            return "could not mkdir " + target;
        }
        return "";
    }
    private String create(String target) throws IOException {
        File f = getTargetFile(target);

        if (!f.createNewFile()) {
            return "could not create file " + target;
        }
        return "";
    }

    private String copyOrCutClipboard(String[] selectedIDs, int cutOrCopy, HttpSession session)
            throws IOException {
        File[] selectedfiles = new File[selectedIDs.length];

        for (int i = 0; i < selectedIDs.length; i++) {
            File f = checkAndGet(selectedIDs[i]);

            if (null == f) {
                throw new IOException();
            }
            selectedfiles[i] = f;
        }

        ClipBoardContent clipBoardContent = new ClipBoardContent(cutOrCopy, selectedfiles);
        session.setAttribute("clipBoardContent", clipBoardContent);


        return "";
    }

    private String pasteClipboard(HttpSession session)
            throws IOException {
        ClipBoardContent clipBoardContent = (ClipBoardContent)session.getAttribute("clipBoardContent");
        if (clipBoardContent == null) {
            return "nothing in clipboard";
        }

        for (int i = 0; i < clipBoardContent.selectedfiles.length; i++) {
            File f = clipBoardContent.selectedfiles[i];
            File f1 = f.getParentFile();

            if (myFile.getCanonicalFile().equals(f1.getCanonicalFile())) {
                return "same folder";
            }
        }

        for (int i = 0; i < clipBoardContent.selectedfiles.length; i++) {
            File f = clipBoardContent.selectedfiles[i];

            if (clipBoardContent.contentType == ClipBoardContent.COPY_CONTENT) {
                if (f.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(f, myFile);
                } else {
                    FileUtils.copyFileToDirectory(f, myFile, true);
                }
            }
            if (clipBoardContent.contentType == ClipBoardContent.CUT_CONTENT) {
                if (f.isDirectory()) {
                    FileUtils.moveDirectoryToDirectory(f, myFile, false);
                }
                else {
                    FileUtils.moveFileToDirectory(f, myFile, false);
                }
            }
            if (clipBoardContent.contentType == ClipBoardContent.CUT_CONTENT) {
                session.removeAttribute("clipBoardContent");
            }
        }
        return "";
    }

    private String clearClipboard(HttpSession session) throws IOException {
        session.removeAttribute("clipBoardContent");
        return "";
    }

    public String action(int action, OutputStream out, String[] selectedIDs,
                         String target, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String res = null;

        switch (action) {
            case Controller.RENAME_ACTION:
                res = rename(selectedIDs, target);
                break;
            case Controller.DELETE_ACTION:
                res = delete(selectedIDs, target);
                break;
            case Controller.DELETE_RECURSIVE_ACTION:
                res = deleteRecursive(selectedIDs, target);
                break;
            case Controller.MKDIR_ACTION:
                res = mkdir(target);
                break;
            case Controller.CREATE_FILE:
                res = create(target);
                break;
            case Controller.CLIPBOARD_COPY_ACTION:
                res = copyOrCutClipboard(selectedIDs, ClipBoardContent.COPY_CONTENT, session);
                break;
            case Controller.CLIPBOARD_CUT_ACTION:
                res = copyOrCutClipboard(selectedIDs, ClipBoardContent.CUT_CONTENT, session);
                break;
            case Controller.CLIPBOARD_PASTE_ACTION:
                res = pasteClipboard(session);
                break;
            case Controller.CLIPBOARD_CLEAR_ACTION:
                res = clearClipboard(session);
                break;
        } if ("".equals(res)) {
            load();
        }
        return res;
    }

    public void upload(FileItem item, boolean unzip) throws Exception {
        String name = item.getName();

        name = name.replaceAll("\\\\", "/");
        int p = name.lastIndexOf('/');
        if (p > -1) {
            name = name.substring(p);
        } else {
            File f = new File(myFile, name);
            item.write(f);
        }
    }
}