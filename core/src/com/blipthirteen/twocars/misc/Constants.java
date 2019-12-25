package com.blipthirteen.twocars.misc;

public class Constants {
	
	public static final float CAR_SPEED_SIDEWAYS = 800;
	
	public static final float HORIZONTAL_CENTRE = 540;
	public static final float WIDTH = 1080;
	public static final float HEIGHT = 1920;
	public static final float CAR_WIDTH = 150;
	public static final float CAR_HEIGHT = 279;
	public static final float DISTANCE_FROM_SIDES = 60;
	public static final float DISTANCE_FROM_SCREEN_EDGE = 40;
	public static final float TOTAL_DISTANCE_FROM_SIDES = DISTANCE_FROM_SIDES + DISTANCE_FROM_SCREEN_EDGE;
	public static final float DISTANCE_BETWEEN_CARS = 70;
	public static final float SNAPPING_DISTANCE = 3;
	public static final float CENTRE_STRIPE_WIDTH = 20;
	
	// Four distinct positions
	public static final float CAR_LEFT_P1 = TOTAL_DISTANCE_FROM_SIDES;
	public static final float CAR_LEFT_P2 = CAR_LEFT_P1 + CAR_WIDTH + DISTANCE_BETWEEN_CARS ;
	
	public static final float CAR_RIGHT_P1 = HORIZONTAL_CENTRE + DISTANCE_FROM_SIDES + CENTRE_STRIPE_WIDTH/2;
	public static final float CAR_RIGHT_P2 = CAR_RIGHT_P1 + CAR_WIDTH + DISTANCE_BETWEEN_CARS ;
	
	// Four distinct dot positions
	public static final float DOT_LEFT_P1 = 175;
	public static final float DOT_LEFT_P2 = 397;
	
	public static final float DOT_RIGHT_P1 = 687;
	public static final float DOT_RIGHT_P2 = 909;
	
	public static final float FIRST_DOT = 2600;
	public static final float VERTICAL_DISTANCE_BW_DOTS = 600;
	
	public static final float GROWING_RADIUS_SPEED = 2200;
	
	public static final String GAMEOVER_MSG = "TAP TO CONTINUE";
	public static final String PREF_NAME = "com.blipthirteen.twocars";
	public static final String GS_PACK = "gs.pack";
	public static final String TEXTURE_PACK = "twocars.pack";
	public static final String FONT = "RF.TTF";
	
}