package movies.txstate.edu.moviesskeleton;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A Utility class where general purpose methods reside and constants that are used
 * in the rest of the application.
 */
public class Utils {

    private static String TAG = Utils.class.getSimpleName();

    // End point URI's for fetching movies and posters;
    private static final String URI_MOVIE_BASE        = "https://api.themoviedb.org/3/movie/popular";
    private static final String URI_IMAGE_BASE        = "http://image.tmdb.org/t/p/";

    // Recommended image size for poster
    private static final String IMAGE_SIZE = "w185";

    // API Key for themoviedb.org request
    private static final String API_MOVIE_KEY = "ca2835a4ddfe46df8d93b4c22ac6a340";

    // Arguments to themoviedb.org RESTful interfaces
    private static final String API_KEY            = "api_key";

    // The number of movies returned per page
    public static  int MOVIE_PAGE_SIZE        = 20;

    // Movie rating from 0 - 10; Rating is displayed as a fraction. e.g.,  <rating> / 10
    public static final String MAXIMUM_MOVIE_RATING = "10";

    // size for movie posters in detail movie view
    public static final  int    IMAGE_SIZE_WIDTH       = 600;
    public static final  int    IMAGE_SIZE_HEIGHT      = 825;

    private Utils() {
    }

    /**
     * This method defines an initial movie to put into the singleton PopularMovies so
     * that there is an image to display when the application starts and so that
     * PopularMovies is never null or empty.
     *
     * The actual movies will be fetched and parsed in FetchMoviesTask.
     */
    public static void addFirstMovie () {

         PopularMovies popularMovies = PopularMovies.getInstance();
          String description = "Thirty years after the events of the first film, a new blade runner, LAPD Officer K, unearths a long-buried secret that has the potential to plunge what's left of society into chaos. K's discovery leads him on a quest to find Rick Deckard, a former LAPD blade runner who has been missing for 30 years.";
         Movie movie = new Movie (335984, "Blade Runner 2049", "7.3", "2017", description,
                 "/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg");
         popularMovies.add(movie);
    }

    /**
     * Construct the URL for the themoviedb.org query
     *  Possible parameters are available at api.themoviedb.org
     *  https://api.themoviedb.org/3/movie/<sort order>?api_key=####
     * @return URL the URL to access the movies in the specified order
     * @throws MalformedURLException Bad URL
     */
    public static URL buildMoviesURL () throws MalformedURLException {
        Uri uri = Uri.parse(URI_MOVIE_BASE).buildUpon().
                appendQueryParameter(API_KEY, Utils.getAPIMovieKey()).
                build();
        URL url = new URL(uri.toString());
        Log.v(TAG, "Build Movie URL: " + url);

        return url;
    }

    /**
     * Builds the URL for the movie poster image from the image id. If the image is
     * null, returns a URL that will display as "No Image Available"
     * @param imageId: the imageId as retrieved from themoviedb.org
     * @return URL the full URL for the image
     * @throws MalformedURLException Bad URL
     */
    public static URL buildMovieImageURL(String imageId) throws MalformedURLException {
        // Compose the URL for the accessing the movie's image
        if (imageId == null) {
            Log.v(TAG, "Null movie image ");
            Log.v(TAG, "Returning " + missingPoster());
            return new URL (missingPoster());
        }
        String url_base = URI_IMAGE_BASE + IMAGE_SIZE + "/" + imageId;
        Uri uri = Uri.parse(url_base).buildUpon().
                appendQueryParameter(API_KEY, Utils.getAPIMovieKey()).
                build();
        URL url = new URL(uri.toString());
        Log.v(TAG, "Build Image URL: " + url);
        return url;
    }

    // Disconnect from the URL quietly;
    public static void closeQuietly(HttpURLConnection urlConnection) {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
    }

    // Close the reader stream quietly;
    public static void closeQuietly(BufferedReader stream) {
        if (stream != null) {
            try {
                stream.close();
            }
            catch (final IOException e) {
                Log.e("Utils.closeQuietly", "Error closing stream", e);
            }
        }
    }


    /**
     * Extracts the movie information from the downloaded JSON String
     * @param moviesJsonString JSON formatted string of movie information
     * @return ArrayList<Movie> The list of movies extracted as objects
     * @throws JSONException Some problem with JSON
     */
    public static ArrayList<Movie> parseMoviesFromJSONString(
            String moviesJsonString) throws
            JSONException {
        final String RESULTS               = "results";
        final String MOVIE_ID              = "id";
        final String MOVIE_TITLE           = "title";
        final String RATING                = "vote_average";
        final String RELEASE_DATE          = "release_date";
        final String OVERVIEW              = "overview";
        final String POSTER_PATH           = "poster_path";


        JSONObject moviesJson              = new JSONObject(moviesJsonString);
        JSONArray  moviesArray             = moviesJson.getJSONArray(RESULTS);
        ArrayList<Movie> movies            = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject jMovie = moviesArray.getJSONObject(i);
            // Extract the movie details from the JSON object
            int id                = jMovie.getInt(MOVIE_ID);
            String title          = jMovie.getString(MOVIE_TITLE);
            String rating         = jMovie.getString(RATING);
            String releaseYear    = extractReleaseYear(jMovie.getString(RELEASE_DATE));
            String overview       = jMovie.getString(OVERVIEW);
            String posterPath     = jMovie.getString(POSTER_PATH);
            if (posterPath.equals("null")) posterPath = null;

            // Create a new movie instance and add it to the movies to be returned
            Movie movie = new Movie(id, title, rating, releaseYear, overview, posterPath);
            movies.add(movie);

        }
        return movies;
    }

    /**
     * If a movie does not have a poster, return this image instead. Not ideal,
     * but better than nothing
     * @return URL of a poster that says "No Image"
     */
    private static String missingPoster () {
        return "http://comicbookmoviedatabase.com/wp-content/uploads/2014/04/no-poster-available-336x500.jpg";
    }


    /**
     * Return the api key that stored as a constant in this class.
     * @return the API Key
     */
    private static String getAPIMovieKey() {
        return API_MOVIE_KEY;
    }

    /**
     * Extract the release year from the larger release date, if a date is present.
     * If not, return a "?"
     * @param releaseDate either a fully qualified date with YYYY-MM-DD or null
     * @return the year YYYY
     */
    private static String extractReleaseYear (String releaseDate) {
        final int    RELEASE_YEAR_START    = 0;
        final int    RELEASE_YEAR_LENGTH   = 4;
        String releaseYear;
        if ((releaseDate != null) && (releaseDate.length() >= RELEASE_YEAR_LENGTH)) {
            releaseYear = releaseDate.substring(RELEASE_YEAR_START, RELEASE_YEAR_LENGTH);
        } else {
            releaseYear = "?";
        }
        return releaseYear;
    }


}

