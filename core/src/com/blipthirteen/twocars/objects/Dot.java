package com.blipthirteen.twocars.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Dot {
	private Vector2 position;
	private String color;
	private Sprite sprite;
	private Circle cirle;
	private boolean draw;
	private boolean enabled;
	
	public Dot(Vector2 position, String color, Texture texture, int size) {
		this.position = position;
		this.color = color;
		this.sprite = new Sprite(texture);
		this.sprite.setSize(size, size);
		this.sprite.setPosition(position.x, position.y);
		this.cirle = new Circle(position, size/2);
		this.draw = true;
		this.enabled = true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	public String getColor() {
		return this.color;
	}
	
	public Circle getBoundingCircle() {
		return this.cirle;
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public void setPosition(float x, float y) {
		this.cirle.setPosition(x, y);
		this.sprite.setPosition(x, y);
	}	
	
	public Vector2 getPosition() {
		return new Vector2(getXPosition(), getYPosition());
	}
	
	public float getXPosition() {
		return this.cirle.x;
	}
	
	public float getYPosition() {
		return this.cirle.y;
	}
	
	public void draw(SpriteBatch batch) {
		this.sprite.setPosition(getXPosition() - sprite.getWidth()/2, getYPosition() - sprite.getHeight()/2);
		this.sprite.draw(batch);
    }
	
	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.circle(this.cirle.x, this.cirle.y, this.cirle.radius);
	}
	
	public void setSize(float radius) {
		this.sprite.setSize(radius, radius);
		this.cirle.setRadius(radius);
	}
	
	public void update() {
		
	}
	
	public void updatePositionX(float x, float delta) {
		this.cirle.x = x;
	}
	
	public void updatePositionY(float y, float delta) {
		this.cirle.y = y;
	}
	
	public void updateAndDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		update();
		draw(batch);
	}
}
