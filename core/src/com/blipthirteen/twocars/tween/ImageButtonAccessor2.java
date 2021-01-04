package com.blipthirteen.twocars.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.blipthirteen.twocars.misc.Constants;

import aurelienribon.tweenengine.TweenAccessor;

public class ImageButtonAccessor2 implements TweenAccessor<ImageButton> {
    public static final int POSITION_XY = 1;
    public static final int SCALE_XY = 2;

    @Override
    public int getValues(ImageButton target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;

            case SCALE_XY:

                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;

            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(ImageButton target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_XY: target.setPosition(newValues[0], newValues[1]); break;
            case SCALE_XY: target.setScale(newValues[0], newValues[1]);
                        break;
            default: assert false;
        }
    }
}
