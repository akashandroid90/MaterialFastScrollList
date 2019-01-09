package app.sectionfastscroll.sticky;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.sectionfastscroll.R;
import app.sectionfastscroll.model.RowStyle;

public class StickyIndexLayoutManager {
    private final TextView header;
    private final LinearLayoutManager layoutManager;
    private final RecyclerView contentList;

    public StickyIndexLayoutManager(@NonNull RelativeLayout container, @NonNull RecyclerView contentList) {
        super();
        this.contentList = contentList;
        View var10001 = container.findViewById(R.id.sticky_index);
        this.header = (TextView) var10001;
        RecyclerView.LayoutManager var3 = this.contentList.getLayoutManager();
        if (var3 == null) {
            throw new ClassCastException("null cannot be cast to non-null type android.support.v7.widget.LinearLayoutManager");
        } else {
            this.layoutManager = (LinearLayoutManager) var3;
        }
    }

    private final void synchronizeScrolls(RecyclerView rv) {
        View firstVisibleView = rv.getChildAt(0);
        int var1 = rv.getChildAdapterPosition(firstVisibleView);
        layoutManager.scrollToPositionWithOffset(var1, firstVisibleView.getTop());
    }

    public final void update(@NonNull RecyclerView rv, float dy) {
        RecyclerView.Adapter var10000 = this.contentList.getAdapter();
        if (var10000.getItemCount() >= 2) {
            this.synchronizeScrolls(rv);
            View firstVisibleItemContainer = this.contentList.getChildAt(0);
            TextView firstVisibleItem = (TextView) firstVisibleItemContainer.findViewById(R.id.sticky_row_index);
            this.displayHeader(firstVisibleItem);
            TextView nextVisibleItem = (TextView) this.contentList.getChildAt(1).findViewById(R.id.sticky_row_index);
            if (this.isHeader(firstVisibleItem, nextVisibleItem)) {
                this.animateTransitionToNext(firstVisibleItem, firstVisibleItemContainer, nextVisibleItem);
            } else {
                firstVisibleItem.setVisibility(View.INVISIBLE);
                if (this.isScrollingDown(dy)) {
                    this.header.setVisibility(View.VISIBLE);
                } else {
                    nextVisibleItem.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private final void displayHeader(TextView firstItem) {
        TextView var10000 = this.header;
        String var2 = String.valueOf(firstItem.getText().charAt(0));
        TextView var3 = var10000;
        if (var2 == null) {
            throw new ClassCastException("null cannot be cast to non-null type java.lang.String");
        } else {
            String var5 = var2.toUpperCase();
            String var4 = var5;
            var3.setText((CharSequence) var4);
            this.header.setVisibility(View.VISIBLE);
            firstItem.setAlpha(1.0F);
        }
    }

    private final void animateTransitionToNext(TextView first, View firstContainer, TextView second) {
        this.header.setVisibility(View.INVISIBLE);
        first.setVisibility(View.VISIBLE);
        first.setAlpha(this.computeAlpha(firstContainer, first));
        second.setVisibility(View.VISIBLE);
    }

    public final void applyStyle(@NonNull RowStyle style) {
        if (style.size != 0) {
            this.header.setTextSize(0, style.size);
        }

        if (style.style != 0) {
            this.header.setTypeface((Typeface) null, style.style);
        }

        this.header.setTextColor(style.color);
    }

    private final boolean isScrollingDown(float d) {
        return d > (float) 0;
    }

    private final float computeAlpha(View row, TextView idx) {
        return (float) 1 - Math.abs(row.getY()) / (float) idx.getHeight();
    }

    private final boolean isHeader(TextView prev, TextView act) {
        char var3 = prev.getText().charAt(0);
        char var10000 = Character.toLowerCase(var3);
        var3 = act.getText().charAt(0);
        char var4 = var10000;
        char var5 = Character.toLowerCase(var3);
        return var4 != var5;
    }
}
