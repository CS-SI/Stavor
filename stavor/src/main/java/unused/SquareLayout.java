package unused;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SquareLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int size;
	    if(widthMode == MeasureSpec.EXACTLY && widthSize > 0){
	        size = widthSize;
	    }
	    else if(heightMode == MeasureSpec.EXACTLY && heightSize > 0){
	        size = heightSize;
	    }
	    else{
	        size = widthSize < heightSize ? widthSize : heightSize;
	    }

	    int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
	    super.onMeasure(finalMeasureSpec, finalMeasureSpec);
	}

}
