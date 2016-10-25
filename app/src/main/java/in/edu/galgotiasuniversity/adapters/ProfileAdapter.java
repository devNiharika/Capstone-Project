package in.edu.galgotiasuniversity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.Utils;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    ArrayList<String> titles, contents;
    Context mContext;

    public ProfileAdapter(Context context, ArrayList<String> titles, ArrayList<String> contents) {
        this.mContext = context;
        this.titles = titles;
        this.contents = contents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(titles.get(position));
        holder.content.setText(contents.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.content)
        TextView content;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Utils.setFontAllView((ViewGroup) view);
        }

    }
}