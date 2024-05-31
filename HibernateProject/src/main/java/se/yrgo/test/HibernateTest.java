package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.util.List;

public class HibernateTest {
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

    public static void main(String[] args) {
        setUpData();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Uppgift-1- Navigera över relationer(med member of)
        // Skriv en query för att få namnet på alla elever vars tutor kan undervisa i science.
        // Vi kan skriva den här frågan på olika sätt. Men vad vi vill ha här
        // är att använda member of.

        Subject science = em.createQuery("select s from Subject s where " +
                "s.subjectName = 'Science'", Subject.class).getSingleResult();

        List<String> students = em.createQuery("select s.name from Tutor t join " +
                        "t.teachingGroup s where :subject member of " +
                        "t.subjectsToTeach", String.class)
                .setParameter("subject", science)
                .getResultList();

        for (var student : students) {
            System.out.println("Student: " + student);
        }


        // Uppgift-2- Report Query- Multiple fields (med join)
        // Skriv en query för att hämta namnet på alla studenter och
        // namnet på deras handledare(tutor).
        // Det finns olika sätt att göra detta. Men här vill vi
        // att du använder en report query (sql) genom att använda join.

        List<Object[]> results2 = em.createQuery("select s.name, t.name from Tutor t join " +
                "t.teachingGroup as s").getResultList();
        for (Object[] obj : results2) {
            System.out.println("Student name: " + obj[0] + ", Tutor name: " + obj[1]);
        }


        // Uppgift-3-Report Query- Aggregation
        // Använd aggregation för att få den genomsnittliga terminslängden
        // (average semester) för ämnena (subjects).

        double averageSemesterLength = (Double) em.createQuery("select avg(numberOfSemesters) " +
                "from Subject s").getSingleResult();
        System.out.println("Average semester length is " + averageSemesterLength);


        // Uppgift-4-Query med Aggregation
        // Skriv en query som kan returnera max salary från tutor tabellen.

        int maxSalary = (Integer) em.createQuery("select max(salary) from " +
                "Tutor t").getSingleResult();
        System.out.println("The maximum tutor salary is " + maxSalary + " SEK");


        // Uppgift-5- NamedQuery
        // Skriv en named query som kan returnera alla tutor som har en lön högre än 10 000.

        // In orm.xml:
        // <named-query name="salariesAboveTenThousand">
        //      <query>from Tutor as t where t.salary> :salary</query>
        // </named-query>

        List<Tutor> result5 = em.createNamedQuery("salariesAboveTenThousand",
                Tutor.class).setParameter("salary", 10000).getResultList();
        for (Tutor tutor : result5) {
            System.out.println(tutor + " has a salary above 10000 SEK");
        }


        tx.commit();
        em.close();
    }

    public static void setUpData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        Subject mathematics = new Subject("Mathematics", 2);
        Subject science = new Subject("Science", 2);
        Subject programming = new Subject("Programming", 3);
        em.persist(mathematics);
        em.persist(science);
        em.persist(programming);

        Tutor t1 = new Tutor("ABC123", "Johan Smith", 40000);
        t1.addSubjectsToTeach(mathematics);
        t1.addSubjectsToTeach(science);


        Tutor t2 = new Tutor("DEF456", "Sara Svensson", 20000);
        t2.addSubjectsToTeach(mathematics);
        t2.addSubjectsToTeach(science);

        // This tutor is the only tutor who can teach History
        Tutor t3 = new Tutor("GHI678", "Karin Lindberg", 0);
        t3.addSubjectsToTeach(programming);

        em.persist(t1);
        em.persist(t2);
        em.persist(t3);


        t1.createStudentAndAddtoTeachingGroup("Jimi Hendriks", "1-HEN-2019", "Street 1", "city 2", "1212");
        t1.createStudentAndAddtoTeachingGroup("Bruce Lee", "2-LEE-2019", "Street 2", "city 2", "2323");
        t3.createStudentAndAddtoTeachingGroup("Roger Waters", "3-WAT-2018", "Street 3", "city 3", "34343");

        tx.commit();
        em.close();
    }


}
