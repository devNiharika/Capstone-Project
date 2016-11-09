package in.edu.galgotiasuniversity.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.utils.Utils;

public class DateWiseAdapter extends RecyclerView.Adapter<DateWiseAdapter.ViewHolder> {

    private List<Record> records;
    private Context mContext;

    public DateWiseAdapter(Context context, List<Record> records) {
        this.mContext = context;
        this.records = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_date_wise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String attendance = records.get(position).STATUS;

        if (attendance.equals("P") || attendance.equals("p"))
            holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_green));
        else if (attendance.equals("A") || attendance.equals("a"))
            holder.cardColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_red));

        holder.title.setText(records.get(position).SUBJECT_NAME);
        holder.content1.setText(records.get(position).ATTENDANCE_TYPE);
        holder.content2.setText(records.get(position).TIME_SLOT);
        holder.content3.setText(records.get(position).STRING_DATE);
        holder.content4.setText(attendance);
    }

    @Override
    public int getItemCount() {
        return records.size();
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
        @BindView(R.id.content4)
        TextView content4;
        @BindView(R.id.cardColor)
        View cardColor;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Utils.setFontAllView((ViewGroup) view);
        }

    }
}