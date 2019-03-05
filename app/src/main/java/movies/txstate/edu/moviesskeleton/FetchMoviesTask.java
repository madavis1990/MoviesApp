package movies.txstate.edu.moviesskeleton;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;



/**
 * The asynchronous task that fetches movie information from themoviedb.org database
 * via a RESTful interface.
 *
 * An ArrayList of Movies are returned if successful.
 */
public class FetchMoviesTask extends
        AsyncTask<String, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getCanonicalName();



    public FetchMoviesTask () {

    }


    @Override
    protected ArrayList<Movie> doInBackground(
            String... urlArgs) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader        = null;

        // Will contain the raw JSON response as a string.

        try {
            // Construct the URL for the themoviedb.org query
            // Possible parameters are available at api.themoviedb.org

            URL url = Utils.buildMoviesURL();
            Log.v(LOG_TAG, "Build URI: " + url);

            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do. Make a note in the log and return null
                Log.v(LOG_TAG, "No values returned from themovedb invocation");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            String moviesJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Movies JSON String " + moviesJsonStr);
            return Utils.parseMoviesFromJSONString(moviesJsonStr);
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeQuietly(urlConnection);
            Utils.closeQuietly(reader);
        }
        return null;
    }

    /**
     * @param movies The movies returned from the RESTful call
     */
    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);

        PopularMovies popularMovies = PopularMovies.getInstance();

        Iterator<Movie> itr = movies.iterator();
        while (itr.hasNext()) {
            popularMovies.add(itr.next());
        }
    }

}

