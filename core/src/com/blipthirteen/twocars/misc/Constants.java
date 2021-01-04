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
	
	public static final float FIRST_DOT = 3400;
	public static final float VERTICAL_DISTANCE_BW_DOTS = 680;
	
	public static final float GROWING_RADIUS_SPEED = 2200;
	
	public static final String GAMEOVER_MSG = "tap to continue";
	public static final String PREF_NAME = "com.blipthirteen.twocars.pref";
	public static final String GS_PACK = "gs.pack";
	public static final String TEXTURE_PACK = "pack/twocars2.pack";
	public static final String FONT = "RF.TTF";

	public static int bannerHeight = 0;
	public static final String LEADERBOARD_ID = "CgkIvNrH1u4NEAIQBQ";
	public static final String ACH_1 = "CgkIvNrH1u4NEAIQBg";
	public static final String ACH_2 = "CgkIvNrH1u4NEAIQBw";
	public static final String ACH_3 = "CgkIvNrH1u4NEAIQCA";
	public static final String ACH_4 = "CgkIvNrH1u4NEAIQCQ";
	public static final String ACH_5 = "CgkIvNrH1u4NEAIQCg";
	public static final String ACH_6 = "CgkIvNrH1u4NEAIQCw";
	public static final String ACH_7 = "CgkIvNrH1u4NEAIQDA";
	public static final String ACH_8 = "CgkIvNrH1u4NEAIQDQ";
	public static final String ACH_9 = "CgkIvNrH1u4NEAIQDg";
	public static final String ACH_10 = "CgkIvNrH1u4NEAIQDw";
}