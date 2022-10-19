package com.example.myflaylayout;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlayLayout extends ViewGroup {

        private int mHorizontalSpacing = dp2px(16); //每个item横向间距
        private int mVerticalSpacing = dp2px(8); //每个item横向间距

        //  记录所有的行
        private List<List<View>> allLines = new ArrayList<>();
        //  记录所有的行高
        private List<Integer> lineHeights = new ArrayList<>();


        public FlayLayout(Context context) {
            super(context);
        }

        public FlayLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FlayLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        /**
         * 由于onMeasure可能会被调用多次，所以我们需要不断的清空重新添加
         */
        private void clearMeasureParams() {
            //不断创建回收会造成内存抖动，clear即可
            allLines.clear();
            lineHeights.clear();
        }

        /**
         *      度量---大部分是先测量孩子再测量自己。孩子的大小可能是一直在变的，父布局随之改变
         *      只有ViewPager是先测量自己再测量孩子
         *      spec 是一个参考值，不是一个具体的值
         * @param widthMeasureSpec      父布局给的。这是个递归的过程
         * @param heightMeasureSpec     父布局给的
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            clearMeasureParams();

            //  先拿到孩子的总数
            int childCount = getChildCount();

            int parentTop = getPaddingTop();
            int parentLeft = getPaddingLeft();
            int parentRight = getPaddingRight();
            int parentBottom = getPaddingBottom();

            //父布局的size，这个值有可能是我们测量子view后叠加起来的，也有可能是指定的
            int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
            int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

            //  保存一行所有的 view
            List<View> lineViews = new ArrayList<>();
            //  记录这行已使用多宽 size
            int lineWidthUsed = 0;
            //  一行的高
            int lineHeight = 0;

            //  measure过程中，子view要求的父布局宽高
            int parentNeedWidth = 0;
            int parentNeedHeight = 0;

            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);

                LayoutParams childParams = childView.getLayoutParams();
                //  将LayoutParams转为measureSpec
                /**
                 *      测量是个递归的过程，测量子View确定自身大小
                 *      getChildMeasureSpec的三个参数，第一个是父布局传过来的MeasureSpec，第二个参数是去除自身用掉的padding，第三个是子布局需要的宽度或高度
                 */
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, parentLeft + parentRight, childParams.width);
                int childHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, parentTop + parentBottom, childParams.height);

                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                //  获取子View测量的宽高
                int childMeasuredWidth = childView.getMeasuredWidth();
                int childMeasuredHeight = childView.getMeasuredHeight();

                //  需要换行
                if (childMeasuredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {

                    //  换行时确定当前需要的宽高
                    parentNeedHeight = parentNeedHeight + lineHeight + mVerticalSpacing;
                    parentNeedWidth = Math.max(parentNeedWidth, lineWidthUsed + mHorizontalSpacing);

                    //  存储每一行的数据 ！！！ 最后一行会被漏掉
                    allLines.add(lineViews);
                    lineHeights.add(lineHeight);

                    //  数据清空
                    lineViews = new ArrayList<>();
                    lineWidthUsed = 0;
                    lineHeight = 0;
                }

                lineViews.add(childView);
                lineWidthUsed = lineWidthUsed + childMeasuredWidth + mHorizontalSpacing;
                lineHeight = Math.max(lineHeight, childMeasuredHeight);

                //处理最后一行数据
                if (i == childCount - 1) {
                    allLines.add(lineViews);
                    lineHeights.add(lineHeight);
                    parentNeedHeight = parentNeedHeight + lineHeight + mVerticalSpacing;
                    parentNeedWidth = Math.max(parentNeedWidth, lineWidthUsed + mHorizontalSpacing);
                }

            }


            //  测量完孩子后再测量自己

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            //  如果父布局给的是确切的值，测量子view则变得毫无意义
            int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth : parentNeedWidth;
            int realHeight = (heightMode == MeasureSpec.EXACTLY) ? selfHeight : parentNeedHeight;
            setMeasuredDimension(realWidth, realHeight);
        }

        /**
         *      布局
         */
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

            int currentL = getPaddingLeft();
            int currentT = getPaddingTop();

            for (int i = 0; i < allLines.size(); i++) {
                List<View> lineViews = allLines.get(i);
                int lineHeight = lineHeights.get(i);
                for (int j = 0; j < lineViews.size(); j++) {
                    View view = lineViews.get(j);
                    int left = currentL;
                    int top = currentT;
                    //  此处为什么不用 int right = view.getWidth(); getWidth是调用完onLayout才有的
                    int right = left + view.getMeasuredWidth();
                    int bottom = top + view.getMeasuredHeight();
                    //  子view位置摆放
                    view.layout(left, top, right, bottom);
                    currentL = right + mHorizontalSpacing;
                }
                currentT = currentT + lineHeight + mVerticalSpacing;
                currentL = getPaddingLeft();
            }


        }

        public static int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
        }



}
