package seproject14.enshare.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import seproject14.enshare.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DialogPictureView extends AppCompatActivity {
    private String date;
    private String location;
    private String user;
    private TextView dateButton;
    private TextView locationButton;
    private TextView userButton;
    Dialog dialog;
    private Button closeButton1;
    private Button closeButton2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_info);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();


        Log.d(TAG, "onCreateDialog: reached DialogFragment");
        // Get the layout inflater
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        location = intent.getStringExtra("location");
        user = intent.getStringExtra("user");

        dateButton = findViewById(R.id.timeInformation);
        dateButton.setText(date);
        locationButton = findViewById(R.id.locationInformation);
        locationButton.setText(location);
        userButton = findViewById(R.id.userInformation);
        userButton.setText(user);

        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        setContentView(inflater.inflate(R.layout.dialog_image_info, null))
//                // Add action buttons
//                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                    }
//                });

        OnClickButtonListener();
    }
    public void OnClickButtonListener() {
        closeButton1 = (Button) findViewById(R.id.button_close);
        closeButton1.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
