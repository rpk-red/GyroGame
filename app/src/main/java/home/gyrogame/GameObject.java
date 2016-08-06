package home.gyrogame;

import android.graphics.Rect;

public abstract class GameObject {
    protected int x; // position of object on x axis
    protected int y; // position of object on y axis
    protected int dx; // movement of object on x axis
    protected int dy; // movment of object on y axis
    protected int width; // width of object
    protected int height; // height of object

    //Setter's
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setDx(int dx) {
        this.dx = dx;
    }
    public void setDy(int dy) {this.dy = dy;}
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    // Getter's
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getDx() {
        return dx;
    }
    public int getDy() {
        return dy;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Rect getRectangle(){
        return new Rect(x, y, x+width, y+height);
    } // used for collision
}


