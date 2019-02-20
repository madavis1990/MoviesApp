package movies.txstate.edu.moviesskeleton;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PopularMovies {
    private static PopularMovies instance=null;
    private ArrayList<Movie> movies = new ArrayList<>();
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    private PopularMovies() {

    }
    public static PopularMovies getInstance() {
        if(instance==null) {
            instance = new PopularMovies();
        }
        return instance;
    }
    public void add(Movie movie) {
        LOGGER.info ("movie added");
        movies.add(movie);
    }
    public Movie getMovie(int index) {
        return movies.get(index);
    }
    public ArrayList<Movie> getMovies(int index) {
        return movies;
    }
    public int size() {
        return movies.size();
    }
    public void clear() {
        movies.clear();
        return;
    }
}
