package org.dice_research.fc.paths.paths;

import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import java.util.*;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HibernateManyToManyAnnotationPathIntegrationTest {
    private static SessionFactory sessionFactory;
    private Session session;

/*    @Test
    public void givenData_whenInsert_thenCreatesMtoMrelationship() {
        String[] pathData = { "", "" }; //empl
        String[] pathElemetData = { "", "" }; // pr
        Set<PathElement> pathElements = new HashSet<>();

        for (String element : pathElemetData) {
            pathElements.add(new PathElement(element));
        }

        for (String p : pathData) {
            Path path = new Path(p.split(" ")[0]);

            assertEquals(0, path.getPathElement().size());
            path.setPathElement(pathElements);
            session.persist(path);

            assertNotNull(path);
        }
    }

    @Test
    public void givenSession_whenRead_thenReturnsMtoMdata() {
        @SuppressWarnings("unchecked")
        List<Employee> employeeList = session.createQuery("FROM Employee")
                .list();

        assertNotNull(employeeList);

        for(Employee employee : employeeList) {
            assertNotNull(employee.getProjects());
        }
    }*/

}
