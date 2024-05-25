package web.servlets;

import web.utils.ResourcesOps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentServlet extends HttpServlet {

    private final String prefix;

    public ContentServlet(String root) {
        this.prefix = ResourcesOps.dirUnsafe(root);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getPathInfo();
        String fullName = prefix + fileName;

        if (!new File(fullName).exists()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            Path path = Paths.get(fullName);
            Files.copy(path, resp.getOutputStream());
        }
    }
}
