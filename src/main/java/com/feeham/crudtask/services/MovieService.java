package com.feeham.crudtask.services;

import com.feeham.crudtask.models.Movie;
import com.feeham.crudtask.utilities.MovieBase;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMoviesService{
    // Dummy database instance.
    private final MovieBase movieBase;
    public  MovieService(MovieBase movieBase){
        this.movieBase = movieBase;
    }

    // Return the list of all movies
    @Override
    public List<Movie> getAllMovies() {
        return movieBase.getMovies();
    }

    // Find by id
    @Override
    public Movie getMovieById(int id) {
        return movieBase.getMovies().stream()
            .filter(movie -> movie.getId() == id)
            .findFirst()
            .orElse(null);
    }

    @Override
    public void addMovie(Movie movie) {
        // ID is set to the next possible value based on the lists' size
        movie.setId(movieBase.getMovies().size());
        movieBase.getMovies().add(movie);
    }

    @Override
    public void updateMovie(int id, Movie movie) {
        // Reading the existing movie and updating its information
        Movie existingMovie = getMovieById(id);
        if (existingMovie != null) {
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setDescription(movie.getDescription());
            existingMovie.setGenres(movie.getGenres());
            existingMovie.setCategory(movie.getCategory());
            existingMovie.setReleaseDate(movie.getReleaseDate());
            existingMovie.setDirector(movie.getDirector());
            existingMovie.setActors(movie.getActors());
            existingMovie.setRating(movie.getRating());
            existingMovie.setPosterUrl(movie.getPosterUrl());
        }
    }

    // Delete a movie by id
    @Override
    public void deleteMovie(int id) {
        Movie movieToDelete = getMovieById(id);
        if (movieToDelete != null) {
            movieBase.getMovies().remove(movieToDelete);
        }
    }

    /*
    * param1 - category of movie
    * param2 - genre of movie
    * param3 - year of movie release date
    * return - list of movie satisfying passed filters
    *
    * if any filter param value is null, the filter is not applied
    */
    @Override
    public List<Movie> filter(String category, String genre, int releaseYear) {
        List<Movie> filteredMovies = movieBase.getMovies();

        if (category != null) {
            String lowercaseCategory = category.toLowerCase();
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getCategory().toLowerCase().equals(lowercaseCategory))
                    .collect(Collectors.toList());
        }

        if (genre != null) {
            String lowercaseGenre = genre.toLowerCase();
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getGenres().stream().map(String::toLowerCase).anyMatch(lowercaseGenre::equals))
                    .collect(Collectors.toList());
        }

        if (releaseYear > 0) {
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getReleaseDate().getYear() == releaseYear)
                    .collect(Collectors.toList());
        }
        return filteredMovies;
    }

    /*
    * Allows two types of sorting, based on boolean value then applies the
    * third param value to apply ascending or descending sort
    * exceptional case: if both sort is true only dateOfRelease sort is applied.
    */
    @Override
    public List<Movie> sort(boolean dateOfRelease, boolean alphabetic, boolean asc) {
        List<Movie> movies = movieBase.getMovies();
        if(dateOfRelease){
            if(asc){
                movies.sort(Comparator.comparing(Movie::getReleaseDate));
            }
            else{
                movies.sort(Comparator.comparing(Movie::getReleaseDate).reversed());
            }
        }
        if(alphabetic){
            if(asc){
                Collections.sort(movies, Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
            }
            else{
                Collections.sort(movies, Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
            }
        }
        return movies;
    }

    /*
    * This search method uses model.search() method to search in each movies' multiple parameters
    * After searching in each movie object, it sorts movies based on matched value count
    */
    @Override
    public List<Movie> search(String text) {
        List<Movie> searchResults = new ArrayList<>();
        int maxMatch = 0;
        for (Movie movie : movieBase.getMovies()) {
            int matchValue = movie.match(text);
            if (matchValue > 0) {
                searchResults.add(movie);
            }
            maxMatch = Math.max(maxMatch, matchValue);
        }
        searchResults.sort((movie1, movie2) -> {
            double match1 = movie1.match(text);
            double match2 = movie2.match(text);
            return Double.compare(match2, match1);
        });
        int finalMaxMatch = maxMatch;
        return searchResults.stream().filter(movie -> movie.match(text) == finalMaxMatch).collect(Collectors.toList());
    }
}
