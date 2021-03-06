package com.vitoling.cloudmusictv.data.source

import com.google.gson.GsonBuilder
import com.vitoling.cloudmusictv.data.model.LoginResponse
import com.vitoling.cloudmusictv.data.model.song.enhance.player.SongUrlResponse
import com.vitoling.cloudmusictv.data.model.user.PlaylistResponse
import com.vitoling.cloudmusictv.data.model.v3.playlist.PlaylistDetailResponse
import com.vitoling.cloudmusictv.data.model.v3.song.SongDetailResponse
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.URI


/**
 * Created by lingzhiyuan.
 * Date : 2/24/17.
 * Time : 14:57.
 * Description:
 *
 */

private const val COOKIE_DOMAIN = "http://music.163.com"
private const val BASE_API_URL = "http://music.163.com/weapi/"
private const val HEADER_ORIGIN = "Origin"
private const val HEADER_ORIGIN_VALUE = "http://music.163.com"
private const val HEADER_REFERER = "Referer"
private const val HEADER_REFERER_VALUE = "http://music.163.com/"
private const val HEADER_USER_AGENT = "User-Agent"
private const val HEADER_USER_AGENT_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
private const val HEADER_HOST = "Host"
private const val HEADER_HOST_VALUE = "music.163.com"
private const val HEADER_CONTENT_TYPE = "Content-Type"
private const val HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
private const val COOKIE_APP_VER = "appver"
private const val COOKIE_APP_VER_VALUE = "2.7.1"

internal interface CloudMusicAPI {

    companion object {

        private val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create()

        // 通用请求头信息
        private val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    .header(HEADER_ORIGIN, HEADER_ORIGIN_VALUE)
                    .header(HEADER_HOST, HEADER_HOST_VALUE)
                    .header(HEADER_REFERER, HEADER_REFERER_VALUE)
                    .header(HEADER_USER_AGENT, HEADER_USER_AGENT_VALUE)
                    .header(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                    .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        // 设置cookie
        private val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            // add a basic cookie "appver"
            // cookies will update automatically after each call
            cookieStore.add(URI(BASE_API_URL), HttpCookie(COOKIE_APP_VER, COOKIE_APP_VER_VALUE).apply {
                domain = COOKIE_DOMAIN
            })
        }

        val CLOUD_MUSIC_API_ADAPTER: Retrofit by lazy {
            // Build retrofit
            Retrofit.Builder().run {
                addConverterFactory(GsonConverterFactory.create(gson))
                baseUrl(BASE_API_URL)
                client(OkHttpClient.Builder().run {
                    addInterceptor(headerInterceptor)
                    cookieJar(JavaNetCookieJar(cookieManager))
                    // build okhttp client
                    build()
                })
                build()
            }
        }
    }

    /**
     * 账号登录
     * */
    @POST("login")
    fun login(@Body body: RequestBody): Call<Any>

    /**
     * 手机号登录
     * */
    @POST("login/cellphone")
    fun login_cellphone(@Body body: RequestBody): Call<LoginResponse>

    @POST("feedback/weblog")
    fun feedback_weblog(@Body body: RequestBody): Call<Map<String, Any>>

    /**
     * get captcha
     * */
    @GET("/captcha?id={id}")
    fun captcha(@Path("id") id: String): Call<ResponseBody>

    /**
     * get playlists
     * */
    @POST("user/playlist")
    fun user_playlist(@Body body: RequestBody): Call<PlaylistResponse>

    /**
     * get playlist detail
     * */
    @POST("v3/playlist/detail")
    fun v3_playlist_detail(@Body body: RequestBody): Call<PlaylistDetailResponse>

    /**
     * Get song detail
     * */
    @POST("v3/song/detail")
    fun v3_song_detail(@Body body: RequestBody): Call<SongDetailResponse>

    @POST("song/enhance/player/url")
    fun song_enhance_player_url(@Body body: RequestBody): Call<SongUrlResponse>

    /**
     *
     * */
    @POST("user/getfollows/{id}")
    fun user_getfollows(@Path("id") id: Long, @Body body: RequestBody): Call<PlaylistResponse>


}