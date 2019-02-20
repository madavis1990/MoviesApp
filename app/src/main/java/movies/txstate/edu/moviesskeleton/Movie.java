package movies.txstate.edu.moviesskeleton;

public class Movie {
    //The attributes in the directions list the attributes and tyh
    private int id;
    private String title;
    private String rating;
    private String releaseYear;
    private String overview;
    private String posterPath;


    public Movie(int id, String title, String rating, String releaseYear, String description, String posterPath) {
        super();
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.overview = description;
        this.posterPath = posterPath;
    }
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getRating() {
        return rating;
    }
    public String getReleaseYear() {
        return releaseYear;
    }
    public String getOverview() {
        return overview;
    }
    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", rating=" + rating + ", releaseYear=" + releaseYear
                + ", overview=" + overview + ", posterPath=" + posterPath + "]";
    }


}
