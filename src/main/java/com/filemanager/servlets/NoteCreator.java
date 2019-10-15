package com.filemanager.servlets;

import com.filemanager.dao.NoteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "create", urlPatterns = {"/createNote"})
public class NoteCreator extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("nameNote");
        String note = req.getParameter("note");
        NoteDAO.getInstance().addNoteToDB(name, note);

        getServletContext().getRequestDispatcher("/fm/").forward(req, resp);
    }

}
