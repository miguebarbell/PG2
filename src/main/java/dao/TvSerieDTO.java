package dao;

public record TvSerieDTO(int id, String name, String overview,
                         String firstAirDate,
                         int numberOfSeasons, int numberOfEpisodes) {

}
