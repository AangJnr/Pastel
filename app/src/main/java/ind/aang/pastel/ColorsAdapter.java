package ind.aang.pastel;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by AangJnr on 10/5/16.
 */
public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>{

    View v;
    Context mContext;
    OnItemClickListener mItemClickListener;
    private List<ColorItem> colors;
    DataBaseHelper colorsDatabase;


    public ColorsAdapter(Context mContext, List<ColorItem> colors) {
        this.colors = colors;
        this.mContext = mContext;

    }



    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.colors_item, parent, false);


        return new ColorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {

        ColorItem color = colors.get(position);
        String _color = color.getColorCode();

        //holder.colorView.setCircleRadius(50);
        holder.colorView.setFillColor(Color.parseColor(_color));
        holder.colorCode.setText(_color);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView colorCode;
        public CircleView colorView;


        ColorViewHolder(View itemView){
            super(itemView);

            colorView = (CircleView)itemView.findViewById(R.id.color_view);
            colorCode = (TextView) itemView.findViewById(R.id.color_name);

            colorView.setOnClickListener(this);
        }




        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());


            }

        }
    }



    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
