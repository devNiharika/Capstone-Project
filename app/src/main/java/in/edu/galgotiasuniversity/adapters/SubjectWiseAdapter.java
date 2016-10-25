package in.edu.galgotiasuniversity.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.ColorUtils;
import in.edu.galgotiasuniversity.utils.Utils;

public class SubjectWiseAdapter extends RecyclerView.Adapter<SubjectWiseAdapter.ViewHolder> {

    ArrayList<String> titles, contents1, contents2, contents3;
    Context mContext;

    public SubjectWiseAdapter(Context context, ArrayList<String> titles, ArrayList<String> contents1, ArrayList<String> contents2, ArrayList<String> contents3) {
        this.mContext = context;
        this.titles = titles;
        this.contents1 = contents1;
        this.contents2 = contents2;
        this.contents3 = contents3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject_wise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String sPercentage = contents3.get(position);
        String color = contents3.get(position);
        try {
//            float percentage = Float.valueOf(sPercentage);
//            if (percentage >= 75)
            if (color.equals("DarkGreen") || color.equals("Green") || color.equals("Lime"))
                holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_green));
            else
                holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_red));
//            sPercentage += "%";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        holder.title.setText(titles.get(position));
        holder.content1.setText(contents1.get(position));
        holder.content2.setText(contents2.get(position));
        holder.content3.setText(ColorUtils.getAttendanceRange(color));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.content1)
        TextView content1;
        @Bind(R.id.content2)
        TextView content2;
        @Bind(R.id.content3)
        TextView content3;
        @Bind(R.id.cardColor)
        View cardColor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Utils.setFontAllView((ViewGroup) view);
        }

    }
}