import mysql.connector

# See MySQL Connector/Python Developer Guide
#   https://dev.mysql.com/doc/connector-python/en/
# Specifically, the coding examples
#   https://dev.mysql.com/doc/connector-python/en/connector-python-examples.html

conn = mysql.connector.connect(
    user="imdb.manager",
    password="bad-clear-text-password",  # clearly, not secured! See Java example for best practices
    host="localhost",
    port=3307,
    database="imdb"
)

MOVIE_MEMENTO_QUERY = \
    "SELECT year_released, ranking FROM movies WHERE name= 'Memento'"
SEARCH_MOVIES_QUERY = \
    ("SELECT name, year_released, ranking FROM movies " +
     "WHERE ranking >= %s AND year_released BETWEEN %s AND %s")

# Note the use of %s above, that is the placeholder for values to
# be bound later. Use %s regardless of the data type of the columns.

if conn and conn.is_connected():
    cursor = conn.cursor()

    # retrieving information
    cursor.execute(MOVIE_MEMENTO_QUERY)

    movie = cursor.fetchone()
    # movie is a Python tuple
    print(f"Year released: {movie[0]}, rank: {movie[1]}")

    print("Enter two years creating a range to movies released in the range: ")
    lower_bound = int(input())
    upper_bound = int(input())
    print("Enter the minimum rank of movies to retrieve: ")
    minimum_rank = float(input())

    cursor.execute(SEARCH_MOVIES_QUERY, (minimum_rank, lower_bound, upper_bound))

    print("The following movies were found:")
    print("{:30s}{:>8s}{:>8s}".format("Movie", "Year", "Rank"))
    print('-'*50)
    for (movie, year, rank) in cursor:
        print("{a:30s}{b:8d}{c:7.1f}".format(a=movie, b=year, c=rank))

    # Always close the connection
    conn.close()
else:
    print("Failed to connect to MySQL database")
