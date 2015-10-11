package rohan.rydo.com.rydofb;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.util.ArrayList;


public class Details extends ActionBarActivity {

    TextView details;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        data = "ID:" + Login.id + "\nNAME:" + Login.name + "\nBirthday:" + Login.birthday + "\nAge:"
                + Login.age + "\nLocation:" + Login.location + "\nHometown:" + Login.hometown + "\nNo. of Friends:" + Login.friend_count;

        data = data + "\n\nWork";
        for(int i =0; i<Login.position.size();i++){
            data = data + "\n" + Login.position.get(i) + " in " + Login.workplace.get(i);
        }
        data = data + "\n\nEducation";
        for(int i =0; i<Login.institution.size();i++){
              data = data + "\n" + Login.type.get(i) + ":" + Login.institution.get(i);
        }
        data = data + "\n\nOther friends using the app";

        for(int i =0; i<Login.friends_list.size();i++){
            data = data + "\n" + Login.friends_list.get(i);
        }


        details = (TextView) findViewById(R.id.details);

        details.setText(data);
    }
}
