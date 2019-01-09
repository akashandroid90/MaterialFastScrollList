package app.sectionfastscroll.sticky;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import app.sectionfastscroll.R;
import app.sectionfastscroll.model.RowStyle;

public class StickyIndex extends RelativeLayout {
    private StickyIndexAdapter adapterSticky;
    private StickyIndexLayoutManager stickyStickyIndex;
    private LinearLayout sticky_index_wrapper;
    private RecyclerView index_list;

    public StickyIndex(Context context) {
        this(context, null);
    }

    public StickyIndex(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyIndex(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context.obtainStyledAttributes(attrs, R.styleable.StickyIndex));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StickyIndex(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context.obtainStyledAttributes(attrs, R.styleable.StickyIndex));
    }

    private void init(TypedArray typedArray) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.stickyindex_view, this, true);
        sticky_index_wrapper = inflate.findViewById(R.id.sticky_index_wrapper);
        index_list = inflate.findViewById(R.id.index_list);

        RowStyle rowStyle = new RowStyle(
                typedArray.getDimension(R.styleable.StickyIndex_rowHeight, 0),
                typedArray.getDimension(R.styleable.StickyIndex_stickyWidth, 0),
                typedArray.getColor(R.styleable.StickyIndex_android_textColor, ContextCompat.getColor(getContext(), R.color.index_text_color)),
                typedArray.getDimension(R.styleable.StickyIndex_android_textSize, 26f),
                typedArray.getInt(R.styleable.StickyIndex_android_textStyle, 0)
        );
        applyStyle(rowStyle);
    }

    private void applyStyle(RowStyle style) {
        renderStickyList(style);
        if (style == null) return;
        stickyStickyIndex.applyStyle(style);
        renderStickyWrapper(style);
        if (style.height != 0) {
            ViewGroup.LayoutParams layoutParams = sticky_index_wrapper.getLayoutParams();
            layoutParams.height = (int) style.height;
            sticky_index_wrapper.setLayoutParams(layoutParams);
        }
    }

    private void renderStickyList(RowStyle styles) {
        index_list.setLayoutManager(new LinearLayoutManager(getContext()));
        index_list.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
        adapterSticky = new StickyIndexAdapter(new char[]{}, styles);
        index_list.setAdapter(adapterSticky);
        stickyStickyIndex = new StickyIndexLayoutManager(this, index_list);
    }

    private void renderStickyWrapper(RowStyle styles) {
        ViewGroup.LayoutParams layoutParams = sticky_index_wrapper.getLayoutParams();
        layoutParams.width = (int) styles.width;
        sticky_index_wrapper.setLayoutParams(layoutParams);
        invalidate();
    }

    public void refresh(char[] dataSet) {
        adapterSticky.refresh(dataSet);
    }

    public void bindRecyclerView(final RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                stickyStickyIndex.update(rv, dy);
            }
        });
    }
}
