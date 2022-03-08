package com.example.venteran;

import com.example.venteran.Notification.MyResponse;
import com.example.venteran.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAKjcGLrc:APA91bEEskzdOIlRbPrFw3ovaBPvwc2kJYd7UrCCMevz1J-b_WunTO3QyD4j6i5pyJhiq6SSmMSkoyZW2KxFXR6w1VBv1kZOFf0koRYUCB6D6cJLXx-uXEJ6GiIcQEbgRBQvFPm9Oacq"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
