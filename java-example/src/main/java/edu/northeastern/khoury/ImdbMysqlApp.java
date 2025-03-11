package edu.northeastern.khoury;

import java.sql.*;
// See JavaDoc for java.sql package:
//    https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/package-summary.html

import java.util.Scanner;

/**
 * Sample Java program to demonstrate use of JDBC to connect to a MySQL DB,
 * send queries, and get results.
 */
public class ImdbMysqlApp {
    public static final String MOVIE_MEMENTO_QUERY =
            "SELECT year_released, ranking FROM movies WHERE name='Memento'";

    /**
     * A prepared statement to search movies given a ranking and a range of years.
     */
    public static final String SEARCH_MOVIES_QUERY =
            "SELECT name, year_released, ranking FROM movies WHERE ranking >= ? AND year_released BETWEEN ? AND ?";

    /**
     * The MySQL JDBC connection URL
     */
    public static final String MYSQL_IMDB_URL = "jdbc:mysql://localhost:3307/imdb";
    public static final String MYSQL_IMDB_USER = "imdb.manager";

    /**
     * This is clearly not secured! A good way to manage the MySQL password to use
     * is by setting an environment variable and have the program load them. There
     * are much better ways to do this with web applications!
     */
    public static final String MYSQL_IMDB_PWD = "bad-clear-text-password";

    public static void main(String[] args) throws Exception {
        //create connection for a server installed in localhost, with a user "root" with no password
        System.out.println("Acquiring connection to database...");
        try (Connection conn = DriverManager.getConnection(
                MYSQL_IMDB_URL, MYSQL_IMDB_USER, MYSQL_IMDB_PWD)) {
            // create a Statement
            try (Statement stmt = conn.createStatement()) {
                //execute query
                System.out.println("Executing query...");
                try (ResultSet rs = stmt.executeQuery(MOVIE_MEMENTO_QUERY)) {
                    System.out.println("Fetching the movie information");
                    //position result cursor to first one
                    rs.next();
                    System.out.printf("year released: %d, ranking = %d\n",
                            rs.getInt(1),
                            rs.getInt(2)); //result is "Hello World!"
                } // TODO: Catch exceptions
            } // TODO: Catch exceptions

            try (PreparedStatement searchMovies = conn.prepareStatement(SEARCH_MOVIES_QUERY)) {
                Scanner userInput = new Scanner(System.in);

                System.out.print("Enter two years creating a range to movies released in the range: ");
                int lowerBound = userInput.nextInt();
                int upperBound = userInput.nextInt();
                System.out.print("Enter the minimum ranking of movies to retrieve: ");
                float  minimumRanking = userInput.nextFloat();

                searchMovies.setFloat(1, minimumRanking);
                searchMovies.setInt(2, lowerBound);
                searchMovies.setInt(3, upperBound);

                ResultSet moviesFound = searchMovies.executeQuery();

                System.out.println("The following movies were found:");
                System.out.printf("%30s %8s %8s%n", "Movie", "Year", "Rank");

                System.out.println(String.format("%50s", "").replace(" ", "-"));
                while (moviesFound.next()) {
                    String movieName = moviesFound.getString("name");
                    int year = moviesFound.getInt("year_released");
                    float ranking = moviesFound.getFloat("ranking");

                    System.out.printf("%30s %8d %7.1f%n", movieName, year, ranking);
                }
            } // TODO: Exceptions should be caught!

            conn.close();
        } // TODO: Catch exceptions
    }
}