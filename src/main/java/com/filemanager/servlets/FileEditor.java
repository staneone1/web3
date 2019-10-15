package com.filemanager.servlets;

import com.filemanager.TextEditor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileEditor extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String text = req.getParameter("newText");
        String path = req.getParameter("fileName");

        TextEditor.getInstance().editText(text, path);
        getServletContext().getRequestDispatcher("/fm/").forward(req, resp);
    }
}
