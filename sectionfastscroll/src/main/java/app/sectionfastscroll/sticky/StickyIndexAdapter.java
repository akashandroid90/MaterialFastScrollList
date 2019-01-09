package app.sectionfastscroll.sticky;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.sectionfastscroll.R;
import app.sectionfastscroll.model.RowStyle;

public class StickyIndexAdapter extends RecyclerView.Adapter<StickyIndexAdapter.StickyIndexViewHolder> {
    private RowStyle rowStyle;
    private char[] dataSet;

    public StickyIndexAdapter(@NonNull char[] dataSet, @Nullable RowStyle rowStyle) {
        this.dataSet = dataSet;
        this.rowStyle = rowStyle;
    }

    @NonNull
    @Override
    public StickyIndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stickyindex_details, parent, false);
        applyStyle(view);
        return new StickyIndexViewHolder(view);
    }

    private void applyStyle(View view) {
        if (rowStyle == null) return;
        if (rowStyle.height != 0)
            setLayoutParams(view, rowStyle);
        TextView index = view.findViewById(R.id.sticky_row_index);
        if (rowStyle.color != 0) {
            index.setTextColor(this.rowStyle.color);
        }

        if (rowStyle.size != 0) {
            index.setTextSize(0, this.rowStyle.size);
        }

        if (rowStyle.style != 0) {
            index.setTypeface((Typeface) null, this.rowStyle.style);
        }
    }

    private void setLayoutParams(View view, RowStyle rowStyle) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.height = (int) rowStyle.height;
        params.width = (int) rowStyle.width;
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull StickyIndexViewHolder holder, int position) {
        holder.textView.setText(String.valueOf(dataSet[position]));

        holder.textView.setVisibility(isHeader(position) ? View.VISIBLE : View.INVISIBLE);
    }

    private final boolean isHeader(int idx) {
        if (idx != 0) {
            char var2 = this.dataSet[idx - 1];
            char var3 = Character.toLowerCase(var2);
            var2 = this.dataSet[idx];
            char var4 = Character.toLowerCase(var2);
            if (var3 == var4) {
                return false;
            }
        }
        return true;
    }

    public final void refresh(@NonNull char[] dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public class StickyIndexViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StickyIndexViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.sticky_row_index);
        }
    }
}
