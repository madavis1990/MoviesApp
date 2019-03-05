package movies.txstate.edu.moviesskeleton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.logging.Logger;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG             = MainActivity.class.getSimpleName();

    private static final Logger LOGGER = Logger.getAnonymousLogger();
    private Button nextButton;
    private Button backButton;
    private ImageView movieImage;
    private TextView movieTitle;
    private TextView releaseYear;
    private TextView rating;
    private TextView description;
    private int currentIndex = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.addFirstMovie();
        new FetchMoviesTask().execute();
        movieTitle  = findViewById(R.id.movie_title);
        releaseYear  = findViewById(R.id.release_year);
        rating  = findViewById(R.id.rating);
        description  = findViewById(R.id.movie_description);
        movieImage  = findViewById(R.id.movie_image);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        updateMovie(true);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovie(true);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovie(false);
            }
        });
    }
    private void updateMovie(boolean isNext){
        PopularMovies popularMovies = PopularMovies.getInstance();
        Movie movie = null;
        if(isNext){
            currentIndex++;
            //tests if the movie is out of the arraylist bounds, if it is it wraps the list back around
            try {
                movie = popularMovies.getMovie(currentIndex);
            }
            catch (final IndexOutOfBoundsException e) {
                LOGGER.info("out of bounds" + e);
                currentIndex = 0;
                movie = popularMovies.getMovie(currentIndex);
            }
            if(movie instanceof Movie) {
                loadMovieImage(movie, movieImage);
                movieTitle.setText(movie.getTitle());
                System.out.println(movieTitle);
                releaseYear.setText(movie.getReleaseYear());
                rating.setText(movie.getRating() + "/" + Utils.MAXIMUM_MOVIE_RATING);
                description.setText(movie.getOverview());
            }
        }
        else{
            currentIndex--;
            //tests if the movie is out of the arraylist bounds, if it is it wraps the list back around
            try {
                movie = popularMovies.getMovie(currentIndex);
            }
            catch (final IndexOutOfBoundsException e) {
                LOGGER.info("out of bounds" + e);
                currentIndex = Utils.MOVIE_PAGE_SIZE;
                movie = popularMovies.getMovie(currentIndex);
            }
            if(movie instanceof Movie) {
                loadMovieImage(movie, movieImage);
                movieTitle.setText(movie.getTitle());
                releaseYear.setText(movie.getReleaseYear());
                rating.setText(movie.getRating() + "/" + Utils.MAXIMUM_MOVIE_RATING);
                description.setText(movie.getOverview());
            }
        }
    }

    private void loadMovieImage (Movie movie, ImageView view) {

        try {
            URL url = Utils.buildMovieImageURL(movie.getPosterPath());
            Log.v(LOG_TAG, "Movie poster url " + url);
            //Load the image from the URL into imageView
            Picasso.with(this)
                    .load(url.toString())
                    .resize(Utils.IMAGE_SIZE_WIDTH, Utils.IMAGE_SIZE_HEIGHT)
                    .into(view);
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

}
