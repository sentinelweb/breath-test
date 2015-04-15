package co.uk.sentinelweb.pyst.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An image view that forces itself square
 */
public class SquareImageView extends ImageView{

    public SquareImageView(final Context context)
    {
        super(context);
    }

    public SquareImageView(final Context context, final AttributeSet attrs){
        super(context, attrs);
    }

    public SquareImageView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec){
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh){
        super.onSizeChanged(w, w, oldw, oldh);
    }
}