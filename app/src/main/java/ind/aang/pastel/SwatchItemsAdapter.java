package ind.aang.pastel;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by AangJnr on 10/9/16.
 */
public class SwatchItemsAdapter extends RecyclerView.Adapter<SwatchItemsAdapter.SwatchViewHolder>{


    Context mContext;
    List<Palette.Swatch> swatchList;
    OnItemClickListener mItemClickListener;


public SwatchItemsAdapter(Context context, List<Palette.Swatch> swatch_list){
    this.mContext = context;
    this. swatchList = swatch_list;

}


    @Override
    public SwatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.swatch_item, parent, false);



        return new SwatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SwatchViewHolder holder, int position) {
        Palette.Swatch swatch_item = swatchList.get(position);

        String _color = String.format("#%06X", (0xFFFFFF & swatch_item.getRgb()));
        String _titleColor = String.format("#%06X", (0xFFFFFF & swatch_item.getTitleTextColor()));

        holder.swatch_view.setBackgroundColor(Color.parseColor(_color));
        holder.swatch_code.setText(_color);
        holder.swatch_code.setTextColor(Color.parseColor(_titleColor));
    }



    @Override
    public int getItemCount() {
        return swatchList.size();
    }

    public class SwatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LinearLayout swatch_item;
        RelativeLayout swatch_view;
        TextView swatch_code;


    SwatchViewHolder(View itemView){
        super(itemView);


        swatch_item = (LinearLayout) itemView.findViewById(R.id.swatch_item);
        swatch_view = (RelativeLayout) itemView.findViewById(R.id.swatch_view);
        swatch_code = (TextView) itemView.findViewById(R.id.swatch_name);

        swatch_item.setOnClickListener(this);
        swatch_code.setOnClickListener(this);

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

