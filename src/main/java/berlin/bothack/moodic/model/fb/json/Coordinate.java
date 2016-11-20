package berlin.bothack.moodic.model.fb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class Coordinate {
    public Double lat;
    @JsonProperty("lang") public Double lng;
}
