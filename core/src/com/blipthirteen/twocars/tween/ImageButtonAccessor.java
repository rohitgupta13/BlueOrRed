package com.blipthirteen.twocars.tween;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import aurelienribon.tweenengine.TweenAccessor;

public class ImageButtonAccessor implements TweenAccessor<ImageButton> {

	public static final int ALPHA = 0;
	public static final int MOVE_X = 1;
	public static final int MOVE_Y = 2;
	
	@Override
	public int getValues(ImageButton target, int tweenType, float[] returnValues) {
		switch(tweenType) {
		case ALPHA:
			returnValues[0] = target.getColor().a;
			return 1;
		case MOVE_X:
			returnValues[0] = target.getX();
            return 1;
		case MOVE_Y:
			returnValues[0] = target.getY();
            return 1;
		
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(ImageButton target, int tweenType, float[] newValues) {
		switch(tweenType) {
		case ALPHA:
			target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
			break;
		case MOVE_X:
            target.setX( + newValues[0]);
            break;
		case MOVE_Y:
            target.setY( + newValues[0]);
            break;    
		default:
			assert false;
		}
	}

}