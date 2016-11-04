package ind.aang.pastel;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * Created by AangJnr on 10/12/16.
 */
public class CustomDialogAvtivity extends AppCompatActivity{
    RecyclerView mRecycler;
    GridLayoutManager mLayoutManager;
    SwatchItemsAdapter mAdapter;
    List<Palette.Swatch> _swatchList;
    DataBaseHelper colorsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swatch_dialog);

        mRecycler = (RecyclerView) findViewById(R.id.swatch_recycler);
        mLayoutManager = new GridLayoutManager(this, LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        _swatchList = ImageColorPickerActivity.swatchList;

        mAdapter = new SwatchItemsAdapter(CustomDialogAvtivity.this, _swatchList);

        mRecycler.setAdapter(mAdapter);

        colorsDatabase = new DataBaseHelper(this);
        mAdapter.setOnItemClickListener(onItemClickListener);


        findViewById(R.id.swatch_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }else{
                    finish();

                }

            }
        });

    }


    SwatchItemsAdapter.OnItemClickListener onItemClickListener = new SwatchItemsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {



            Palette.Swatch swatch_item = _swatchList.get(position);

            String _colorCode = String.format("#%06X", (0xFFFFFF & swatch_item.getRgb()));

            if(!colorsDatabase.colorExists(_colorCode)) {

                ColorItem color = new ColorItem(_colorCode);

                colorsDatabase.addColor(color);


                Toast.makeText(CustomDialogAvtivity.this, "Color added", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(CustomDialogAvtivity.this, "Already added!", Toast.LENGTH_SHORT).show();

            }


        }
    };




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
