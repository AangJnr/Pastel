package ind.aang.pastel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by AangJnr on 10/6/16.
 */
public class FullScreenActivity extends AppCompatActivity{


    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.full_screen_activity);



        Intent intent = getIntent();
        String _getColor = intent.getStringExtra("ColorCode");

        findViewById(R.id.full_screen_activity_layout).setBackgroundColor(Color.parseColor(_getColor));



    }







    @Override
    public void onDestroy(){
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }else{
            finish();

        }


    }




}
