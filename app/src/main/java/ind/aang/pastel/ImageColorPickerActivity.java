package ind.aang.pastel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

/**
 * Created by AangJnr on 10/8/16.
 */
public class ImageColorPickerActivity extends AppCompatActivity {
    RecyclerView mRecycler;
    static List<Palette.Swatch> swatchList;
    GridLayoutManager mLayoutManager;
    Bitmap bitmap;
    SwatchItemsAdapter mAdapter;
    View mView;
    AlertDialog alertDialogAndroid;
    DataBaseHelper colorsDatabase;
    FloatingActionButton fab_image_picker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
        setContentView(R.layout.image_picker_activity);

        String decoded_image = getIntent().getStringExtra("decodedImageString");
        ImageView image = (ImageView) findViewById(R.id.image);
        bitmap = getScaledBitmap(decoded_image,
                Utils.getScreenWidth(this), Utils.getScreenHeight(this));

                image.setImageBitmap(bitmap);
        colorsDatabase = new DataBaseHelper(this);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/





        final Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                // access palette colors here
                swatchList = palette.getSwatches();

                Toast.makeText(ImageColorPickerActivity.this, "Palette generated", Toast.LENGTH_SHORT).show();
            }
        };

        if (bitmap != null && !bitmap.isRecycled()) {
            Palette.from(bitmap).generate(paletteListener);
        }


        fab_image_picker = (FloatingActionButton) findViewById(R.id.image_picker_fab);

        fab_image_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Generate swatch and show dialog
                showSwatchList();



            }
        });






    }

    public void updateTheme() {
        if (Utils.getTheme(getApplicationContext()) <= Constants.LIGHT_THEME) {
            setTheme(R.style.AppTheme);
            Constants.isNightMode = false;

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            }
        } else if (Utils.getTheme(getApplicationContext()) == Constants.DARK_THEME) {
            setTheme(R.style.AppTheme_Dark);
            Constants.isNightMode = true;

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }



    public void showSwatchList(){



       LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ImageColorPickerActivity.this);
        mView = layoutInflaterAndroid.inflate(R.layout.swatch_dialog, null, false);
        mRecycler = (RecyclerView)mView.findViewById(R.id.swatch_recycler);
        mLayoutManager = new GridLayoutManager(this, LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);

        mAdapter = new SwatchItemsAdapter(ImageColorPickerActivity.this, swatchList);

        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ImageColorPickerActivity.this);
        alertDialogBuilderUserInput.setView(mView);


        alertDialogBuilderUserInput
                .setCancelable(true);
        alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialogAndroid.show();


        mView.findViewById(R.id.swatch_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogAndroid.cancel();
                alertDialogAndroid.dismiss();


            }
        });





        /*Intent intent = new Intent(ImageColorPickerActivity.this, CustomDialogAvtivity.class);

        Pair<View, String> imagePair = Pair.create((View) fab_image_picker, getResources().getString(R.string.transition_name));


        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageColorPickerActivity.this,
                imagePair);

        ActivityCompat.startActivity(ImageColorPickerActivity.this, intent, options.toBundle());*/


    }


    SwatchItemsAdapter.OnItemClickListener onItemClickListener = new SwatchItemsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {



            Palette.Swatch swatch_item = swatchList.get(position);

            String _colorCode = String.format("#%06X", (0xFFFFFF & swatch_item.getRgb()));

            if(!colorsDatabase.colorExists(_colorCode)) {

                ColorItem color = new ColorItem(_colorCode);

                colorsDatabase.addColor(color);


                Toast.makeText(ImageColorPickerActivity.this, "Color added", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ImageColorPickerActivity.this, "Already added!", Toast.LENGTH_SHORT).show();

            }


        }
    };

    public void onBackPressed(){
        super.onBackPressed();
        refreshActivity();
        //finish();

    }

    public void refreshActivity() {
        Intent intent = new Intent(ImageColorPickerActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}