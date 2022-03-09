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
                    "Authorization:key=AAAAKjcGLrc:APA91bFT-Eiow50QKvqUEoMrixtEJl7HjH_gpqVvG-3rJL7aT2Q8dIfZqhP6bKtBHgajyqHHbdjF8IqQFTb-_FWIUWp3ifyXjZTlrwn1oDic7WybIwTnHpBIrnwhYq3WpimcVmuffrSF"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
