package app.sectionfastscroll.model;

public class RowStyle {
    public float height,width;
    public int color;
    public float size;
    public int style;

    public RowStyle(float height, float width, int textcolor, float textsize, int textstyle) {
        this.height=height;
        this.width=width;
        this.color=textcolor;
        this.size=textsize;
        this.style=textstyle;
    }
}
