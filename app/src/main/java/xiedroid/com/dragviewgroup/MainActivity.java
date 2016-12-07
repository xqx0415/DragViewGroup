package xiedroid.com.dragviewgroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PositionLayout mPositionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<UserModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserModel userModel = new UserModel();
            userModel.setViewName("View" + i);
            if (i % 2 == 0) {
                userModel.setViewRole(1);
            } else {
                userModel.setViewRole(2);
            }
            list.add(userModel);
        }

        mPositionLayout = (PositionLayout) findViewById(R.id.id_room);
        mPositionLayout.addUsers(list, "View5", new OnUserClickListener() {
            @Override
            public void onUserClick(View v, int position) {
                Toast.makeText(MainActivity.this, ""+list.get(position).getViewName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
