package app.sectionfastscroll.fastscroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.sectionfastscroll.R;

public class FastScroller extends LinearLayout {
    private final int BUBBLE_ANIMATION_DURATION = 250;
    private final int TRACK_SNAP_RANGE = 5;
    private float scrollHeight;
    private ObjectAnimator bubbleAnimator;
    private RecyclerView recyclerView;
    private TextView bubble;
    private View handle;

    public FastScroller(Context context) {
        this(context, null);
    }

    public FastScroller(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScroller(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.fastscroller_view, this, true);
        bubble = inflate.findViewById(R.id.bubble);
        handle = inflate.findViewById(R.id.handle);
        bubble.setVisibility(INVISIBLE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scrollHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                value = handleActionDown(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                value = handleActionUpCancel();
                break;
            case MotionEvent.ACTION_MOVE:
                value = handleActionMove(event);
                break;
            default:
                value = super.onTouchEvent(event);
        }
        return value;
    }

    private boolean handleActionDown(MotionEvent event) {
        if (event.getX() < handle.getX()) return false;
        if (bubble.getVisibility() == INVISIBLE) showBubble();
        handle.setSelected(true);
        setBubbleAndHandlePosition(event.getY());
        setRecyclerViewPosition(event.getY());
        return true;
    }

    private boolean handleActionMove(MotionEvent event) {
        setBubbleAndHandlePosition(event.getY());
        setRecyclerViewPosition(event.getY());
        return true;
    }

    private boolean handleActionUpCancel() {
        handle.setSelected(false);
        hideBubble();
        return true;
    }

    private void showBubble() {
        bubble.setVisibility(VISIBLE);
        bubbleAnimator = createBubbleAnimator(0f, 1f);
        bubbleAnimator.start();
    }

    private void hideBubble() {
        bubbleAnimator = createBubbleAnimator(1f, 0f);
        bubbleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                bubble.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bubble.setVisibility(INVISIBLE);
            }
        });
        bubbleAnimator.start();
    }

    private ObjectAnimator createBubbleAnimator(float v1, float v2) {
        return ObjectAnimator.ofFloat(bubble, "alpha", v1, v2).setDuration(BUBBLE_ANIMATION_DURATION);
    }

    private void setBubbleAndHandlePosition(Float y) {
        int bubbleHeight = bubble.getHeight();
        int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, scrollHeight - handleHeight, (y - handleHeight / 2)));
        bubble.setY(getValueInRange(0, scrollHeight - bubbleHeight - handleHeight / 2, (y - bubbleHeight)));
    }

    private float getValueInRange(float min, float max, float value) {
        return Math.min(Math.max(min, value), max);
    }

    private void setRecyclerViewPosition(Float y) {
        float targetPos = computeTargetPosition(y);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset((int) targetPos, 0);
        bubble.setText(((FastScrollerLabelPublisher) recyclerView.getAdapter()).getLabel((int) targetPos));
    }

    private float computeTargetPosition(Float y) {
        int itemCount = recyclerView.getAdapter().getItemCount();
        return getValueInRange(0, itemCount - 1, (int) (computeProportion(y) * itemCount));
    }

    private float computeProportion(Float y) {
        if (handle.getY() == 0f)
            return 0f;
        else if (handle.getY() + handle.getHeight() >= scrollHeight - TRACK_SNAP_RANGE)
            return 1.0f;
        else return y / scrollHeight;
    }

    /**
     * Binds the fast-scroller to the provided {@link RecyclerView}, so they scroll event are synchronized
     */
    public void bindRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                refreshScrollPosition();
            }
        });
    }

    private void refreshScrollPosition() {
        View firstVisibleView = recyclerView.getChildAt(0);
        int firstVisiblePosition = recyclerView.getChildAdapterPosition(firstVisibleView);
        int visibleRange = recyclerView.getChildCount();
        int lastVisiblePosition = firstVisiblePosition + visibleRange;
        int itemCount = recyclerView.getAdapter().getItemCount();
        float position = computePosition(firstVisiblePosition, lastVisiblePosition, itemCount, visibleRange);
        float proportion = position / itemCount;
        setBubbleAndHandlePosition(scrollHeight * proportion);
    }

    private float computePosition(int firstVisiblePosition, int lastVisiblePosition, int itemCount, int visibleRange) {
        return firstVisiblePosition == 0 ? 0 : (lastVisiblePosition == itemCount ? itemCount : (firstVisiblePosition / (itemCount - visibleRange) * itemCount));
    }

}
