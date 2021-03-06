package in.edu.galgotiasuniversity.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.models.Month;
import in.edu.galgotiasuniversity.utils.Utils;

public class MonthWiseAdapter extends RecyclerView.Adapter<MonthWiseAdapter.ViewHolder> {

    private List<Month> months;
    private Context mContext;

    public MonthWiseAdapter(Context context, List<Month> months) {
        this.mContext = context;
        this.months = months;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_month_wise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Float percentage = months.get(position).PERCENTAGE;
//        String color = contents3.get(position);

//            float percentage = Float.valueOf(sPercentage);
//            if (color.equals("DarkGreen") || color.equals("Green") || color.equals("Lime"))

        if (percentage >= 75f)
            holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_green));
        else
            holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_red));

        holder.title.setText(months.get(position).NAME);
        holder.content1.setText(mContext.getString(R.string.present) + " :" + months.get(position).PRESENT);
        holder.content2.setText(mContext.getString(R.string.absent) + " :" + months.get(position).ABSENT);
//        holder.content3.setText(ColorUtils.getAttendanceRange(color));
        String percent = String.format(Locale.ENGLISH, "%.2f", percentage) + "%";
        holder.content3.setText(percent);
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content1)
        TextView content1;
        @BindView(R.id.content2)
        TextView content2;
        @BindView(R.id.content3)
        TextView content3;
        @BindView(R.id.cardColor)
        View cardColor;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Utils.setFontAllView((ViewGroup) view);
        }

    }
}