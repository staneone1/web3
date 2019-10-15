package com.filemanager.servlets;

import java.io.*;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.filemanager.Folder;
import com.filemanager.dao.NoteDAO;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;


public class Controller extends HttpServlet {
    static {
        NoteDAO.getInstance().connect();
    }

    public static final int NOP_ACTION = 0;
    public static final int RENAME_ACTION = 1;
    public static final int DELETE_ACTION = 2;
    public static final int MKDIR_ACTION = 3;
    public static final int CLIPBOARD_COPY_ACTION = 5;
    public static final int CLIPBOARD_CUT_ACTION = 6;
    public static final int CLIPBOARD_PASTE_ACTION = 7;
    public static final int CLIPBOARD_CLEAR_ACTION = 8;
    public static final int GETURL_ACTION = 9;
    public static final int CREATE_FILE = 10;
    public static final int DELETE_RECURSIVE_ACTION = 11;



    private File tempDir = null;

    private String rootDir = null;

    public void init() throws ServletException {
        tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        rootDir = getServletContext().getInitParameter("rootdir");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();


        String self = null;
        String contextPath = null;
        String pathInfo = null;
        Folder folder = null;
        String queryString = null;
        String language = null;
        String name = null;
        String pathth = null;


        try {
            contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            String method = request.getMethod();
            boolean formPosted = "POST".equals(method);

            pathInfo = request.getPathInfo();

            if (pathInfo == null) {

                PrintWriter writer = response.getWriter();
                writer.print(contextPath + servletPath + " doesn't exist.");

                return;
            }


            File f = new File(rootDir, pathInfo);
            name = f.getName();
            pathth = rootDir + pathInfo;
            if (!f.exists()) {

                PrintWriter writer = response.getWriter();
                writer.print(contextPath + pathInfo + " doesn't exist.");

                return;
            }

            if (f.isFile()) {
                doDownload(request, response, f);
                return;

            }
//            if (f.getName().end) {
//
//            }

            if (!pathInfo.endsWith("/")) {
                response.sendRedirect(request.getRequestURL() + "/");

                return;
            }


            queryString = request.getQueryString();

            String requestURI = request.getRequestURI();

            self = contextPath + servletPath;

            String fileURL = requestURI.replaceFirst(contextPath, "");
            fileURL = fileURL.replaceFirst(servletPath, "");

            folder = new Folder(f, pathInfo, fileURL);

            folder.load();

            String actionresult = "";

            if (FileUpload.isMultipartContent(request)) {
                try {
                    actionresult = handleUpload(request, folder);
                    folder.load();
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            } else if (formPosted || null != queryString) {
                try {
                    actionresult = handleQuery(request, response, folder);
                } catch (Exception e) {
                    actionresult = e.getMessage();
                }
                if (null == actionresult) {
                    return;
                }
            }

            request.setAttribute("actionresult", actionresult);
        } catch (SecurityException e) {
            request.setAttribute("actionresult", e.getClass().getName() + " " + e.getMessage());
            request.setAttribute("fatalerror", new Boolean(true));

        }

        Map<String, String> notes = new HashMap<>();
        File f = new File(rootDir, pathInfo);
        List<String> dirs = new ArrayList<String>();

        File[] files = f.listFiles();
        for (File file: files) {
                dirs.add(file.getName());
                notes.put(file.getName(), NoteDAO.getInstance().getNoteByName(file.getName()));
        }

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            request.setAttribute("principal", principal.getName());
        }
        request.setAttribute("pathth", pathth);
        request.setAttribute("self", self);
        session.setAttribute("self1", pathth);
        request.setAttribute("dirs", dirs);
        request.setAttribute("name", name);
        request.setAttribute("notes", notes);
        request.setAttribute("path", pathInfo);
        request.setAttribute("folder", folder);
        request.setAttribute("url", contextPath);

        String forward = "/WEB-INF/FileManager.jsp";

        if (queryString != null) {
            // hide get query parameters response.sendRedirect(request.getRequestURL() + ""); return;
        }
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(forward);
        System.out.println("post");
        requestDispatcher.forward(request, response);
    }

    private String handleQuery(HttpServletRequest request, HttpServletResponse response, Folder folder)
            throws Exception {
        String rc = "";
        String pathInfo = request.getPathInfo();
        File f = new File(rootDir, pathInfo);
        HttpSession session = request.getSession();

        String target = null;
        int action = NOP_ACTION;
        String[] selectedfiles = request.getParameterValues("index");

        OutputStream out = null;

        String command = request.getParameter("command");
        if ("Create dir".equals(command)) {
            target = request.getParameter("newdir");
            action = MKDIR_ACTION;
        }
        if ("Create file".equals(command)) {
            target = request.getParameter("newfile");
            action = CREATE_FILE;
        }
        if ("GetURL".equals(command)) {
            target = request.getParameter("url");
            action = GETURL_ACTION;
        } else if ("Delete".equals(command)) {
            action = DELETE_ACTION;
        } else if ("DeleteRecursively".equals(command)) {
                target = request.getParameter("confirm");
                action = DELETE_RECURSIVE_ACTION;
        } else if ("Rename to".equals(command)) {
            target = request.getParameter("renameto");
            action = RENAME_ACTION;
        } else if ("Cut".equals(command)) {
            action = CLIPBOARD_CUT_ACTION;
        } else if ("Copy".equals(command)) {
            action = CLIPBOARD_COPY_ACTION;
        } else if ("Paste".equals(command)) {
            action = CLIPBOARD_PASTE_ACTION;
        } else if ("Clear".equals(command)) {
            action = CLIPBOARD_CLEAR_ACTION;
        }

        if (NOP_ACTION == action) {
            return "";
        }

        try {
            rc = folder.action(action, out, selectedfiles, target, session, request, response);
        } catch (SecurityException e) {
            rc = "SecurityException: " + e.getMessage();
            return rc;
        }

        folder.load();
        return rc;
    }

    private String handleUpload(HttpServletRequest request, Folder folder) throws Exception {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setRepositoryPath(tempDir.toString());
        System.out.println(upload.getSizeMax());

        // parse this request by the handler this gives us a list of items from the request
        List items = upload.parseRequest(request);

        Iterator itr = items.iterator();

        boolean unzip = false;

        while (itr.hasNext()) {
            FileItem item = (FileItem) itr.next();

            if (item.isFormField()) {
                String name = item.getFieldName();
                String value = item.getString();
                if ("command".equals(name) && "unzip".equals(value)) {
                    unzip = true;
                }
            } else {
                String name = item.getFieldName();
                unzip = "unzip".equals(name);

                if (!"".equals(item.getName())) {
                    folder.upload(item, unzip);
                }

            }
        }
        return "";
    }

    public void doDownload(HttpServletRequest request, HttpServletResponse response, File f) throws IOException {

        String name = f.getName();

//        String mimeType = getServletContext().getMimeType(name);

        response.setContentType("application/octet-stream");

        response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");

        OutputStream out = response.getOutputStream();

        FileInputStream in = new FileInputStream(f);

        byte[] buf = new byte[512];
        int l;

        try {
            while ((l = in.read(buf)) > 0) {
                out.write(buf, 0, l);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
        }
    }
    @Override
    public void destroy() {
        NoteDAO.getInstance().disconnect();
        super.destroy();
    }
}