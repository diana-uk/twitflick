package com.diana_ukrainsky.twitflick.data;

import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.utils.DataSP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ReviewsDao {
    private static final String REVIEWS_KEY = "REVIEW_LIST";

    public ReviewsDao() {
    }

    public List<ReviewData> readReviews() {
        List<ReviewData> reviews;
        DataSP dataSP = DataSP.getInstance ();
        String json = DataSP.getInstance().getString(REVIEWS_KEY,null);
        if (json !=null){
            reviews = new Gson().fromJson(json, new TypeToken<List<ReviewData>> (){}.getType());
        }else{
            reviews = new ArrayList<> ();
        }
        return reviews;
    }

    public void saveReviews(List<ReviewData> reviews) {
        String json = new Gson().toJson(reviews);
        DataSP.getInstance().putString(REVIEWS_KEY,json);
    }
}
