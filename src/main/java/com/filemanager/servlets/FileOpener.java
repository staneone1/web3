package com.filemanager.servlets;

import com.filemanager.TextReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileOpener extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("name");
        String path = fileName;
        System.out.println("path");
        String text = TextReader.getInstance().getText(path);
        req.setAttribute("text", text);
        req.setAttribute("fileName", fileName);
        getServletContext().getRequestDispatcher("/WEB-INF/Editor.jsp").forward(req, resp);
    }
}
