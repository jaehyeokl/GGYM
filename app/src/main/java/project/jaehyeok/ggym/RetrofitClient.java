package project.jaehyeok.ggym;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

//public class RetrofitClient {
//    String URL = "https://dapi.kakao.com/";
//
//    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(URL)
//            .build();
//    //.addConverterFactory(GsonConverterFactory.create())
//
//    RetrofitInterface server = retrofit.create(RetrofitInterface.class);
//}

interface RetrofitInterface{
    @Headers("Authorization: KakaoAK 5b69b8285fbd1ab617bdcb6790cc746d")
    @GET("v2/local/search/address.json")
    Call<String> searchAddressJson(@Query("query") String query);
}

