package com.blipthirteen.twocars.objects;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Car {
	
	private Sprite sprite;
	private Rectangle boundingBox;
	private String id;
	private float targetPositionX;
	
	public Car(String id, float x, float y, Texture texture) {
		this.boundingBox = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
		this.sprite = new Sprite(texture);
		this.sprite.setPosition(x, y);
	}
	
	public float getTargetPositionX() {
		return targetPositionX;
	}

	public void setTargetPositionX(float targetPositionX) {
		this.targetPositionX = targetPositionX;
	}
	
	public Rectangle getBoundingBox() {
		return this.boundingBox;
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public void setPosition(float x, float y) {
		this.boundingBox.setPosition(x, y);
		this.sprite.setPosition(x, y);
	}	
	
	public Vector2 getPosition() {
		return new Vector2(getXPosition(), getYPosition());
	}
	
	public float getXPosition() {
		return this.boundingBox.x;
	}
	
	public float getYPosition() {
		return this.boundingBox.y;
	}
	
	public void draw(SpriteBatch batch) {
		this.sprite.setPosition(getXPosition(), getYPosition());
		this.sprite.draw(batch);
    }
	
	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.box(this.boundingBox.x, this.boundingBox.y, 0, this.boundingBox.getWidth(), this.boundingBox.getHeight(), 0);
	}
	
	public void setSize(float width, float height) {
		this.sprite.setSize(width, height);
		this.boundingBox.setSize(width, height);
	}
	
	public void update() {
		
	}
	
	public void updatePositionX(float x, float delta) {
		this.boundingBox.x = x;
	}
	
	public void updateAndDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		update();
		draw(batch);
	}
	
}
