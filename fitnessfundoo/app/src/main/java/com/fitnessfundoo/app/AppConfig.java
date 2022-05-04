package com.fitnessfundoo.app;

public class AppConfig {

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;


    // Server user login url
    public static String URL_LOGIN = "https://www.fitnessfundoo.in/android_login_api/login_android.php";

    public static String URL_FB_LOGIN = "https://www.fitnessfundoo.in/android_login_api/fb_login_android.php";

    public static String URL_GOOGLE_LOGIN = "https://www.fitnessfundoo.in/android_login_api/google_login_android.php";

    // Server user register url
    public static String URL_REGISTER = "https://www.fitnessfundoo.in/android_login_api/register_android.php";

    // Server user update url
    public static String URL_UPDATE_INTEREST = "https://www.fitnessfundoo.in/android_login_api/update_interest.php";

    public static String URL_FACILITY = "https://www.fitnessfundoo.in/android_login_api/fitness_facility.php";

    public static String URL_EVENTS = "https://www.fitnessfundoo.in/android_login_api/sports_events.php";

    public static String URL_RATING = "https://www.fitnessfundoo.in/android_login_api/rating_review.php";

    public static String URL_COUNT_REVIEW = "https://www.fitnessfundoo.in/android_login_api/review_count.php";

    public static String URL_REVIEW_DETAIL = "https://www.fitnessfundoo.in/android_login_api/review_detail.php";

    public static String  URL_FACILITY_ID = "https://www.fitnessfundoo.in/android_login_api/facility_id.php";

    public static String  URL_CREATE_EVENT = "https://www.fitnessfundoo.in/android_login_api/create_event.php";

    public static String  URL_FEEDBACK = "https://www.fitnessfundoo.in/android_login_api/feedback.php";

    public static String URL_USER_FOLLOWERS = "https://www.fitnessfundoo.in/android_login_api/user_followers.php";

    public static String URL_USER_FOLLOWINGS = "https://www.fitnessfundoo.in/android_login_api/user_followings.php";

    public static String  URL_HOST_DETAIL = "https://www.fitnessfundoo.in/android_login_api/host_detail.php";

    public static String  URL_USER_COUNT = "https://www.fitnessfundoo.in/android_login_api/joined_user_count.php";

    public static String  URL_JOINED_USER = "https://www.fitnessfundoo.in/android_login_api/joined_user_detail.php";
//https://www.fitnessfundoo.in/android_login_api/joined_user_detail.php?event_id=52

    public static String  URL_UPDATE_FAV = "https://www.fitnessfundoo.in/android_login_api/update_favourite.php";

    public static String  URL_SEARCH_RESULTS = "https://www.fitnessfundoo.in/android_login_api/search.php";

    public static String  URL_EDIT_EVENT = "https://www.fitnessfundoo.in/android_login_api/edit_event.php";

    public static String  URL_DELETE_EVENT = "https://www.fitnessfundoo.in/android_login_api/delete_event.php";

    public static String  URL_INTEREST_LEVEL = "https://www.fitnessfundoo.in/android_login_api/interest_level.php";

    public static String  URL_PROFILE_UPDATE = "https://www.fitnessfundoo.in/android_login_api/update_profile.php";

    public static String  URL_UPDATE_DATA = "https://www.fitnessfundoo.in/android_login_api/update_profile_data.php";

    public static String URL_UPDATE_FOLLOWERS = "https://www.fitnessfundoo.in/android_login_api/update_followers.php";

    public static String URL_REMOVE_FOLLOW = "https://www.fitnessfundoo.in/android_login_api/remove_follow.php";

    public static String  URL_UPDATE_STAR = "https://www.fitnessfundoo.in/android_login_api/update_star.php";

    public static String  URL_FAVORITE_COUNT = "https://www.fitnessfundoo.in/android_login_api/favorite_count.php";

    public static String URL_FAV_FACILITY = "https://www.fitnessfundoo.in/android_login_api/favorite_facilities.php";

    public static String URL_LOAD_EVENT_NEARBY = "https://www.fitnessfundoo.in/android_login_api/load_nearby_event.php";

    public static String URL_LOAD_FACILITY_NEARBY = "https://www.fitnessfundoo.in/android_login_api/load_nearby_facility.php";

    public static String URL_LOAD_MY_EVENT  = "https://www.fitnessfundoo.in/android_login_api/load_my_event.php";

    public static String URL_LOAD_FAV_EVENT  = "https://www.fitnessfundoo.in/android_login_api/load_fav_event.php";

    public static String URL_UPDATE_GCM_ID  = "https://www.fitnessfundoo.in/android_login_api/update_gcm_id.php";

}
