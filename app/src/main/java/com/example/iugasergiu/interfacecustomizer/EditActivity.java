package com.example.iugasergiu.interfacecustomizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {

    private Button btnSave, btnDraggable;
    private DatabaseReference databaseRefference;

    private FirebaseUser user_2;
    private String uid_user_2;
    private DatabaseReference databaseRefference_user_2;

    private ViewGroup rootLayout;
    private int _xDelta;
    private int _yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btnSave = (Button) findViewById(R.id.action_bar_button_save);

        btnDraggable = (Button) findViewById(R.id.draggable_btn);

        //get position
        user_2 = FirebaseAuth.getInstance().getCurrentUser();
        uid_user_2 = user_2.getUid();

        databaseRefference_user_2 = FirebaseDatabase.getInstance().getReference();

        databaseRefference_user_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int x_pos_user_2 = dataSnapshot.child(uid_user_2).child("position_x").getValue(Integer.class);
                int y_pos_user_2 = dataSnapshot.child(uid_user_2).child("position_y").getValue(Integer.class);
                btnDraggable.setX(x_pos_user_2);
                btnDraggable.setY(y_pos_user_2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //END get position

        rootLayout = (ViewGroup) findViewById(R.id.view_root);
        btnDraggable.setOnTouchListener(new ChoiceTouchListener());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                databaseRefference = FirebaseDatabase.getInstance().getReference();
                ButtonProperties btnProperties = new ButtonProperties(btnDraggable.getX(),btnDraggable.getY());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                databaseRefference.child(user.getUid()).setValue(btnProperties);

                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    }

}
