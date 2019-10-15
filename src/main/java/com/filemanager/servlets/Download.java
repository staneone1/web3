package com.filemanager.servlets;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "download", urlPatterns = {"/fm/download"})

public class Download extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//    private ServletFileUpload uploader = null;
//    @Override
//    public void init() throws ServletException{
//        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
//        File filesDir = (File) getServletContext().getAttribute("pathth");
//        fileFactory.setRepository(filesDir);
//        this.uploader = new ServletFileUpload(fileFactory);
//    }
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String fileName = request.getParameter("name");
//        if(fileName == null || fileName.equals("")){
//            throw new ServletException("File Name can't be null or empty");
//        }
//        File file = new File(request.getServletContext().getAttribute("pathth")+File.separator+fileName);
//        if(!file.exists()){
//            throw new ServletException("File doesn't exists on server.");
//        }
//        System.out.println("File location on server::"+file.getAbsolutePath());
//        ServletContext ctx = getServletContext();
//        InputStream fis = new FileInputStream(file);
//        String mimeType = ctx.getMimeType(file.getAbsolutePath());
//        response.setContentType(mimeType != null? mimeType:"application/octet-stream");
//        response.setContentLength((int) file.length());
//        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//
//        ServletOutputStream os = response.getOutputStream();
//        byte[] bufferData = new byte[1024];
//        int read=0;
//        while((read = fis.read(bufferData))!= -1){
//            os.write(bufferData, 0, read);
//        }
//        os.flush();
//        os.close();
//        fis.close();
//        System.out.println("File downloaded at client successfully");
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getAttribute("pathth") + "\\" + request.getParameter("name");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename=" + request.getParameter("name"));
        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        ServletOutputStream out = response.getOutputStream();
        int i;
        while ((i = in.read())!=-1){
            out.write(i);
        }
        in.close();
        out.flush();
        out.close();
    }
}
