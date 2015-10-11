package rohan.rydo.com.rydofb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Login extends ActionBarActivity {

    CallbackManager callbackManager;
    LoginButton login;
    String TAG = "FACEBOOKSDK";
    public static String id, name, hometown, location, birthday, friend_count;
    public static int age;
    public static ArrayList<String> institution = new ArrayList<>(), type = new ArrayList<>(), friends_list = new ArrayList<>(), position = new ArrayList<>(), workplace = new ArrayList<>();
    boolean friend_gotData = false, info_gotData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing the facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton) findViewById(R.id.login_button);

        // set the required permissions
        login.setReadPermissions("public_profile email user_friends user_birthday user_education_history " +
                "user_hometown user_work_history user_location");

        if (AccessToken.getCurrentAccessToken() != null) {
            RequestData();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    // do something
                }
            }
        });

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }


        });
    }

    private void RequestData() {

        // to get the count of the friends and the list of friends using the app
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d(TAG, "friends count plus list " + response.getJSONObject());
                            JSONObject resp = new JSONObject("" + response.getJSONObject());
                            JSONArray data = resp.getJSONArray("data");
                            JSONObject summary = resp.getJSONObject("summary");
                            friend_count = summary.getString("total_count");
                            Log.d(TAG, "" + friend_count);

                            JSONObject friends;
                            for (int i = 0; i < data.length(); i++) {
                                friends = data.getJSONObject(i);
                                friends_list.add(friends.getString("name"));
                            }
                            Log.d(TAG, "friends test " + friends_list);
                            friend_gotData = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (friend_gotData && info_gotData) {
                            goToNext();
                        }
                    }
                }
        ).executeAsync();


        // to get basic information of the user
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                Log.d(TAG, "" + jsonObject);
                String data = jsonObject.toString();
                Log.d(TAG, "full data json" + data);
                try {
                    id = jsonObject.getString("id");
                    birthday = jsonObject.getString("birthday");
                    birthday.replace("\\", "");
                    age = getAge(birthday);

                    JSONArray work = new JSONArray(jsonObject.getString("work"));
                    for (int i = 0; i < work.length(); i++) {
                        JSONObject working = work.getJSONObject(i);
                        position.add(new JSONObject(working.getString("position")).getString("name"));
                        workplace.add(new JSONObject(working.getString("employer")).getString("name"));
                    }
                    hometown = new JSONObject(jsonObject.getString("hometown")).getString("name");
                    JSONArray education = new JSONArray(jsonObject.getString("education"));

                    for (int i = 0; i < education.length(); i++) {
                        JSONObject edu = education.getJSONObject(i);
                        type.add(edu.getString("type"));
                        JSONObject school = new JSONObject(edu.getString("school"));
                        institution.add(school.getString("name"));
                    }
                    location = new JSONObject(jsonObject.getString("location")).getString("name");
                    name = jsonObject.getString("name");
                    info_gotData = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "id " + id + " name " + name + " birthday " + birthday + " hometown " + hometown + " location " + location + " type " + type + " institution " + institution);
                if (friend_gotData && info_gotData) {
                    goToNext();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,age_range,location,work,birthday,hometown,education");
        request.setParameters(parameters);
        request.executeAsync();


    }

    void goToNext() {
        Intent intent = new Intent(this, Details.class);
        startActivity(intent);
    }

    public static int getAge(String dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        birthDate.setTime(convertedDate);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of
        // leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR)
                - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of
            // month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (birthDate.get(Calendar.DAY_OF_MONTH) > today
                .get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}