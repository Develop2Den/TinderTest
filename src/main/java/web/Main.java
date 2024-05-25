package web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import web.servlets.*;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class Main {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setClassForTemplateLoading(Main.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        UsersSQL userProfileDAO = new UsersSQL();

        handler.addServlet(new ServletHolder(new LoginServlet(userProfileDAO, cfg)), "/login");
        handler.addServlet(new ServletHolder(new UsersServlet(userProfileDAO)), "/users");
        handler.addServlet(new ServletHolder(new LikedProfilesServlet(userProfileDAO, cfg)), "/liked");
        handler.addServlet(new ServletHolder(new MessagesServlet(userProfileDAO, cfg)), "/messages/*");

        handler.addServlet(new ServletHolder(new ContentServlet("static")), "/static/*");

        server.setHandler(handler);
        server.start();
        server.join();
    }
}
