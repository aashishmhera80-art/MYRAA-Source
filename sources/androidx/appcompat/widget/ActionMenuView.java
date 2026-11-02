package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;

/* JADX INFO: loaded from: classes.dex */
public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        this.mPresenter.setMenuView(this);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean wasFormatted = this.mFormatItems;
        this.mFormatItems = View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
        if (wasFormatted != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.mFormatItems && this.mMenu != null && widthSize != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = widthSize;
            this.mMenu.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.rightMargin = 0;
            lp.leftMargin = 0;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARN: Code duplicated, block: B:136:0x0296  */
    /* JADX WARN: Code duplicated, block: B:138:0x0299  */
    /* JADX WARN: Code duplicated, block: B:140:0x02a7  */
    /* JADX WARN: Code duplicated, block: B:141:0x02aa  */
    /* JADX WARN: Code duplicated, block: B:146:0x02c6  */
    /* JADX WARN: Code duplicated, block: B:147:0x02c8  */
    /* JADX WARN: Type inference failed for: r4v29 */
    /* JADX WARN: Type inference failed for: r4v30, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r4v45 */
    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        boolean needsExpansion;
        boolean needsExpansion2;
        int heightSize;
        int i;
        View child;
        LayoutParams lp;
        int extraPixels;
        ?? r4;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, -2);
        int widthSize2 = widthSize - widthPadding;
        int cellCount = widthSize2 / this.mMinCellSize;
        int cellSizeRemaining = widthSize2 % this.mMinCellSize;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize2, 0);
            return;
        }
        int cellSize = this.mMinCellSize + (cellSizeRemaining / cellCount);
        int cellsRemaining = cellCount;
        int maxChildHeight = 0;
        int maxCellsUsed = 0;
        int expandableItemCount = 0;
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        int visibleItemCount = 0;
        int widthPadding2 = 0;
        while (widthPadding2 < childCount) {
            View child2 = getChildAt(widthPadding2);
            int i2 = widthPadding2;
            int i3 = child2.getVisibility();
            int cellCount2 = cellCount;
            if (i3 != 8) {
                boolean isGeneratedItem = child2 instanceof ActionMenuItemView;
                int visibleItemCount2 = visibleItemCount + 1;
                if (isGeneratedItem) {
                    int i4 = this.mGeneratedItemPadding;
                    int visibleItemCount3 = this.mGeneratedItemPadding;
                    r4 = 0;
                    child2.setPadding(i4, 0, visibleItemCount3, 0);
                } else {
                    r4 = 0;
                }
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                lp2.expanded = r4;
                lp2.extraPixels = r4;
                lp2.cellsUsed = r4;
                lp2.expandable = r4;
                lp2.leftMargin = r4;
                lp2.rightMargin = r4;
                lp2.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child2).hasText();
                int cellsAvailable = lp2.isOverflowButton ? 1 : cellsRemaining;
                int cellsUsed = measureChildForCells(child2, cellSize, cellsAvailable, itemHeightSpec, heightPadding);
                maxCellsUsed = Math.max(maxCellsUsed, cellsUsed);
                if (lp2.expandable) {
                    expandableItemCount++;
                }
                if (lp2.isOverflowButton) {
                    hasOverflow = true;
                }
                cellsRemaining -= cellsUsed;
                int maxChildHeight2 = Math.max(maxChildHeight, child2.getMeasuredHeight());
                if (cellsUsed == 1) {
                    smallestItemsAt |= (long) (1 << i2);
                    visibleItemCount = visibleItemCount2;
                    maxChildHeight = maxChildHeight2;
                } else {
                    visibleItemCount = visibleItemCount2;
                    maxChildHeight = maxChildHeight2;
                }
            }
            widthPadding2 = i2 + 1;
            cellCount = cellCount2;
        }
        int i5 = 2;
        boolean centerSingleExpandedItem = hasOverflow && visibleItemCount == 2;
        boolean needsExpansion3 = false;
        while (true) {
            if (expandableItemCount <= 0 || cellsRemaining <= 0) {
                needsExpansion = needsExpansion3;
                break;
            }
            long minCellsAt = 0;
            int i6 = i5;
            int minCells = Integer.MAX_VALUE;
            int minCells2 = heightPadding;
            int minCellsItemCount = 0;
            boolean centerSingleExpandedItem2 = centerSingleExpandedItem;
            int i7 = 0;
            while (i7 < childCount) {
                int i8 = i7;
                LayoutParams lp3 = (LayoutParams) getChildAt(i7).getLayoutParams();
                boolean needsExpansion4 = needsExpansion3;
                boolean needsExpansion5 = lp3.expandable;
                if (needsExpansion5) {
                    if (lp3.cellsUsed < minCells) {
                        minCells = lp3.cellsUsed;
                        minCellsAt = 1 << i8;
                        minCellsItemCount = 1;
                    } else if (lp3.cellsUsed == minCells) {
                        minCellsAt |= 1 << i8;
                        minCellsItemCount++;
                    }
                }
                i7 = i8 + 1;
                needsExpansion3 = needsExpansion4;
            }
            needsExpansion = needsExpansion3;
            smallestItemsAt |= minCellsAt;
            if (minCellsItemCount > cellsRemaining) {
                break;
            }
            int minCells3 = minCells + 1;
            int i9 = 0;
            while (i9 < childCount) {
                View child3 = getChildAt(i9);
                int minCellsItemCount2 = minCellsItemCount;
                LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                int i10 = i9;
                int heightMode2 = heightMode;
                int widthSize3 = widthSize2;
                if ((minCellsAt & ((long) (1 << i10))) == 0) {
                    if (lp4.cellsUsed == minCells3) {
                        smallestItemsAt |= (long) (1 << i10);
                    }
                } else {
                    if (centerSingleExpandedItem2 && lp4.preventEdgeOffset && cellsRemaining == 1) {
                        child3.setPadding(this.mGeneratedItemPadding + cellSize, 0, this.mGeneratedItemPadding, 0);
                    }
                    lp4.cellsUsed++;
                    lp4.expanded = true;
                    cellsRemaining--;
                }
                i9 = i10 + 1;
                minCellsItemCount = minCellsItemCount2;
                heightMode = heightMode2;
                widthSize2 = widthSize3;
            }
            needsExpansion3 = true;
            heightPadding = minCells2;
            centerSingleExpandedItem = centerSingleExpandedItem2;
            i5 = i6;
        }
        boolean singleItem = !hasOverflow && visibleItemCount == 1;
        if (cellsRemaining > 0 && smallestItemsAt != 0) {
            if (cellsRemaining < visibleItemCount - 1 || singleItem || maxCellsUsed > 1) {
                float expandCount = Long.bitCount(smallestItemsAt);
                if (singleItem) {
                    extraPixels = 0;
                } else {
                    if ((smallestItemsAt & 1) == 0) {
                        extraPixels = 0;
                    } else {
                        extraPixels = 0;
                        if (!((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                            expandCount -= 0.5f;
                        }
                    }
                    if ((smallestItemsAt & ((long) (1 << (childCount - 1)))) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                        expandCount -= 0.5f;
                    }
                }
                if (expandCount > 0.0f) {
                    extraPixels = (int) ((cellsRemaining * cellSize) / expandCount);
                }
                int i11 = 0;
                needsExpansion2 = needsExpansion;
                while (i11 < childCount) {
                    boolean singleItem2 = singleItem;
                    float expandCount2 = expandCount;
                    if ((smallestItemsAt & ((long) (1 << i11))) != 0) {
                        View child4 = getChildAt(i11);
                        LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                        if (child4 instanceof ActionMenuItemView) {
                            lp5.extraPixels = extraPixels;
                            lp5.expanded = true;
                            if (i11 == 0 && !lp5.preventEdgeOffset) {
                                lp5.leftMargin = (-extraPixels) / 2;
                            }
                            needsExpansion2 = true;
                        } else if (lp5.isOverflowButton) {
                            lp5.extraPixels = extraPixels;
                            lp5.expanded = true;
                            lp5.rightMargin = (-extraPixels) / 2;
                            needsExpansion2 = true;
                        } else {
                            if (i11 != 0) {
                                lp5.leftMargin = extraPixels / 2;
                            }
                            if (i11 != childCount - 1) {
                                lp5.rightMargin = extraPixels / 2;
                            }
                        }
                    }
                    i11++;
                    singleItem = singleItem2;
                    expandCount = expandCount2;
                }
            }
            if (needsExpansion2) {
                for (i = 0; i < childCount; i++) {
                    child = getChildAt(i);
                    lp = (LayoutParams) child.getLayoutParams();
                    if (!lp.expanded) {
                        int width = (lp.cellsUsed * cellSize) + lp.extraPixels;
                        int i12 = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
                        child.measure(i12, itemHeightSpec);
                    }
                }
            }
            if (heightMode != 1073741824) {
                heightSize = heightSize2;
            } else {
                heightSize = maxChildHeight;
            }
            setMeasuredDimension(widthSize2, heightSize);
        }
        needsExpansion2 = needsExpansion;
        if (needsExpansion2) {
            while (i < childCount) {
                child = getChildAt(i);
                lp = (LayoutParams) child.getLayoutParams();
                if (!lp.expanded) {
                    int width2 = (lp.cellsUsed * cellSize) + lp.extraPixels;
                    int i13 = View.MeasureSpec.makeMeasureSpec(width2, 1073741824);
                    child.measure(i13, itemHeightSpec);
                }
            }
        }
        if (heightMode != 1073741824) {
            heightSize = heightSize2;
        } else {
            heightSize = maxChildHeight;
        }
        setMeasuredDimension(widthSize2, heightSize);
    }

    static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding;
        int childHeightMode = View.MeasureSpec.getMode(parentHeightMeasureSpec);
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode);
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean expandable = false;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integer.MIN_VALUE);
            child.measure(childWidthSpec, childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        if (!lp.isOverflowButton && hasText) {
            expandable = true;
        }
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        int targetWidth = cellsUsed * cellSize;
        child.measure(View.MeasureSpec.makeMeasureSpec(targetWidth, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        int spacerSize;
        int r;
        int l;
        ActionMenuView actionMenuView = this;
        if (!actionMenuView.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = actionMenuView.getChildCount();
        int midVertical = (bottom - top) / 2;
        int dividerWidth = actionMenuView.getDividerWidth();
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - actionMenuView.getPaddingRight()) - actionMenuView.getPaddingLeft();
        int i2 = 0;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(actionMenuView);
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= childCount) {
                break;
            }
            View v = actionMenuView.getChildAt(i3);
            if (v.getVisibility() != 8) {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    int overflowWidth = v.getMeasuredWidth();
                    if (actionMenuView.hasSupportDividerBeforeChildAt(i3)) {
                        overflowWidth += dividerWidth;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl) {
                        l = actionMenuView.getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth;
                    } else {
                        r = (actionMenuView.getWidth() - actionMenuView.getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth;
                    }
                    int t = midVertical - (height / 2);
                    int midVertical2 = t + height;
                    v.layout(l, t, r, midVertical2);
                    widthRemaining -= overflowWidth;
                    i2 = 1;
                } else {
                    int midVertical3 = v.getMeasuredWidth();
                    int size = midVertical3 + p.leftMargin + p.rightMargin;
                    widthRemaining -= size;
                    actionMenuView.hasSupportDividerBeforeChildAt(i3);
                    nonOverflowCount++;
                }
            }
            i3++;
            midVertical = midVertical;
        }
        int midVertical4 = midVertical;
        if (childCount == 1 && i2 == 0) {
            View v2 = actionMenuView.getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int midHorizontal = (right - left) / 2;
            int l2 = midHorizontal - (width / 2);
            int t2 = midVertical4 - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (i2 ^ 1);
        int spacerSize2 = Math.max(0, spacerCount > 0 ? widthRemaining / spacerCount : 0);
        if (isLayoutRtl) {
            int startRight = actionMenuView.getWidth() - actionMenuView.getPaddingRight();
            int i4 = 0;
            while (i4 < childCount) {
                View v3 = actionMenuView.getChildAt(i4);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() == i) {
                    spacerSize = spacerSize2;
                } else if (lp.isOverflowButton) {
                    spacerSize = spacerSize2;
                } else {
                    int startRight2 = startRight - lp.rightMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical4 - (height3 / 2);
                    spacerSize = spacerSize2;
                    v3.layout(startRight2 - width2, t3, startRight2, t3 + height3);
                    startRight = startRight2 - ((lp.leftMargin + width2) + spacerSize);
                }
                i4++;
                spacerSize2 = spacerSize;
                dividerWidth = dividerWidth;
                i = 8;
            }
            return;
        }
        int startLeft = actionMenuView.getPaddingLeft();
        int i5 = 0;
        while (i5 < childCount) {
            View v4 = actionMenuView.getChildAt(i5);
            LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
            if (v4.getVisibility() != 8 && !lp2.isOverflowButton) {
                int startLeft2 = startLeft + lp2.leftMargin;
                int width3 = v4.getMeasuredWidth();
                int height4 = v4.getMeasuredHeight();
                int t4 = midVertical4 - (height4 / 2);
                v4.layout(startLeft2, t4, startLeft2 + width3, t4 + height4);
                startLeft = startLeft2 + lp2.rightMargin + width3 + spacerSize2;
            }
            i5++;
            actionMenuView = this;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(Drawable icon) {
        getMenu();
        this.mPresenter.setOverflowIcon(icon);
    }

    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutParams result;
        if (p != null) {
            if (p instanceof LayoutParams) {
                result = new LayoutParams((LayoutParams) p);
            } else {
                result = new LayoutParams(p);
            }
            if (result.gravity <= 0) {
                result.gravity = 16;
            }
            return result;
        }
        LayoutParams result2 = generateDefaultLayoutParams();
        return result2;
    }

    @Override // androidx.appcompat.widget.LinearLayoutCompat, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    @Override // androidx.appcompat.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    @Override // androidx.appcompat.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    @Override // androidx.appcompat.view.menu.MenuView
    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback());
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }

    protected boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        KeyEvent.Callback childAt = getChildAt(childIndex - 1);
        KeyEvent.Callback childAt2 = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childAt).needsDividerAfter();
        }
        if (childIndex > 0 && (childAt2 instanceof ActionMenuChildView)) {
            return result | ((ActionMenuChildView) childAt2).needsDividerBefore();
        }
        return result;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }

    private class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    private static class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        @Override // androidx.appcompat.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override // androidx.appcompat.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {

        @ViewDebug.ExportedProperty
        public int cellsUsed;

        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;

        @ViewDebug.ExportedProperty
        public int extraPixels;

        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;

        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((ViewGroup.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        LayoutParams(int width, int height, boolean isOverflowButton) {
            super(width, height);
            this.isOverflowButton = isOverflowButton;
        }
    }
}
