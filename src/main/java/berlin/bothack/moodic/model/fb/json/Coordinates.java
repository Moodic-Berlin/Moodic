package berlin.bothack.moodic.model.fb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class Coordinates {
    public Double lat;
    @JsonProperty("long") public Double lng;
}
