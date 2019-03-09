package application.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.StreamSupport;

/**
 * A servlet which uses JPA to persist data.
 */
@WebServlet(urlPatterns = "/")
public class JPAServlet extends HttpServlet {

    /**  */
    private static final long serialVersionUID = 1L;

    @Autowired
    private ThingRepository thingRepository;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("Hello JPA World");

        try {
            // First create a Thing in the database, then retrieve it
            createThing(writer);
            retrieveThing(writer);
        } catch (Exception e) {
            writer.println("Something went wrong. Caught exception " + e);
        }
    }

    @Transactional
    public void createThing(PrintWriter writer) throws IllegalStateException, SecurityException {
        Thing thing = new Thing();
        thing = thingRepository.save(thing);
        writer.println("Created Thing: " + thing);
    }

    @SuppressWarnings("unchecked")
    public void retrieveThing(PrintWriter writer) {
        Iterable<Thing> things = thingRepository.findAll();
        writer.println("Query returned " + StreamSupport.stream(things.spliterator(), false).count() + " things");

        // Let's see what we got back!
        for (Thing thing : things) {
            writer.println("Thing in list " + thing);
        }
    }
}
