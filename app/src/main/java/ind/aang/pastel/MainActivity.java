package ind.aang.pastel;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper colorsDatabase;
    List<ColorItem> colors;
    ColorsAdapter mAdapter;
    RecyclerView mRecycler;
    GridLayoutManager mLayoutManager;
    ImageView night_toggle;
    LinearLayout emptyView;
    FloatingActionsMenu multi_actions;
    String imgDecodableString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        updateTheme();

        setContentView(R.layout.activity_main);

        multi_actions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        if(savedInstanceState == null) startFabAnimation();

        colorsDatabase = new DataBaseHelper(this);

        colors = colorsDatabase.getAllColors();

        night_toggle = (ImageView) findViewById(R.id.night_mode_toggle);

        if(!Constants.isNightMode){
            night_toggle.setImageResource(R.drawable.night);

        }else{
            night_toggle.setImageResource(R.drawable.day);
        }


        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Capture and set imageView
                multi_actions.collapse();
                Toast.makeText(MainActivity.this, "Coming soon! Pro.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.night_mode_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.isNightMode){
                    Utils.setTheme(getApplicationContext(), 2);
                    refreshActivity();
                    //night_toggle.setImageResource(R.drawable.day);

                }else if (Constants.isNightMode){
                    Utils.setTheme(getApplicationContext(), 1);

                    refreshActivity();
                    //night_toggle.setImageResource(R.drawable.night);

                }
            }
        });


         findViewById(R.id.add_color_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multi_actions.collapse();
                addColorAlert();
            }
        });

        findViewById(R.id.add_color_from_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multi_actions.collapse();
                loadImagefromGallery();
                //finish();
                //startActivity(new Intent(MainActivity.this, ImageColorPickerActivity.class));



            }
        });

        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mLayoutManager = new GridLayoutManager(this, 4);

        //mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(300);
        mRecycler.setItemAnimator(itemAnimator);

        RecyclerViewItemDecorator decoration = new RecyclerViewItemDecorator(8);
        mRecycler.addItemDecoration(decoration);

        initSwipe();

        mRecycler.setItemAnimator(new RecyclerViewItemAnimator());

        if (colors == null) {
            emptyView.setVisibility(View.VISIBLE);


        } else {

            if (colors.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            }

            if (colors != null && colors.size() > 0) {
                emptyView.setVisibility(View.GONE);
                mAdapter = new ColorsAdapter(this, colors);
                mRecycler.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(onItemClickListener);
                startContentAnimation();
            }
        }

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 ) {
                   // multi_actions.setTranslationY(0);
                    multi_actions.clearAnimation();
                    multi_actions.animate()
                            .translationY(0)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setStartDelay(100)
                            .setDuration(500)
                            .start();
                } else if (dy > 0 ) {
                    //multi_actions.setTranslationY(2 * 56).
                    multi_actions.clearAnimation();
                    multi_actions.animate()
                            .translationY(500)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setStartDelay(100)
                            .setDuration(500)
                            .start();
                }
            }
        });
    }


    ColorsAdapter.OnItemClickListener onItemClickListener = new ColorsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
      //Start a fill screen activity with color as background
        ColorItem color = colors.get(position);

            Intent intent = new Intent(MainActivity.this, FullScreenActivity.class);
            intent.putExtra("ColorCode", color.getColorCode());
            CircleView colorview = (CircleView) view.findViewById(R.id.color_view);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.this, colorview, getString(R.string.transition_name));

            ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());

        }
    };


    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, Constants.RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == Constants.RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                if(imgDecodableString != null){
                    //Start ImageActivity

                    Intent intent = new Intent (MainActivity.this, ImageColorPickerActivity.class);
                    intent.putExtra("decodedImageString", imgDecodableString);
                    startActivity(intent);
                    finish();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }



    public void addColorAlert(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText color_code_EditText = (EditText) mView.findViewById(R.id.userInputDialog);
        //if(color_code_EditText.hasFocus()) color_code_EditText.setError(null);
        color_code_EditText.setText("#");
        alertDialogBuilderUserInput
                .setCancelable(true);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        mView.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _colorCode = color_code_EditText.getText().toString();

                if(_colorCode.isEmpty() || _colorCode.equals("") || !( _colorCode.matches("[a-fA-F0-9x#]*"))){
                    color_code_EditText.setError("Enter a valid RGB/Hex color code");

                     }
                else if(!_colorCode.startsWith("#") && (_colorCode.length() == 8 || _colorCode.length() == 6)){
                        _colorCode = "#" + _colorCode;
                    if(!colorsDatabase.colorExists(_colorCode)) {
                        addColor(_colorCode);
                        alertDialogAndroid.dismiss();
                    }else{
                        alertDialogAndroid.dismiss();
                        Toast.makeText(MainActivity.this, "Color already added", Toast.LENGTH_SHORT).show();
                    }

                }else if(_colorCode.startsWith("#") && (_colorCode.length() == 7 || _colorCode.length() == 9)){
                    if(!colorsDatabase.colorExists(_colorCode)) {
                        addColor(_colorCode);
                        alertDialogAndroid.dismiss();
                    }else{
                        alertDialogAndroid.dismiss();
                        Toast.makeText(MainActivity.this, "Color already added", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    color_code_EditText.setError("Enter a valid RGB/Hex color code");
                }
            }
        });


        mView.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });
    }

    public void customAlert(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_alert_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        /*mView.findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                showSignUpAlert();

            }
        });


        mView.findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                showSignInAlert();
            }
        });*/


        mView.findViewById(R.id.google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                googleSignIn();

            }
        });


    }

    public void showSignUpAlert(){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog_signin, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final TextInputLayout email_TextInput = (TextInputLayout) mView.findViewById(R.id.email_layout);
        final TextInputLayout password_TextInput = (TextInputLayout) mView.findViewById(R.id.password_layout);
        final TextInputLayout password_2_TextInput = (TextInputLayout) mView.findViewById(R.id.password_layout_2);



        alertDialogBuilderUserInput
                .setCancelable(true);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        mView.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signUpValidate(email_TextInput, password_TextInput, password_2_TextInput)){

                    //Sign up with firebase, edit shared signin prefs


                }else {
                    alertDialogAndroid.dismiss();
                    Toast.makeText(MainActivity.this, "Please check email and password", Toast.LENGTH_LONG).show();
                    return;

                }
            }
        });


        mView.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });


    }

    public void showSignInAlert(){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog_signin, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final TextInputLayout email_TextInput = (TextInputLayout) mView.findViewById(R.id.email_layout);
        final TextInputLayout password_TextInput = (TextInputLayout) mView.findViewById(R.id.password_layout);
        mView.findViewById(R.id.password_layout_2).setVisibility(View.GONE);




        alertDialogBuilderUserInput
                .setCancelable(true);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        mView.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signInValidate(email_TextInput, password_TextInput)){

                    //Sign in with firebase, edit shared signin prefs


                }else {
                    alertDialogAndroid.dismiss();
                    Toast.makeText(MainActivity.this, "Please check email and password", Toast.LENGTH_LONG).show();
                    return;

                }
            }
        });


        mView.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });


    }

    public void googleSignIn(){}

    public boolean signUpValidate(TextInputLayout emailLayout, TextInputLayout passLayout, TextInputLayout pass2Layout) {
        boolean valid = true;

        String email = emailLayout.getEditText().getText().toString();
        String pass1 = passLayout.getEditText().getText().toString();
        String pass2 = pass2Layout.getEditText().getText().toString();



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("enter a valid email address");
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (pass1.isEmpty() || pass1.length() < 8) {
            passLayout.setError("password should be 8+ characters");
            valid = false;
        } else {
            passLayout.setError(null);
        }

        if (!pass1.equals(pass2)) {
            passLayout.setError("passwords did not match");
            pass2Layout.setError("passwords did not match");
            valid = false;
        } else {
            passLayout.setError(null);
            pass2Layout.setError(null);
        }




        return valid;
    }

    public boolean signInValidate(TextInputLayout emailLayout, TextInputLayout passLayout) {
        boolean valid = true;

        String email = emailLayout.getEditText().getText().toString();
        String pass1 = passLayout.getEditText().getText().toString();




        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("enter a valid email address");
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (pass1.isEmpty() || pass1.length() < 8) {
            passLayout.setError("password should be 8+ characters");
            valid = false;
        } else {
            passLayout.setError(null);
        }

        return valid;
    }


    private void initSwipe() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                onItemRemove(viewHolder, mRecycler);


            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecycler);
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {


        final int adapterPosition = viewHolder.getAdapterPosition();
        final ColorItem _color = colors.get(adapterPosition);

        final Snackbar snackbar = Snackbar
                .make(findViewById(R.id.cord), "DELETED", Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);

                        multi_actions.clearAnimation();
                        multi_actions.animate()
                                .translationY(-100)
                                .setInterpolator(new OvershootInterpolator(1.f))
                                .setDuration(300)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {


                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                        multi_actions.clearAnimation();
                                        multi_actions.animate()
                                                .translationY(0)
                                                .setInterpolator(new OvershootInterpolator(1.f))
                                                .setDuration(200)
                                                .setStartDelay(2000)
                                                .start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }
                })
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colors.add(adapterPosition, _color);
                        colorsDatabase.addColor(_color);

                        if(emptyView.getVisibility() == View.VISIBLE) emptyView.setVisibility(View.GONE);

                        mAdapter.notifyItemInserted(adapterPosition);
                        recyclerView.smoothScrollToPosition(adapterPosition);

                    }
                });
        snackbar.show();

        colors.remove(adapterPosition);
        colorsDatabase.deleteColor(_color.getColorCode());
        mAdapter.notifyItemRemoved(adapterPosition);
        recyclerView.smoothScrollToPosition(adapterPosition);

        if (colors.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }

    }


    public void addColor(String color){

        if(emptyView.getVisibility() == View.VISIBLE){
            emptyView.setVisibility(View.GONE);
        }

        ColorItem _addColor = new ColorItem(color);
        colorsDatabase.addColor(_addColor);
        colors.add(_addColor);
        mAdapter.notifyDataSetChanged();

        Toast.makeText(MainActivity.this, "Color added", Toast.LENGTH_SHORT).show();
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


    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private void startFabAnimation() {
        multi_actions.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.fab_size));
        multi_actions.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(500)
                .start();

    }

    private void startContentAnimation() {
        mRecycler.setAlpha(0f);
        mRecycler.animate()
                .setStartDelay(500)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(Utils.getIsSignedIn(MainActivity.this)) {
            menu.findItem(R.id.backup).setChecked(true);
        }else{
            menu.findItem(R.id.backup).setChecked(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement
        switch(item.getItemId()){
            case  R.id.backup:

            if(!item.isChecked()){
                Toast.makeText(MainActivity.this, "Backup feature coming soon", Toast.LENGTH_SHORT).show();
                //Show dialog to sign up or in

            }
                break;

            case  R.id.restore:

                if(!item.isChecked()){
                    Toast.makeText(MainActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
                    //Show dialog to sign up or in

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();


    }


}
