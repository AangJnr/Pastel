package ind.aang.pastel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;

import java.io.IOException;
import java.util.List;

/**
 * Created by AangJnr on 10/8/16.
 */
public class ImageColorPickerActivity extends AppCompatActivity implements View.OnTouchListener{
    RecyclerView mRecycler;
    static List<Palette.Swatch> swatchList;
    GridLayoutManager mLayoutManager;

    SwatchItemsAdapter mAdapter;
    View mView;
    AlertDialog alertDialogAndroid;
    DataBaseHelper colorsDatabase;
    FloatingActionButton fab_image_picker;
    Bitmap bitmap;
    int PERMISSION_CAMERA = 1;
    int PERMISSION_STORAGE = 2;
    WindowManager windowManager;
    float dX;
    float dY;
    int lastAction;
    View floating_layout;
    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
        setContentView(R.layout.image_picker_activity);
        image = (ImageView) findViewById(R.id.image);

        int requestCode = getIntent().getIntExtra("requestCode", 0);

        String decoded_image = getIntent().getStringExtra("decodedImageString");

        String bytes = getIntent().getStringExtra("PHOTO_BITMAP");

        //Toast.makeText(this, requestCode, Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_STORAGE) {


            Toast.makeText(this, "Gallery intent " + decoded_image, Toast.LENGTH_SHORT).show();


            bitmap = getScaledBitmap(decoded_image,
                    Utils.getScreenWidth(this), Utils.getScreenHeight(this));

            image.setImageBitmap(bitmap);


        } else if (requestCode == PERMISSION_CAMERA) {

            Toast.makeText(this, "Camera intent " + bytes, Toast.LENGTH_SHORT).show();


            bitmap = getScaledBitmap(bytes,
                    Utils.getScreenWidth(this), Utils.getScreenHeight(this));


            image.setImageBitmap(bitmap);

        }


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


        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.image_relative_layout);
        floating_layout = getLayoutInflater().inflate(R.layout.floating_layout, rl, false);

        final RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        rl.addView(floating_layout, params);
        floating_layout.setVisibility(View.GONE);


        image.setOnTouchListener(this);
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
        //super.onBackPressed();
        refreshActivity();
        finish();

    }



    public void refreshActivity() {
        Intent intent = new Intent(ImageColorPickerActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {

        final Bitmap loaded_bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        int xCord;
        int yCord;
        float[] touchPoint;
        Matrix inverse = new Matrix();

        if(floating_layout.getVisibility() == View.GONE)
            floating_layout.setVisibility(View.VISIBLE);




        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                image.getImageMatrix().invert(inverse);
                touchPoint = new float[] {event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);
                xCord = Integer.valueOf((int)touchPoint[0]);
                yCord = Integer.valueOf((int)touchPoint[1]);

                floating_layout.animate()
                        .x(event.getX())
                        .y(event.getY())
                        .setDuration(500)
                        .start();

                break;

            case MotionEvent.ACTION_MOVE:

                image.getImageMatrix().invert(inverse);
                touchPoint = new float[] {event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);
                xCord = Integer.valueOf((int)touchPoint[0]);
                yCord = Integer.valueOf((int)touchPoint[1]);


                if(loaded_bitmap != null )

                floating_layout.animate()
                        .x(event.getX())
                        .y(event.getY())
                        .setDuration(0)
                        .start();
                CircleView circle = (CircleView) floating_layout.findViewById(R.id.circle);
                floating_layout.setBackgroundColor(getProjectedColor((ImageView)view, bitmap , (int)event.getX(), (int)event.getY()));

                /*Toast.makeText(ImageColorPickerActivity.this, redValue + " " + blueValue  + " " + greenValue
                        , Toast.LENGTH_SHORT).show();
*/
                break;

            case MotionEvent.ACTION_UP:

                touchPoint = new float[] {event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);
                xCord = Integer.valueOf((int)touchPoint[0]);
                yCord = Integer.valueOf((int)touchPoint[1]);


                if(loaded_bitmap != null )

                    floating_layout.setBackgroundColor(getProjectedColor((ImageView)view, bitmap , (int)event.getX(), (int)event.getY()));

                break;
            default:
                return false;
        }
        return true;

    }

    private int getProjectedColor(ImageView iv, Bitmap bm, int x, int y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return android.R.color.white;
        }else{
            int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

            /*Toast.makeText(this, x + ":" + y + "/" + iv.getWidth() + " : " + iv.getHeight() + "\n" +
                            projectedX + " : " + projectedY + "/" + bm.getWidth() + " : " + bm.getHeight()
                    , Toast.LENGTH_SHORT).show();*/


            return bm.getPixel(projectedX, projectedY);
        }
    }
}