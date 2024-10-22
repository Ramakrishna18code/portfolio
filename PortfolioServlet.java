import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection parameters
    String url = "jdbc:mysql://localhost:3306/portfoliodb";  // Replace `employ` with your database name
    String user = "root";  // Database username
    String password = "bankingsystem@12";  // Database password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form data
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String profession = request.getParameter("profession");
        String email = request.getParameter("email");
        String location = request.getParameter("location");
        String bio = request.getParameter("bio");
        String[] skills = request.getParameterValues("skills[]");
        String[] skillLevels = request.getParameterValues("skillLevels[]");
        String[] projectTitles = request.getParameterValues("projectTitles[]");
        String[] projectLinks = request.getParameterValues("projectLinks[]");
        String githubUrl = request.getParameter("githubUrl");
        String linkedinUrl = request.getParameter("linkedinUrl");
        String template = request.getParameter("template");

        // Store data into database
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection con = DriverManager.getConnection(url, user, password);

            // Insert portfolio information into database
            String query = "INSERT INTO portfolios(username, fullName, profession, email, location, bio, githubUrl, linkedinUrl, template) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, profession);
            ps.setString(4, email);
            ps.setString(5, location);
            ps.setString(6, bio);
            ps.setString(7, githubUrl);
            ps.setString(8, linkedinUrl);
            ps.setString(9, template);

            int rowsAffected = ps.executeUpdate();
            
            // Insert skills
            if (skills != null && skillLevels != null) {
                for (int i = 0; i < skills.length; i++) {
                    String skillQuery = "INSERT INTO portfolio_skills(username, skillName, skillLevel) VALUES(?, ?, ?)";
                    PreparedStatement skillPs = con.prepareStatement(skillQuery);
                    skillPs.setString(1, username);
                    skillPs.setString(2, skills[i]);
                    skillPs.setString(3, skillLevels[i]);
                    skillPs.executeUpdate();
                }
            }

            // Insert projects
            if (projectTitles != null && projectLinks != null) {
                for (int i = 0; i < projectTitles.length; i++) {
                    String projectQuery = "INSERT INTO portfolio_projects(username, projectTitle, projectLink) VALUES(?, ?, ?)";
                    PreparedStatement projectPs = con.prepareStatement(projectQuery);
                    projectPs.setString(1, username);
                    projectPs.setString(2, projectTitles[i]);
                    projectPs.setString(3, projectLinks[i]);
                    projectPs.executeUpdate();
                }
            }

            if (rowsAffected > 0) {
                out.println("<h3>Portfolio successfully created and stored in the database!</h3>");
            } else {
                out.println("<h3>Error: Unable to store portfolio data.</h3>");
            }

            ps.close();
            con.close();
        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
