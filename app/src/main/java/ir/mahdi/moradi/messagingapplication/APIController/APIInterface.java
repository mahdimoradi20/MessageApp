package ir.mahdi.moradi.messagingapplication.APIController;

import androidx.core.app.NotificationCompat;

import ir.mahdi.moradi.messagingapplication.Utils.ServerConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @FormUrlEncoded
    @POST("addUser/apikey={apikey}")
    Call<ResponseBody> addUser(@Path("apikey") String apikey,
                               @Field("name") String name,
                               @Field("username")String username,
                               @Field("password")String password,
                               @Field("fcmtoken") String fcmToken,
                               @Field("phonenumber")String phoneNumber,
                               @Field("avatarurl")String avatarUrl,
                               @Field("biography") String biography);

    @FormUrlEncoded
    @POST("authUser/apikey={apikey}")
    Call<ResponseBody> authUser(@Path("apikey") String apikey,
                               @Field("username")String username,
                               @Field("password")String password);

    @GET("getAllChats/apikey={apikey}/uid={userid}")
    Call<ResponseBody> getAllChats(@Path("apikey") String apikey , @Path("userid")String userid);

    @GET("getMessages/apikey={apikey}/chatid={chatid}")
    Call<ResponseBody> getAllMessages(@Path("apikey") String apikey , @Path("chatid")String chatid);

    @FormUrlEncoded
    @POST("addMessage/apikey={apikey}")
    Call<ResponseBody> addMessage(@Path("apikey") String apikey ,
                                  @Field("content") String content,
                                  @Field("chatid") String chatid,
                                  @Field("userid") String userid);

    @FormUrlEncoded
    @POST("addChat/apikey={apikey}")
    Call<ResponseBody> addChat(@Path("apikey") String apikey,
                               @Field("name")String name,
                               @Field("avatar")String avatar);

    @FormUrlEncoded
    @POST("addUserChat/apikey={apikey}")
    Call<ResponseBody> addUserChat(@Path("apikey") String apikey ,
                                   @Field("userid")String userid ,
                                   @Field("chatid")String chatid);

    @FormUrlEncoded
    @POST("deleteUserChats/apikey={apikey}")
    Call<ResponseBody> deleteUserChats(@Path("apikey") String apikey,
                                       @Field("userid") String userid,
                                       @Field("chatids")String chatIds);
}
