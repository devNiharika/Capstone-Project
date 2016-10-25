package in.edu.galgotiasuniversity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.Utils;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private ArrayList<String> titles, contents1, contents2, contents3;
    private Context mContext;

    public LibraryAdapter(Context context, ArrayList<String> titles, ArrayList<String> contents1, ArrayList<String> contents2, ArrayList<String> contents3) {
        this.mContext = context;
        this.titles = titles;
        this.contents1 = contents1;
        this.contents2 = contents2;
        this.contents3 = contents3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.content1.setText(contents1.get(position));
        holder.content2.setText(contents2.get(position));
        holder.content3.setText(contents3.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Utils.setFontAllView((ViewGroup) view);
        }

    }
}