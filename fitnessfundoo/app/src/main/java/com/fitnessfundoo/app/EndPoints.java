package com.fitnessfundoo.app;

/**
 * Created by Anubhav on 20-04-2016.
 */
public class EndPoints {

    // localhost url -
    public static final String BASE_URL = "https://www.fitnessfundoo.in/android_login_api/v1";
    public static final String ADD_IN_CHAT_ROOM = BASE_URL + "/user/addInChatRoom";
    public static final String CREATE_CHAT_ROOM = BASE_URL + "/create/ChatRoom";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String JOIN_CHAT_ROOM = BASE_URL + "/create/JoinChatRoom";

    public static String  URL_JOIN_EVENT ="https://www.fitnessfundoo.in/android_login_api/join_event.php";
}