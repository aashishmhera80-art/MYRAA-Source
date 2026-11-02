package androidx.appcompat.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.R;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes.dex */
public class AlertDialogLayout extends LinearLayoutCompat {
    public AlertDialogLayout(Context context) {
        super(context);
    }

    public AlertDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!tryOnMeasure(widthMeasureSpec, heightMeasureSpec)) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean tryOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childHeightSpec;
        View topPanel = null;
        View buttonPanel = null;
        View middlePanel = null;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int id = child.getId();
                if (id == R.id.topPanel) {
                    topPanel = child;
                } else if (id == R.id.buttonPanel) {
                    buttonPanel = child;
                } else {
                    if ((id != R.id.contentPanel && id != R.id.customPanel) || middlePanel != null) {
                        return false;
                    }
                    middlePanel = child;
                }
            }
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int childState = 0;
        int usedHeight = getPaddingTop() + getPaddingBottom();
        if (topPanel != null) {
            topPanel.measure(widthMeasureSpec, 0);
            usedHeight += topPanel.getMeasuredHeight();
            childState = View.combineMeasuredStates(0, topPanel.getMeasuredState());
        }
        int buttonHeight = 0;
        int buttonWantsHeight = 0;
        if (buttonPanel != null) {
            buttonPanel.measure(widthMeasureSpec, 0);
            buttonHeight = resolveMinimumHeight(buttonPanel);
            buttonWantsHeight = buttonPanel.getMeasuredHeight() - buttonHeight;
            usedHeight += buttonHeight;
            childState = View.combineMeasuredStates(childState, buttonPanel.getMeasuredState());
        }
        int middleHeight = 0;
        if (middlePanel != null) {
            if (heightMode == 0) {
                childHeightSpec = 0;
            } else {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, heightSize - usedHeight), heightMode);
            }
            middlePanel.measure(widthMeasureSpec, childHeightSpec);
            middleHeight = middlePanel.getMeasuredHeight();
            usedHeight += middleHeight;
            childState = View.combineMeasuredStates(childState, middlePanel.getMeasuredState());
        }
        int remainingHeight = heightSize - usedHeight;
        if (buttonPanel != null) {
            int usedHeight2 = usedHeight - buttonHeight;
            int heightToGive = Math.min(remainingHeight, buttonWantsHeight);
            if (heightToGive > 0) {
                remainingHeight -= heightToGive;
                buttonHeight += heightToGive;
            }
            int remainingHeight2 = remainingHeight;
            int childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(buttonHeight, 1073741824);
            buttonPanel.measure(widthMeasureSpec, childHeightSpec2);
            usedHeight = usedHeight2 + buttonPanel.getMeasuredHeight();
            childState = View.combineMeasuredStates(childState, buttonPanel.getMeasuredState());
            remainingHeight = remainingHeight2;
        }
        if (middlePanel != null && remainingHeight > 0) {
            int heightToGive2 = remainingHeight;
            int remainingHeight3 = remainingHeight - heightToGive2;
            int childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(middleHeight + heightToGive2, heightMode);
            middlePanel.measure(widthMeasureSpec, childHeightSpec3);
            usedHeight = (usedHeight - middleHeight) + middlePanel.getMeasuredHeight();
            int childHeightSpec4 = middlePanel.getMeasuredState();
            childState = View.combineMeasuredStates(childState, childHeightSpec4);
            remainingHeight = remainingHeight3;
        }
        int maxWidth = 0;
        int remainingHeight4 = 0;
        while (remainingHeight4 < count) {
            View child2 = getChildAt(remainingHeight4);
            int i2 = remainingHeight4;
            int i3 = child2.getVisibility();
            View buttonPanel2 = buttonPanel;
            if (i3 != 8) {
                maxWidth = Math.max(maxWidth, child2.getMeasuredWidth());
            }
            remainingHeight4 = i2 + 1;
            buttonPanel = buttonPanel2;
        }
        int i4 = getPaddingLeft();
        int widthSizeAndState = View.resolveSizeAndState(maxWidth + i4 + getPaddingRight(), widthMeasureSpec, childState);
        int heightSizeAndState = View.resolveSizeAndState(usedHeight, heightMeasureSpec, 0);
        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
        if (widthMode != 1073741824) {
            forceUniformWidth(count, heightMeasureSpec);
            return true;
        }
        return true;
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int heightMeasureSpec2;
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        int i = 0;
        while (i < count) {
            View child = getChildAt(i);
            if (child.getVisibility() == 8) {
                heightMeasureSpec2 = heightMeasureSpec;
            } else {
                LinearLayoutCompat.LayoutParams lp = (LinearLayoutCompat.LayoutParams) child.getLayoutParams();
                if (lp.width != -1) {
                    heightMeasureSpec2 = heightMeasureSpec;
                } else {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    heightMeasureSpec2 = heightMeasureSpec;
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec2, 0);
                    lp.height = oldHeight;
                }
            }
            i++;
            heightMeasureSpec = heightMeasureSpec2;
        }
    }

    private static int resolveMinimumHeight(View v) {
        int minHeight = ViewCompat.getMinimumHeight(v);
        if (minHeight > 0) {
            return minHeight;
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            if (vg.getChildCount() == 1) {
                return resolveMinimumHeight(vg.getChildAt(0));
            }
        }
        return 0;
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childTop;
        int paddingLeft;
        int childLeft;
        int childTop2;
        AlertDialogLayout alertDialogLayout = this;
        int paddingLeft2 = alertDialogLayout.getPaddingLeft();
        int width = right - left;
        int childRight = width - alertDialogLayout.getPaddingRight();
        int childSpace = (width - paddingLeft2) - alertDialogLayout.getPaddingRight();
        int totalLength = alertDialogLayout.getMeasuredHeight();
        int count = alertDialogLayout.getChildCount();
        int gravity = alertDialogLayout.getGravity();
        int majorGravity = gravity & 112;
        int minorGravity = gravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        switch (majorGravity) {
            case 16:
                int childTop3 = alertDialogLayout.getPaddingTop();
                childTop = childTop3 + (((bottom - top) - totalLength) / 2);
                break;
            case 80:
                int childTop4 = alertDialogLayout.getPaddingTop();
                childTop = ((childTop4 + bottom) - top) - totalLength;
                break;
            default:
                childTop = alertDialogLayout.getPaddingTop();
                break;
        }
        Drawable dividerDrawable = alertDialogLayout.getDividerDrawable();
        int dividerHeight = dividerDrawable == null ? 0 : dividerDrawable.getIntrinsicHeight();
        int i = 0;
        while (i < count) {
            int childTop5 = childTop;
            View child = alertDialogLayout.getChildAt(i);
            if (child == null || child.getVisibility() == 8) {
                paddingLeft = paddingLeft2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                paddingLeft = paddingLeft2;
                LinearLayoutCompat.LayoutParams lp = (LinearLayoutCompat.LayoutParams) child.getLayoutParams();
                int layoutGravity = lp.gravity;
                if (layoutGravity < 0) {
                    layoutGravity = minorGravity;
                }
                int layoutDirection = alertDialogLayout.getLayoutDirection();
                int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutGravity, layoutDirection);
                switch (absoluteGravity & 7) {
                    case 1:
                        childLeft = ((paddingLeft + ((childSpace - childWidth) / 2)) + lp.leftMargin) - lp.rightMargin;
                        break;
                    case 5:
                        int childLeft2 = childRight - childWidth;
                        childLeft = childLeft2 - lp.rightMargin;
                        break;
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }
                if (!alertDialogLayout.hasDividerBeforeChildAt(i)) {
                    childTop2 = childTop5;
                } else {
                    childTop2 = childTop5 + dividerHeight;
                }
                int childTop6 = lp.topMargin;
                int childTop7 = childTop6 + childTop2;
                int i2 = childLeft;
                alertDialogLayout.setChildFrame(child, i2, childTop7, childWidth, childHeight);
                childTop5 = childTop7 + lp.bottomMargin + childHeight;
            }
            childTop = childTop5;
            i++;
            alertDialogLayout = this;
            paddingLeft2 = paddingLeft;
            width = width;
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }
}
