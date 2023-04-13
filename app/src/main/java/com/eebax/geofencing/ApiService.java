package com.eebax.geofencing;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
//    https://run.mocky.io/v3/e36507c9-86b0-4ee1-8982-0765c1486d6e
    @GET("e36507c9-86b0-4ee1-8982-0765c1486d6e")
    Call<PolygonModel> getAllPolygon();

}
