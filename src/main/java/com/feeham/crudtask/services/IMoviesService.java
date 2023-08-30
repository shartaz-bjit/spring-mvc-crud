package com.feeham.crudtask.services;
import com.feeham.crudtask.models.Movie;
import java.util.Date;
import java.util.List;

public interface IMoviesService {

    // CRUD methods
    List<Movie> getAllMovies();
    Movie getMovieById(int id);
    void addMovie(Movie movie);
    void updateMovie(int id, Movie movie);
    void deleteMovie(int id);

    // Additional methods
    List<Movie> filter(String category, String genre, int releaseYear);
    List<Movie> sort(boolean dateOfRelease, boolean alphabetic, boolean asc);
    List<Movie> search(String text);
}